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
package boringyuri.processor.common.steps

import androidx.room.compiler.codegen.toJavaPoet
import androidx.room.compiler.processing.ExperimentalProcessingApi
import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XExecutableElement
import androidx.room.compiler.processing.XFiler
import androidx.room.compiler.processing.XMethodElement
import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XTypeElement
import androidx.room.compiler.processing.XVariableElement
import androidx.room.compiler.processing.addOriginatingElement
import androidx.room.compiler.processing.isMethod
import androidx.room.compiler.processing.isTypeElement
import boringyuri.api.DefaultValue
import boringyuri.api.Param
import boringyuri.api.Path
import boringyuri.api.UriBuilder
import boringyuri.api.UriFactory
import boringyuri.api.constant.BooleanParam
import boringyuri.api.constant.DoubleParam
import boringyuri.api.constant.LongParam
import boringyuri.api.constant.StringParam
import boringyuri.processor.common.steps.ProcessorOptions.getTypeAdapterFactory
import boringyuri.processor.common.base.BoringProcessingStep
import boringyuri.processor.common.base.ProcessingSession
import boringyuri.processor.common.ext.getAnnotation
import boringyuri.processor.common.ext.getAnnotations
import boringyuri.processor.common.ext.requireAnnotation
import boringyuri.processor.common.steps.ext.createModifiers
import boringyuri.processor.common.steps.ext.createParamSpec
import boringyuri.processor.common.steps.type.CommonTypeName.ANDROID_URI
import boringyuri.processor.common.steps.type.CommonTypeName.ANDROID_URI_BUILDER
import boringyuri.processor.common.steps.type.CommonTypeName.OVERRIDE
import boringyuri.processor.common.steps.type.CommonTypeName.STRING
import boringyuri.processor.common.steps.type.TypeConverter
import boringyuri.processor.common.steps.uripart.ConstantPathSegment
import boringyuri.processor.common.steps.uripart.PathSegment
import boringyuri.processor.common.steps.uripart.QueryParameter
import boringyuri.processor.common.steps.uripart.VariableWritePathSegment
import boringyuri.processor.common.steps.uripart.VariableWriteQueryParameter
import boringyuri.processor.common.steps.util.AnnotationHandler
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeSpec
import com.squareup.kotlinpoet.javapoet.KotlinPoetJavaPoetPreview
import javax.lang.model.element.Modifier

@OptIn(ExperimentalProcessingApi::class, KotlinPoetJavaPoetPreview::class)
class UriFactoryGeneratorStep(
    session: ProcessingSession,
    private val annotationHandler: AnnotationHandler
) : BoringProcessingStep(session) {

    private val typeConverter = TypeConverter(
        logger,
        getTypeAdapterFactory(session)
    )

    override fun annotations(): Set<String> {
        return setOf(UriFactory::class.java.name)
    }

    @Deprecated(
        "We're combining processOver() and this process() overload.",
        replaceWith = ReplaceWith("process(XProcessingEnv, Map<String, Set<XElement>>, Boolean)"),
        level = DeprecationLevel.WARNING
    )
    override fun process(
        env: XProcessingEnv,
        elementsByAnnotation: Map<String, Set<XElement>>
    ): Set<XElement> {
        val annotatedContainers =
            elementsByAnnotation[UriFactory::class.java.name]
                ?.filter { it.isTypeElement() }
                ?.mapNotNull { it as? XTypeElement }
                ?: emptyList()

        val deferred = hashSetOf<XElement>()
        for (container in annotatedContainers) {
            if (!container.isInterface()) {
                logger.warn(
                    container,
                    "@%s can only be applied to an interface",
                    UriFactory::class.simpleName
                )
                continue
            }
            if (container.isPrivate()) {
                logger.warn(
                    container,
                    "@%s can not be applied to a private interface",
                    UriFactory::class.simpleName
                )
                continue
            }

            val containerMetadata = obtainContainerMetadata(container)
            val generated = generateUriBuilderContainerImpl(container, containerMetadata)
            if (!generated) {
                deferred.add(container)
            }
        }
        return deferred
    }

    private fun obtainContainerMetadata(containerElement: XTypeElement): List<BuilderMetadata> {
        val declaredMethods = containerElement.getEnclosedElements()
            .filter { it.isMethod() }
            .map { it as XMethodElement }
        val metadata = ArrayList<BuilderMetadata>(declaredMethods.size)

        for (methodElement in declaredMethods) {
            if (methodElement.isStatic()) {
                continue  // skip static methods
            }

            val builderAnnotation =
                methodElement.getAnnotation<UriBuilder>()
                    ?: continue // skip non-annotated methods

            val returnType = methodElement.returnType.typeElement?.asClassName()?.toJavaPoet()
            if (ANDROID_URI != returnType) {
                logger.warn(
                    methodElement,
                    "Uri builder method must have ${ANDROID_URI.simpleName()} return type"
                )
                continue  // skip the methods with invalid return types
            }

            val (methodParams, segments, queryParams) = obtainBuilderMetadata(
                builderAnnotation,
                methodElement
            )

            metadata.add(BuilderMetadata(methodElement, methodParams, segments, queryParams))
        }

        return metadata
    }

    private fun obtainBuilderMetadata(
        builderAnnotation: UriBuilder,
        methodElement: XExecutableElement
    ): Triple<List<ParameterSpec>, List<PathSegment>, List<QueryParameter>> {
        val methodParameters = methodElement.parameters
        val parameterSpecs = createParamSpecs(methodParameters)

        // We find all the possible variable path segments replacements defined
        // in the method parameters. On the next step we'll try to find the placeholders
        // where to apply these variable path segments.
        val variablePathSegments = obtainPathSegments(methodParameters, parameterSpecs)
        // Iterating over all constant and variable path segments we'll put them in a list
        // in the exact order as they were defined in the base path of @UriBuilder annotation.
        val pathSegments = obtainPathSegmentsFromBasePath(
            builderAnnotation,
            variablePathSegments,
            methodElement
        )

        val queryParams = obtainQueryParams(methodParameters, parameterSpecs)

        return Triple(parameterSpecs.values.toList(), pathSegments, queryParams)
    }

    private fun createParamSpecs(
        methodParameters: List<XVariableElement>
    ): Map<XVariableElement, ParameterSpec> {
        return methodParameters.associateWithTo(LinkedHashMap()) { parameter ->
            parameter.createParamSpec(annotationHandler)
        }
    }

    private fun obtainPathSegments(
        methodParameters: List<XVariableElement>,
        parameterSpecs: Map<XVariableElement, ParameterSpec>
    ): Map<String, VariableWritePathSegment> {
        return methodParameters.mapNotNull { param ->
            val pathAnnotation = param.getAnnotation<Path>() ?: return@mapNotNull null

            val spec = parameterSpecs.getValue(param)
            val nullable = annotationHandler.isNullable(spec.type, param)
            val defaultValue = param.getAnnotation<DefaultValue>()?.value

            if (nullable && defaultValue == null) {
                logger.error(
                    param, "Path segment '${spec.name}' must be explicitly non-null" +
                            " or have a @${DefaultValue::class.simpleName}."
                )
            }

            val pathName = pathAnnotation.value.ifEmpty { spec.name }
            val segment = VariableWritePathSegment(
                param,
                spec,
                defaultValue,
                pathAnnotation.encoded,
                URI_BUILDER_NAME
            )

            pathName to segment
        }.associate { it }
    }

    private fun obtainQueryParams(
        methodParameters: List<XVariableElement>,
        parameterSpecs: Map<XVariableElement, ParameterSpec>
    ): List<QueryParameter> {
        return methodParameters.mapNotNull { param ->
            val paramAnnotation = param.getAnnotation<Param>() ?: return@mapNotNull null

            val spec = parameterSpecs.getValue(param)
            val nullable = annotationHandler.isNullable(spec.type, param)
            val defaultValue = param.getAnnotation<DefaultValue>()?.value

            val paramName = paramAnnotation.value.ifEmpty { spec.name }
            VariableWriteQueryParameter(
                paramName,
                spec,
                param,
                nullable,
                defaultValue,
                URI_BUILDER_NAME
            )
        }
    }

    private fun obtainPathSegmentsFromBasePath(
        builderAnnotation: UriBuilder,
        variablePathSegments: Map<String, VariableWritePathSegment>,
        originatingElement: XElement
    ): List<PathSegment> {
        val basePath = builderAnnotation.value

        var unprocessedElementsCounter = variablePathSegments.size
        val segments = if (basePath.isNotEmpty()) {
            basePath.split("/")
                .filter { it.isNotEmpty() }
                .mapNotNullTo(ArrayList()) {
                    val template = PATH_TEMPLATE_REGEX.find(it)?.run { groupValues[1] }
                    val segment = if (template == null) {
                        ConstantPathSegment(it, builderAnnotation.encoded, URI_BUILDER_NAME)
                    } else {
                        variablePathSegments[template]?.also { unprocessedElementsCounter-- }
                    }

                    segment
                }
        } else ArrayList()

        if (unprocessedElementsCounter > 0) {
            session.logger.error(
                originatingElement,
                "Some of the @${Path::class.simpleName} annotated method parameters " +
                        "are not found in '$basePath'"
            )
        }

        return segments
    }

    private fun generateUriBuilderContainerImpl(
        containerElement: XTypeElement,
        containerMetadata: List<BuilderMetadata>
    ): Boolean {
        val packageName = containerElement.packageName
        val containerSimpleName = containerElement.name + CONTAINER_IMPL_SUFFIX
        val containerClassName = ClassName.get(packageName, containerSimpleName)

        val content = generateUriBuilderContainerContent(
            containerClassName,
            containerElement,
            containerMetadata
        )

        session.fileWriter.writeSourceFile(containerClassName, content, XFiler.Mode.Isolating)

        return true
    }

    private fun generateUriBuilderContainerContent(
        containerImplName: ClassName,
        containerElement: XTypeElement,
        containerMetadata: List<BuilderMetadata>
    ): TypeSpec {
        val classContent = TypeSpec.classBuilder(containerImplName)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addSuperinterface(containerElement.asClassName().toJavaPoet())

        val containerAnnotation = containerElement.requireAnnotation<UriFactory>()
        val scheme = containerAnnotation.scheme
        val authority = containerAnnotation.authority

        for (builderMetadata in containerMetadata) {
            val methodElement = builderMetadata.builderMethod

            val modifiers = methodElement.createModifiers()

            val method = MethodSpec.methodBuilder(methodElement.name)
                .addAnnotation(OVERRIDE)
                .addAnnotations(
                    annotationHandler.toAnnotationSpec(
                        methodElement.returnType,
                        methodElement.getAllAnnotations()
                    )
                )
                .returns(ANDROID_URI)
                .addModifiers(modifiers.toMutableSet().apply {
                    remove(Modifier.ABSTRACT)
                    remove(Modifier.DEFAULT)
                })
                .addParameters(builderMetadata.methodParameters)

            method.addStatement(
                "$1T $2L = new $1T()\n.scheme($3S)\n.authority($4S)",
                ANDROID_URI_BUILDER,
                URI_BUILDER_NAME,
                scheme,
                authority
            )

            method.addCode("\n")
            appendUriBody(builderMetadata, method)
            appendConstantStringParams(methodElement, method)
            appendConstantBooleanParams(methodElement, method)
            appendConstantLongParams(methodElement, method)
            appendConstantDoubleParams(methodElement, method)
            method.addCode("\n")

            method.addStatement("return \$L.build()", URI_BUILDER_NAME)
            classContent.addMethod(method.build())
        }

        classContent.addOriginatingElement(containerElement)

        return classContent.build()
    }

    private fun appendUriBody(
        builderMetadata: BuilderMetadata,
        method: MethodSpec.Builder
    ) {
        builderMetadata.pathSegments.forEach {
            method.addCode(it.createValueBlock(typeConverter))
        }
        builderMetadata.parameters.forEach {
            method.addCode(it.createValueBlock(typeConverter))
        }
    }

    private fun appendConstantStringParams(
        methodElement: XExecutableElement,
        method: MethodSpec.Builder
    ) {
        val constParams = methodElement.getAnnotations<StringParam>()
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
        methodElement: XExecutableElement,
        method: MethodSpec.Builder
    ) {
        val constParams = methodElement.getAnnotations<LongParam>()
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
        methodElement: XExecutableElement,
        method: MethodSpec.Builder
    ) {
        val constParams = methodElement.getAnnotations<DoubleParam>()
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
        methodElement: XExecutableElement,
        method: MethodSpec.Builder
    ) {
        val constParams = methodElement.getAnnotations<BooleanParam>()
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

    private data class BuilderMetadata(
        val builderMethod: XMethodElement,
        val methodParameters: List<ParameterSpec>,
        val pathSegments: List<PathSegment>,
        val parameters: List<QueryParameter>
    )

    companion object {
        const val CONTAINER_IMPL_SUFFIX = "Impl"
        private const val URI_BUILDER_NAME = "builder"

        private val PATH_TEMPLATE_REGEX = "^\\{([a-zA-Z0-9_-]+)}$".toRegex()

        fun create(session: ProcessingSession): UriFactoryGeneratorStep {
            return UriFactoryGeneratorStep(
                session,
                AnnotationHandler(URI_FACTORY_ANNOTATIONS)
            )
        }
    }
}