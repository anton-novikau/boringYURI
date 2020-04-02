/*
 * Copyright 2020 Anton Novikau
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package boringyuri.processor

import boringyuri.api.Param
import boringyuri.api.Path
import boringyuri.api.UriBuilder
import boringyuri.api.UriFactory
import boringyuri.api.adapter.TypeAdapter
import boringyuri.api.constant.BooleanParam
import boringyuri.api.constant.DoubleParam
import boringyuri.api.constant.LongParam
import boringyuri.api.constant.StringParam
import boringyuri.processor.base.AbortProcessingException
import boringyuri.processor.base.BoringProcessingStep
import boringyuri.processor.base.ProcessingSession
import boringyuri.processor.ext.valueMirror
import boringyuri.processor.util.AnnotationHandler
import boringyuri.processor.util.CommonTypeName.*
import com.google.common.collect.ImmutableSet
import com.google.common.collect.SetMultimap
import com.squareup.javapoet.*
import java.util.*
import javax.lang.model.element.*
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.ElementFilter

class MediaUriGeneratorStep internal constructor(
    session: ProcessingSession,
    private val annotationHandler: AnnotationHandler
) : BoringProcessingStep(session) {

    override fun annotations(): Set<Class<out Annotation>> {
        return ImmutableSet.of<Class<out Annotation>>(
            UriFactory::class.java
        )
    }

    override fun process(
        elementsByAnnotation: SetMultimap<Class<out Annotation>, Element>
    ): Set<Element> {
        val annotatedContainers = ElementFilter.typesIn(
            elementsByAnnotation[UriFactory::class.java]
        )
        val deferred = hashSetOf<Element>()
        for (container in annotatedContainers) {
            if (container.kind != ElementKind.INTERFACE) {
                session.logger.warn(
                    container,
                    "@%s can only be applied to interface",
                    UriFactory::class.java.simpleName
                )
                continue
            }

            val generated = generateUriBuilderContainerImpl(container)
            if (!generated) {
                deferred.add(container)
            }
        }
        return deferred
    }

    private fun generateUriBuilderContainerImpl(containerElement: TypeElement): Boolean {
        val packageElement = session.elementUtils.getPackageOf(containerElement)
        val containerPackageName = packageElement.qualifiedName.toString()
        val containerSimpleName = containerElement.simpleName.toString() + CONTAINER_IMPL_SUFFIX
        val containerClassName = ClassName.get(containerPackageName, containerSimpleName)

        val content = generateUriBuilderContainerContent(containerClassName, containerElement)

        writeSourceFile(containerClassName, content, containerElement)

        return true
    }

    private fun generateUriBuilderContainerContent(
        containerImplName: ClassName,
        containerElement: TypeElement
    ): TypeSpec {
        val classContent = TypeSpec.classBuilder(containerImplName)
            .addSuperinterface(ClassName.get(containerElement))

        val containerAnnotation = containerElement.getAnnotation(UriFactory::class.java)
        val scheme = containerAnnotation.scheme
        val authority = containerAnnotation.authority
        val declaredMethods = ElementFilter.methodsIn(containerElement.enclosedElements)

        for (methodElement in declaredMethods) {
            val modifiers = methodElement.modifiers
            if (modifiers.contains(Modifier.STATIC)) {
                continue  // skip static methods
            }
            val uriBuilderAnnotation =
                methodElement.getAnnotation(UriBuilder::class.java)
                    ?: continue  // skip non-annotated methods

            val returnType = ClassName.get(methodElement.returnType)
            if (ANDROID_URI != returnType) {
                session.logger.warn(
                    methodElement,
                    "Uri builder method must have ${ANDROID_URI.simpleName()} return type"
                )
                continue  // skip the methods with invalid return types
            }

            val method = MethodSpec.methodBuilder(methodElement.simpleName.toString())
                .addAnnotation(OVERRIDE)
                .addAnnotations(annotationHandler.toAnnotationSpec(
                    methodElement.annotationMirrors))
                .returns(ANDROID_URI)
                .addModifiers(LinkedHashSet(modifiers).apply {
                    remove(Modifier.ABSTRACT)
                    remove(Modifier.DEFAULT)
                })

            method.addStatement(
                "$1T $2L = new $1T()\n.scheme($3S)\n.authority($4S)",
                ANDROID_URI_BUILDER,
                URI_BUILDER_NAME,
                scheme,
                authority
            )

            method.addCode("\n")
            appendBaseUriPath(uriBuilderAnnotation, method)
            appendUriBody(methodElement, method)
            appendConstantStringParams(methodElement, method)
            appendConstantLongParams(methodElement, method)
            appendConstantDoubleParams(methodElement, method)
            appendConstantBooleanParams(methodElement, method)
            method.addCode("\n")

            method.addStatement("return \$L.build()", URI_BUILDER_NAME)
            classContent.addMethod(method.build())
        }

        return classContent.build()
    }

    private fun appendBaseUriPath(uriBuilder: UriBuilder, method: MethodSpec.Builder) {
        val basePath = uriBuilder.value
        if (basePath.isEmpty()) {
            return // early exit
        }

        val pathMethodName = if (uriBuilder.encoded) "encodedPath" else "path"

        method.addStatement(
            "\$L.\$L(\$S)",
            URI_BUILDER_NAME,
            pathMethodName,
            basePath
        )
    }

    private fun appendUriBody(
        methodElement: ExecutableElement,
        method: MethodSpec.Builder
    ) {
        val parameters = methodElement.parameters
        for (paramElement in parameters) {
            val paramType = ClassName.get(paramElement.asType())
            val paramName = paramElement.simpleName.toString()
            val param = ParameterSpec.builder(paramType, paramName)
                .addModifiers(paramElement.modifiers)
                .addAnnotations(annotationHandler.toAnnotationSpec(
                    paramElement.annotationMirrors))
                .build()
            method.addParameter(param)

            val appendCodeBlock = appendPath(paramElement, paramType, param)
                ?: appendQueryParam(paramElement, paramType, param)

            if (appendCodeBlock != null) {
                method.addCode(appendCodeBlock)
            } else {
                session.logger.warn(paramElement, "parameter $paramName ignored")
            }
        }
    }

    private fun appendPath(
        paramElement: VariableElement,
        paramType: TypeName,
        param: ParameterSpec
    ): CodeBlock? {
        val pathParam = paramElement.getAnnotation(Path::class.java) ?: return null // early exit
        val typeAdapter = obtainTypeAdapter(paramElement)

        val appendPathBlock = CodeBlock.builder()
        val paramToString = buildSerializingParamBlock(paramType, param, typeAdapter)
        val nullable = annotationHandler.isNullable(paramElement.asType(), paramElement)

        if (nullable) {
            appendPathBlock.beginControlFlow("if (\$N != null)", param)
        }

        val appendMethod = if (pathParam.encoded) "appendEncodedPath" else "appendPath"
        appendPathBlock.addStatement(
            "\$L.\$L(\$L)",
            URI_BUILDER_NAME,
            appendMethod,
            paramToString
        )

        if (nullable) {
            appendPathBlock.endControlFlow()
        }

        return appendPathBlock.build()
    }

    private fun appendQueryParam(
        paramElement: VariableElement,
        paramType: TypeName,
        param: ParameterSpec
    ): CodeBlock? {
        val queryParam = paramElement.getAnnotation(Param::class.java) ?: return null // early exit
        val typeAdapter = obtainTypeAdapter(paramElement)

        val appendQueryBlock = CodeBlock.builder()
        val nullable = annotationHandler.isNullable(paramElement.asType(), paramElement)

        if (nullable) {
            appendQueryBlock.beginControlFlow("if (\$N != null)", param)
        }

        val queryParamName = queryParam.value.ifEmpty { param.name }
        val serializedParam = buildSerializingParamBlock(paramType, param, typeAdapter)
        appendQueryBlock.addStatement(
            "\$L.appendQueryParameter(\$S, \$L)",
            URI_BUILDER_NAME,
            queryParamName,
            serializedParam
        )

        if (nullable) {
            appendQueryBlock.endControlFlow()
        }

        return appendQueryBlock.build()
    }

    private fun obtainTypeAdapter(element: VariableElement): TypeMirror? {
        val adapter = element.getAnnotation(TypeAdapter::class.java)
        if (adapter != null) {
            return adapter.valueMirror()
        }

        val elementType = element.asType()

        return if (elementType is DeclaredType) {
            elementType.asElement().getAnnotation(TypeAdapter::class.java)?.valueMirror()
        } else null
    }

    private fun buildSerializingParamBlock(
        paramType: TypeName,
        param: ParameterSpec,
        typeAdapter: TypeMirror?
    ): CodeBlock = if (typeAdapter != null) {
        CodeBlock.of("new \$T().serialize(\$N)", TypeName.get(typeAdapter), param)
    } else if (STRING == paramType) {
        CodeBlock.of("\$N", param)
    } else if (paramType.isPrimitive || paramType.isBoxedPrimitive || ANDROID_URI == paramType) {
        CodeBlock.of("\$T.valueOf(\$N)", STRING, param)
    } else {
        throw AbortProcessingException("Unknown type $paramType")
    }

    private fun appendConstantStringParams(
        methodElement: ExecutableElement,
        method: MethodSpec.Builder
    ) {
        val constParams = methodElement.getAnnotationsByType(StringParam::class.java) ?: return
        for (constParam in constParams) {
            method.addStatement(
                "\$L.appendQueryParameter(\$S, \$S)",
                URI_BUILDER_NAME,
                constParam.name,
                constParam.value
            )
        }
    }

    private fun appendConstantLongParams(
        methodElement: ExecutableElement,
        method: MethodSpec.Builder
    ) {
        val constParams = methodElement.getAnnotationsByType(LongParam::class.java) ?: return
        for (constParam in constParams) {
            method.addStatement(
                "\$L.appendQueryParameter(\$S, \$T.valueOf(\$L))",
                URI_BUILDER_NAME,
                constParam.name,
                STRING,
                constParam.value
            )
        }
    }

    private fun appendConstantDoubleParams(
        methodElement: ExecutableElement,
        method: MethodSpec.Builder
    ) {
        val constParams = methodElement.getAnnotationsByType(DoubleParam::class.java) ?: return
        for (constParam in constParams) {
            method.addStatement(
                "\$L.appendQueryParameter(\$S, \$T.valueOf(\$L))",
                URI_BUILDER_NAME,
                constParam.name,
                STRING,
                constParam.value
            )
        }
    }

    private fun appendConstantBooleanParams(
        methodElement: ExecutableElement,
        method: MethodSpec.Builder
    ) {
        val constParams = methodElement.getAnnotationsByType(BooleanParam::class.java) ?: return
        for (constParam in constParams) {
            method.addStatement(
                "\$L.appendQueryParameter(\$S, \$T.valueOf(\$L))",
                URI_BUILDER_NAME,
                constParam.name,
                STRING,
                constParam.value
            )
        }
    }

    private companion object {
        const val CONTAINER_IMPL_SUFFIX = "Impl"
        const val URI_BUILDER_NAME = "builder"
    }

}