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

package boringyuri.processor.common

import androidx.room.compiler.processing.ExperimentalProcessingApi
import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XExecutableElement
import androidx.room.compiler.processing.XFiler
import androidx.room.compiler.processing.XMethodElement
import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XTypeElement
import androidx.room.compiler.processing.addOriginatingElement
import androidx.room.compiler.processing.isMethod
import androidx.room.compiler.processing.isTypeElement
import boringyuri.api.Path
import boringyuri.api.UriBuilder
import boringyuri.api.UriFactory
import boringyuri.api.matcher.MatcherCode
import boringyuri.api.matcher.MatchesTo
import boringyuri.api.matcher.WithUriMatcher
import boringyuri.processor.common.base.AbortProcessingException
import boringyuri.processor.common.base.BoringProcessingStep
import boringyuri.processor.common.base.ProcessingSession
import boringyuri.processor.common.type.CommonTypeName.ANDROID_URI
import boringyuri.processor.common.type.CommonTypeName.ANDROID_URI_MATCHER
import boringyuri.processor.common.type.CommonTypeName.NON_NULL
import boringyuri.processor.common.type.CommonTypeName.OVERRIDE
import boringyuri.processor.common.type.CommonTypeName.STRING
import boringyuri.processor.common.type.CommonTypeName.UNSUPPORTED_OPERATION
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Modifier

@OptIn(ExperimentalProcessingApi::class)
class UriMatcherGeneratorStep(
    session: ProcessingSession
) : BoringProcessingStep(session) {

    /**
     * A collection of the deferred elements over all processing steps
     */
    private val deferredElements = hashSetOf<XElement>()

    private var matcherCodeCounter = 0

    override fun annotations(): Set<String> {
        return setOf(WithUriMatcher::class.java.name)
    }

    override fun process(
        env: XProcessingEnv,
        elementsByAnnotation: Map<String, Set<XElement>>
    ): Set<XElement> {
        val factories = elementsByAnnotation[WithUriMatcher::class.java.name]
            ?.filter { it.isTypeElement() }
            ?.mapNotNull { it as? XTypeElement }
            ?: return emptySet()

        // a collection of the deferred elements on the current processing step
        val deferred = hashSetOf<XElement>()
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
                message = "For some of the Uri factories it is not possible to generate a proper UriMatcher." +
                        " Ensure all matcher codes defined correctly."
            )
        }
    }

    private fun obtainFactoryMetadata(factory: XTypeElement): UriMatcherMetadata? {
        val uriFactoryAnnotation = factory.getAnnotation(UriFactory::class)?.value
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
        val declaredMethods = factory.getEnclosedElements()
            .filter { it.isMethod() }
            .mapNotNull { it as? XMethodElement }

        for (method in declaredMethods) {
            if (method.isStatic()) {
                continue // skip static methods
            }

            val uriBuilderAnnotation = method.getAnnotation(UriBuilder::class)?.value
            val matchesToAnnotation = method.getAnnotation(MatchesTo::class)?.value
            val matcherCodeAnnotation = method.getAnnotation(MatcherCode::class)?.value
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

                    val enabled = matchesToAnnotation.enabled

                    matcherCodes.getOrPut(fieldName) {
                        createMatcherCode(fieldName, enabled)
                    }.let {
                        if (it.enabled != enabled) it.copy(enabled = enabled) else it
                    }
                } else if (matcherCodeAnnotation != null) {
                    createMatcherCode(matcherCodeAnnotation.value, matcherCodeAnnotation.enabled)
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
        factory: XTypeElement
    ) {
        val matcherContent = generateUriMatcherContent(metadata, factory)

        session.fileWriter.writeSourceFile(
            metadata.matcherClassName,
            matcherContent,
            XFiler.Mode.Isolating
        )
    }

    private fun obtainMatcherClassName(factory: XTypeElement): ClassName {
        val withUriMatcherAnnotation = factory.requireAnnotation(WithUriMatcher::class).value
        val matcherName = withUriMatcherAnnotation.value

        return if (matcherName.isEmpty()) {
            val packageName = factory.packageName
            val simpleName = factory.name + DEFAULT_MATCHER_SUFFIX

            ClassName.get(packageName, simpleName)
        } else {
            ClassName.bestGuess(matcherName).takeIf {
                it.packageName().isNotEmpty()
            } ?: ClassName.get(
                factory.packageName,
                matcherName
            )
        }
    }

    private fun obtainPathParameters(method: XExecutableElement): Map<String, TypeName> {
        return method.parameters.mapNotNull {
            val pathAnnotation = it.getAnnotation(Path::class)?.value ?: return@mapNotNull null

            val segmentName = pathAnnotation.value.ifEmpty {
                it.name
            }
            segmentName to it.type.typeName
        }.associate { it }
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
        }?.uppercase()
    }

    private fun generateUriMatcherContent(
        metadata: UriMatcherMetadata,
        factory: XTypeElement,
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

        if (metadata.matcherCodes.isNotEmpty()) {
            uriMatcherContent.addType(generateMatcherCodeClass(metadata))
        }

        uriMatcherContent.addOriginatingElement(factory)

        return uriMatcherContent.build()
    }

    private fun generateMatcherCodeClass(metadata: UriMatcherMetadata): TypeSpec {
        val matcherCodeContent = TypeSpec.classBuilder(metadata.matcherCodeClassName)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)

        metadata.matcherCodes.forEach { (_, _, codeField) ->
            codeField?.let { matcherCodeContent.addField(it) }
        }

        matcherCodeContent.addMethod(
            MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .build()
        )

        matcherCodeContent.addMethod(generateMatcherCodeToString(metadata.matcherCodes))

        return matcherCodeContent.build()
    }

    private fun generateInitMatcher(metadata: UriMatcherMetadata): MethodSpec {
        val method = MethodSpec.methodBuilder("initMatcher")
            .addModifiers(Modifier.PRIVATE)

        val authority = metadata.authority
        metadata.pathMappings.forEach { (path, matcherCode) ->
            if (!matcherCode.enabled) {
                return@forEach // skip disabled matcher codes
            }

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

    private fun generateMatcherCodeToString(
        matcherCodes: Collection<MatcherCodeMetadata>
    ): MethodSpec {
        val method = MethodSpec.methodBuilder("toString")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addAnnotation(NON_NULL)
            .returns(STRING)

        val codeParam = ParameterSpec.builder(TypeName.INT, "code").build()

        method.addParameter(codeParam)

        method.beginControlFlow("switch (\$N)", codeParam)
        matcherCodes.forEach { (_, _, codeField) ->
            codeField?.let { method.addStatement("case \$1N: return \"\$1N\"", it) }
        }
        method.endControlFlow()
        method.addStatement("return \$T.toString(\$N)", TypeName.INT.box(), codeParam)

        return method.build()
    }

    private fun createMatcherCode(
        code: Int,
        enabled: Boolean
    ) = MatcherCodeMetadata(code, enabled, null)

    private fun createMatcherCode(fieldName: String, enabled: Boolean): MatcherCodeMetadata {
        val matcherCode = ++matcherCodeCounter
        val matcherCodeField = FieldSpec.builder(
            TypeName.INT,
            fieldName,
            Modifier.PUBLIC,
            Modifier.STATIC,
            Modifier.FINAL
        ).initializer("\$L", matcherCode).build()

        return MatcherCodeMetadata(matcherCode, enabled, matcherCodeField)
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
        val enabled: Boolean,
        val field: FieldSpec?
    )

    companion object {
        private const val DEFAULT_MATCHER_SUFFIX = "_UriMatcher"
        private const val MATCHER_CODE_NAME = "MatcherCode"
        private const val PATH_SEPARATOR = "/"
        private const val WILDCARD_ANY = "*"
        private const val WILDCARD_NUMBER = "#"

        private val PATH_TEMPLATE_REGEX = "^\\{([a-zA-Z0-9_-]+)}$".toRegex()
        private val FIELD_NAME_REGEX = "^[a-zA-Z_][a-zA-Z0-9_]*$".toRegex()
    }
}

