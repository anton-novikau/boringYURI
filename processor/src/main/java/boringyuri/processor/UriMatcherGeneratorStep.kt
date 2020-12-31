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
import boringyuri.processor.base.AbortProcessingException
import boringyuri.processor.base.BoringProcessingStep
import boringyuri.processor.base.ProcessingSession
import boringyuri.processor.ext.getAnnotation
import boringyuri.processor.ext.requireAnnotation
import boringyuri.processor.type.CommonTypeName.*
import com.google.common.collect.ImmutableSet
import com.google.common.collect.ImmutableSetMultimap
import com.squareup.javapoet.*
import java.util.*
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.util.ElementFilter

class UriMatcherGeneratorStep(
    session: ProcessingSession
) : BoringProcessingStep(session) {

    /**
     * A collection of the deferred elements over all processing steps
     */
    private val deferredElements = hashSetOf<Element>()

    private var matcherCodeCounter = 0

    override fun annotations(): Set<String> {
        return ImmutableSet.of(WithUriMatcher::class.java.name)
    }

    override fun process(
        elementsByAnnotation: ImmutableSetMultimap<String, Element>
    ): Set<Element> {
        val factories = ElementFilter.typesIn(
            elementsByAnnotation[WithUriMatcher::class.java.name]
        )

        // a collection of the deferred elements on the current processing step
        val deferred = hashSetOf<Element>()
        for (factory in factories) {
            val metadata = try {
                obtainFactoryMetadata(factory).also {
                    deferredElements.remove(factory)
                }
            } catch (e: AbortProcessingException) {
                throw e
            } catch (e: RuntimeException) {
                deferred.add(factory)
                deferredElements.add(factory)
                null
            }

            metadata?.let { generateUriMatcher(it, factory) }
        }

        return deferred
    }

    override fun onProcessingOver() {
        if (deferredElements.isNotEmpty()) {
            throw AbortProcessingException(
                logger,
                null,
                "For some of the Uri factories it is not possible to generate a proper UriMatcher." +
                        " Ensure all matcher codes defined correctly."
            )
        }
    }

    private fun obtainFactoryMetadata(factory: TypeElement): UriMatcherMetadata? {
        val uriFactoryAnnotation = factory.getAnnotation<UriFactory>()
        if (uriFactoryAnnotation == null) {
            logger.warn(
                factory,
                "%s can be created only for a valid @%s",
                ANDROID_URI_MATCHER.simpleName(),
                UriFactory::class.simpleName
            )
            return null // early exit
        }

        val matcherCodes = mutableMapOf<String, MatcherCodeMetadata>()
        val pathMappings = arrayListOf<Pair<String, MatcherCodeMetadata>>()
        val declaredMethods = ElementFilter.methodsIn(factory.enclosedElements)

        for (method in declaredMethods) {
            if (method.modifiers.contains(Modifier.STATIC)) {
                continue // skip static methods
            }

            val uriBuilderAnnotation = method.getAnnotation<UriBuilder>()
            val matchesToAnnotation = method.getAnnotation<MatchesTo>()
            val matcherCodeAnnotation = method.getAnnotation<MatcherCode>()
            val hasMatcherCode = matchesToAnnotation != null || matcherCodeAnnotation != null
            if (uriBuilderAnnotation == null || !hasMatcherCode) {
                if (hasMatcherCode) {
                    logger.warn(
                        method,
                        "@%s and @%s must be used only in combination with @%s",
                        MatchesTo::class.simpleName,
                        MatcherCode::class.simpleName,
                        UriBuilder::class.simpleName
                    )
                }
                continue // skip non-annotated methods
            }

            if (matchesToAnnotation != null && matcherCodeAnnotation != null) {
                logger.warn(
                    method,
                    "Used both @%1\$s and @%2\$s, but only @%1\$s will take effect",
                    MatchesTo::class.simpleName,
                    MatcherCode::class.simpleName
                )
            }

            val parameters = obtainPathParameters(method)
            val pathTemplate = obtainMatcherPathTemplate(uriBuilderAnnotation, parameters)
            if (pathTemplate.isEmpty()) {
                logger.warn(
                    method,
                    "%s path template can't be built for '%s'",
                    ANDROID_URI_MATCHER.simpleName(),
                    uriBuilderAnnotation.value
                )
            } else {
                val matcherCode = if (matchesToAnnotation != null) {
                    val fieldName = obtainMatcherCodeFieldName(matchesToAnnotation).also {
                        if (it == null) {
                            val matcherCodeName = matchesToAnnotation.value
                            logger.error(
                                method,
                                "@%s(\"%s\") contains invalid symbols for matcher code field",
                                MatchesTo::class.simpleName,
                                matcherCodeName
                            )
                        }
                    } ?: continue  // skip methods with invalid matcher code names

                    matcherCodes.getOrPut(fieldName) { createMatcherCode(fieldName) }
                } else if (matcherCodeAnnotation != null) {
                    createMatcherCode(matcherCodeAnnotation.value)
                } else null

                matcherCode?.let { pathMappings.add(pathTemplate to it) }
            }
        }

        val authority = uriFactoryAnnotation.authority
        val matcherClassName = obtainMatcherClassName(factory)
        val matcherCodeClassName = matcherClassName.nestedClass(MATCHER_CODE_NAME)

        return UriMatcherMetadata(
            matcherClassName,
            matcherCodeClassName,
            matcherCodes.values,
            authority,
            pathMappings
        )
    }

    private fun generateUriMatcher(
        metadata: UriMatcherMetadata,
        factory: TypeElement
    ) {
        val matcherContent = generateUriMatcherContent(metadata)

        writeSourceFile(metadata.matcherClassName, matcherContent, factory)
    }

    private fun obtainMatcherClassName(factory: TypeElement): ClassName {
        val withUriMatcherAnnotation = factory.requireAnnotation<WithUriMatcher>()
        val matcherName = withUriMatcherAnnotation.value

        return if (matcherName.isEmpty()) {
            val packageName = elementUtils.getPackageOf(factory).qualifiedName.toString()
            val simpleName = factory.simpleName.toString() + DEFAULT_MATCHER_SUFFIX

            ClassName.get(packageName, simpleName)
        } else {
            ClassName.bestGuess(matcherName).takeIf {
                it.packageName().isNotEmpty()
            } ?: ClassName.get(
                elementUtils.getPackageOf(factory).qualifiedName.toString(),
                matcherName
            )
        }
    }

    private fun obtainPathParameters(method: ExecutableElement): Map<String, TypeName> {
        return method.parameters.mapNotNull {
            val pathAnnotation = it.getAnnotation<Path>() ?: return@mapNotNull null

            val segmentName = pathAnnotation.value.ifEmpty {
                it.simpleName.toString()
            }
            segmentName to ClassName.get(it.asType())
        }.associate{ it }
    }

    private fun obtainMatcherPathTemplate(
        builderAnnotation: UriBuilder,
        parameters: Map<String, TypeName>
    ): String {
        return builderAnnotation.value
            .split(PATH_SEPARATOR)
            .filter { it.isNotEmpty() }
            .joinToString(separator = PATH_SEPARATOR) {
                val template = PATH_TEMPLATE_REGEX.find(it)?.run { groupValues[1] }

                if (template != null) {
                    if (isNumber(parameters[template])) WILDCARD_NUMBER else WILDCARD_ANY
                } else it
            }
    }

    private fun obtainMatcherCodeFieldName(
        matchesToAnnotation: MatchesTo,
    ): String? {
        return matchesToAnnotation.value.takeIf {
            it.matches(FIELD_NAME_REGEX)
        }?.toUpperCase(Locale.ENGLISH)
    }

    private fun generateUriMatcherContent(
        metadata: UriMatcherMetadata,
    ): TypeSpec {
        val uriMatcherContent = TypeSpec.classBuilder(metadata.matcherClassName)
            .addModifiers(Modifier.PUBLIC)
            .superclass(ANDROID_URI_MATCHER)

        val initMatcherMethod = generateInitMatcher(metadata)
        val isInitializedField = FieldSpec.builder(
            TypeName.BOOLEAN,
            "mIsInitialized",
            Modifier.PRIVATE,
            Modifier.VOLATILE
        ).initializer("\$L", false).build()
        val ensureInitializedMethod = generateEnsureInitialized(
            initMatcherMethod,
            isInitializedField
        )

        uriMatcherContent.addField(isInitializedField)
        uriMatcherContent.addMethod(
            MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addStatement("super(NO_MATCH)")
                .build()
        )

        uriMatcherContent.addMethod(generateAddUri())
        uriMatcherContent.addMethod(generateMatch(ensureInitializedMethod))
        uriMatcherContent.addMethod(ensureInitializedMethod)
        uriMatcherContent.addMethod(initMatcherMethod)

        uriMatcherContent.addType(generateMatcherCodeClass(metadata))

        return uriMatcherContent.build()
    }

    private fun generateMatcherCodeClass(metadata: UriMatcherMetadata): TypeSpec {
        val matcherCodeContent = TypeSpec.classBuilder(metadata.matcherCodeClassName)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)

        metadata.matcherCodes.forEach { (_, matcherCode) ->
            matcherCodeContent.addField(matcherCode)
        }

        matcherCodeContent.addMethod(
            MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .build()
        )

        return matcherCodeContent.build()
    }

    private fun generateInitMatcher(metadata: UriMatcherMetadata): MethodSpec {
        val method = MethodSpec.methodBuilder("initMatcher")
            .addModifiers(Modifier.PRIVATE)

        val authority = metadata.authority
        metadata.pathMappings.forEach { (path, matcherCode) ->
            if (matcherCode.field != null) {
                method.addStatement(
                    "super.addURI(\$S, \$S, \$T.\$N)",
                    authority,
                    path,
                    metadata.matcherCodeClassName,
                    matcherCode.field
                )
            } else {
                method.addStatement(
                    "super.addURI(\$S, \$S, \$L)",
                    authority,
                    path,
                    matcherCode.code
                )
            }
        }

        return method.build()
    }

    private fun generateAddUri(): MethodSpec {
        val method = MethodSpec.methodBuilder("addURI")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(OVERRIDE)
            .addParameter(STRING, "authority")
            .addParameter(STRING, "path")
            .addParameter(TypeName.INT, "code")

        method.addStatement(
            "throw new \$T(\$S)",
            UNSUPPORTED_OPERATION,
            "Adding new URIs to this matcher is not supported."
        )
        return method.build()
    }

    private fun generateMatch(ensureInitialized: MethodSpec): MethodSpec {
        val methodName = "match"
        val uriParamName = "uri"
        val method = MethodSpec.methodBuilder(methodName)
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(OVERRIDE)
            .addParameter(
                ParameterSpec.builder(
                    ANDROID_URI,
                    uriParamName
                ).addAnnotation(NON_NULL).build()
            )
            .returns(TypeName.INT)

        method.addStatement("\$N()", ensureInitialized)
        method.addCode("\n")
        method.addStatement("return super.\$L(\$L)", methodName, uriParamName)

        return method.build()
    }

    private fun generateEnsureInitialized(
        initMatcher: MethodSpec,
        isInitialized: FieldSpec
    ): MethodSpec {
        val method = MethodSpec.methodBuilder("ensureInitialized")
            .addModifiers(Modifier.PRIVATE)

        method.beginControlFlow("if (!\$N)", isInitialized)
        method.beginControlFlow("synchronized(this)")
        method.beginControlFlow("if (!\$N)", isInitialized)
        method.addStatement("\$N()", initMatcher)
        method.addStatement("\$N = \$L", isInitialized, true)
        method.endControlFlow()
        method.endControlFlow()
        method.endControlFlow()

        return method.build()
    }

    private fun createMatcherCode(code: Int) = MatcherCodeMetadata(code, null)

    private fun createMatcherCode(fieldName: String): MatcherCodeMetadata {
        val matcherCode = ++matcherCodeCounter
        val matcherCodeField = FieldSpec.builder(
            TypeName.INT,
            fieldName,
            Modifier.PUBLIC,
            Modifier.STATIC,
            Modifier.FINAL
        ).initializer("\$L", matcherCode).build()

        return MatcherCodeMetadata(matcherCode, matcherCodeField)
    }

    private fun isNumber(type: TypeName?) = when (type) {
        TypeName.BYTE, TypeName.SHORT, TypeName.INT, TypeName.LONG -> true
        else -> false
    }

    private class UriMatcherMetadata(
        val matcherClassName: ClassName,
        val matcherCodeClassName: ClassName,
        val matcherCodes: Collection<MatcherCodeMetadata>,
        val authority: String,
        val pathMappings: List<Pair<String, MatcherCodeMetadata>>
    )

    private data class MatcherCodeMetadata(
        val code: Int,
        val field: FieldSpec?
    )

    companion object {
        private const val DEFAULT_MATCHER_SUFFIX = "UriMatcher"
        private const val MATCHER_CODE_NAME = "MatcherCode"
        private const val PATH_SEPARATOR = "/"
        private const val WILDCARD_ANY = "*"
        private const val WILDCARD_NUMBER = "#"

        private val PATH_TEMPLATE_REGEX = "^\\{([a-zA-Z0-9_-]+)}$".toRegex()
        private val FIELD_NAME_REGEX = "^[a-zA-Z_][a-zA-Z0-9_]*$".toRegex()
    }
}

