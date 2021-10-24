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

import boringyuri.api.*
import boringyuri.api.constant.BooleanParam
import boringyuri.api.constant.DoubleParam
import boringyuri.api.constant.LongParam
import boringyuri.api.constant.StringParam
import boringyuri.processor.base.BoringProcessingStep
import boringyuri.processor.base.ProcessingSession
import boringyuri.processor.ext.createParamSpec
import boringyuri.processor.ext.getAnnotation
import boringyuri.processor.ext.requireAnnotation
import boringyuri.processor.type.CommonTypeName.*
import boringyuri.processor.type.TypeConverter
import boringyuri.processor.uripart.*
import boringyuri.processor.util.AnnotationHandler
import boringyuri.processor.util.ProcessorOptions
import boringyuri.processor.util.ProcessorOptions.getTypeAdapterFactory
import com.google.common.collect.ImmutableSet
import com.google.common.collect.ImmutableSetMultimap
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeSpec
import java.util.*
import javax.lang.model.element.*
import javax.lang.model.util.ElementFilter
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap
import kotlin.collections.LinkedHashSet

class UriFactoryGeneratorStep internal constructor(
    session: ProcessingSession,
    private val annotationHandler: AnnotationHandler
) : BoringProcessingStep(session) {

    private val typeConverter = TypeConverter(
        logger,
        getTypeAdapterFactory(session)
    )

    override fun annotations(): Set<String> {
        return ImmutableSet.of(UriFactory::class.java.name)
    }

    override fun process(
        elementsByAnnotation: ImmutableSetMultimap<String, Element>
    ): Set<Element> {
        val annotatedContainers = ElementFilter.typesIn(
            elementsByAnnotation[UriFactory::class.java.name]
        )
        val deferred = hashSetOf<Element>()
        for (container in annotatedContainers) {
            if (container.kind != ElementKind.INTERFACE) {
                logger.warn(
                    container,
                    "@%s can only be applied to an interface",
                    UriFactory::class.simpleName
                )
                continue
            }
            val modifiers = container.modifiers
            if (modifiers.contains(Modifier.PRIVATE)) {
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

    private fun obtainContainerMetadata(containerElement: TypeElement): List<BuilderMetadata> {
        val declaredMethods = ElementFilter.methodsIn(containerElement.enclosedElements)
        val metadata = ArrayList<BuilderMetadata>(declaredMethods.size)

        for (methodElement in declaredMethods) {
            if (methodElement.modifiers.contains(Modifier.STATIC)) {
                continue  // skip static methods
            }

            val builderAnnotation =
                methodElement.getAnnotation<UriBuilder>() ?: continue // skip non-annotated methods

            val returnType = ClassName.get(methodElement.returnType)
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
        methodElement: ExecutableElement
    ): Triple<List<ParameterSpec>, List<PathSegment>, List<QueryParameter>> {
        val methodParameters = methodElement.parameters
        val parameterSpecs = createParamSpecs(methodParameters)

        // We find all the possible variable path segments replacements defined
        // in the method parameters. On the next step we'll try to find the placeholders
        // where to apply these variable path segments.
        val variablePathSegments = obtainPathSegments(methodParameters, parameterSpecs)
        // Iterating over all constant and variable path segments we'll put them in a list
        // in the exact order as they were defined in the base path of @UriBuilder annotation.
        val pathSegments = obtainPathSegmentsFromBasePath(builderAnnotation, variablePathSegments)

        val queryParams = obtainQueryParams(methodParameters, parameterSpecs)

        return Triple(parameterSpecs.values.toList(), pathSegments, queryParams)
    }

    private fun createParamSpecs(
        methodParameters: List<VariableElement>
    ): Map<VariableElement, ParameterSpec> {
        return methodParameters.associateWithTo(LinkedHashMap()) { parameter ->
            parameter.createParamSpec(annotationHandler)
        }
    }

    private fun obtainPathSegments(
        methodParameters: List<VariableElement>,
        parameterSpecs: Map<VariableElement, ParameterSpec>
    ): Map<String, VariableWritePathSegment> {
        return methodParameters.mapNotNull { param ->
            val pathAnnotation = param.getAnnotation<Path>() ?: return@mapNotNull null

            val spec = parameterSpecs.getValue(param)
            val nullable = annotationHandler.isNullable(spec.type, param)
            val defaultValue = param.getAnnotation<DefaultValue>()?.value

            if (nullable && defaultValue == null) {
                logger.error(param, "Path segment '${spec.name}' must be explicitly non-null" +
                        " or have a @${DefaultValue::class.simpleName}.")
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
        methodParameters: List<VariableElement>,
        parameterSpecs: Map<VariableElement, ParameterSpec>
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
        variablePathSegments: Map<String, VariableWritePathSegment>
    ): List<PathSegment> {
        val basePath = builderAnnotation.value

        val notFoundSegments = LinkedHashSet(variablePathSegments.values)
        val segments = if (basePath.isNotEmpty()) {
            basePath.split("/")
                .filter { it.isNotEmpty() }
                .mapNotNullTo(ArrayList()) {
                    val template = PATH_TEMPLATE_REGEX.find(it)?.run { groupValues[1] }
                    val segment = if (template == null) {
                        ConstantPathSegment(it, builderAnnotation.encoded, URI_BUILDER_NAME)
                    } else {
                        variablePathSegments[template]?.also { foundSegment ->
                            notFoundSegments.remove(foundSegment)
                        }
                    }

                    segment
                }
        } else ArrayList()

        notFoundSegments.forEach { segment ->
            val segmentElement = segment.segment
            val pathAnnotation = segmentElement.requireAnnotation<Path>()
            val segmentName = pathAnnotation.value.ifEmpty { segmentElement.simpleName.toString() }

            ProcessorOptions.warnOrderedSegmentsUsage(
                session,
                segmentName,
                basePath,
                UriBuilder::class,
                segmentElement
            )

            segments.add(segment)
        }

        return segments
    }

    private fun generateUriBuilderContainerImpl(
        containerElement: TypeElement,
        containerMetadata: List<BuilderMetadata>
    ): Boolean {
        val packageElement = elementUtils.getPackageOf(containerElement)
        val containerPackageName = packageElement.qualifiedName.toString()
        val containerSimpleName = containerElement.simpleName.toString() + CONTAINER_IMPL_SUFFIX
        val containerClassName = ClassName.get(containerPackageName, containerSimpleName)

        val content = generateUriBuilderContainerContent(
            containerClassName,
            containerElement,
            containerMetadata
        )

        writeSourceFile(containerClassName, content, containerElement)

        return true
    }

    private fun generateUriBuilderContainerContent(
        containerImplName: ClassName,
        containerElement: TypeElement,
        containerMetadata: List<BuilderMetadata>
    ): TypeSpec {
        val classContent = TypeSpec.classBuilder(containerImplName)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addSuperinterface(ClassName.get(containerElement))

        val containerAnnotation = containerElement.requireAnnotation<UriFactory>()
        val scheme = containerAnnotation.scheme
        val authority = containerAnnotation.authority

        for (builderMetadata in containerMetadata) {
            val methodElement = builderMetadata.builderMethod

            val modifiers = methodElement.modifiers

            val method = MethodSpec.methodBuilder(methodElement.simpleName.toString())
                .addAnnotation(OVERRIDE)
                .addAnnotations(annotationHandler.toAnnotationSpec(
                    methodElement.annotationMirrors)
                )
                .returns(ANDROID_URI)
                .addModifiers(LinkedHashSet(modifiers).apply {
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

    private data class BuilderMetadata(
        val builderMethod: ExecutableElement,
        val methodParameters: List<ParameterSpec>,
        val pathSegments: List<PathSegment>,
        val parameters: List<QueryParameter>
    )

    companion object {
        const val CONTAINER_IMPL_SUFFIX = "Impl"
        private const val URI_BUILDER_NAME = "builder"

        private val PATH_TEMPLATE_REGEX = "^\\{([a-zA-Z0-9_-]+)}$".toRegex()
    }
}