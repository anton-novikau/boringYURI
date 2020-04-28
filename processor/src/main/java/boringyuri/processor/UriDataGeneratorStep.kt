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

import boringyuri.processor.base.BoringProcessingStep
import boringyuri.processor.base.ProcessingSession
import boringyuri.processor.uripart.ReadPathSegment
import boringyuri.processor.uripart.ReadQueryParameter
import boringyuri.processor.uripart.TemplatePathSegment
import boringyuri.processor.util.AnnotationHandler
import boringyuri.processor.util.CommonTypeName.*
import boringyuri.processor.util.TypeConverter
import com.squareup.javapoet.*
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier

abstract class UriDataGeneratorStep protected constructor(
    session: ProcessingSession,
    protected val annotationHandler: AnnotationHandler
) : BoringProcessingStep(session) {

    protected val uriField: FieldSpec = FieldSpec.builder(
        ANDROID_URI,
        URI_FIELD_NAME,
        Modifier.PRIVATE,
        Modifier.FINAL
    ).addAnnotation(NON_NULL).build()

    private val parseFlagField: FieldSpec = FieldSpec.builder(
        TypeName.INT,
        PARSE_FLAG_NAME,
        Modifier.PRIVATE
    ).build()

    private val typeConverter = TypeConverter(logger)

    protected fun obtainBasePathSegments(
        basePath: String,
        originatingElement: Element
    ): MutableMap<String, ReadPathSegment> {
        return basePath
            .ifEmpty { return LinkedHashMap() } // early exit
            .split("/")
            .filter { it.isNotEmpty() }
            .mapIndexedNotNull { index, segment ->
                val template = PATH_TEMPLATE_REGEX.find(segment)?.run { groupValues[1] }

                template?.let { index to it } // ignore all constant segments
            }
            .associateTo(LinkedHashMap()) {
                val segmentIndex = it.first
                val segmentName = it.second
                val segment = TemplatePathSegment(
                    segmentIndex,
                    segmentName,
                    originatingElement,
                    logger
                )

                segmentName to segment
            }
    }

    protected fun generateUriDataClassContent(
        className: ClassName,
        sourceElement: Element,
        uriMetadata: UriMetadata,
        superInterface: TypeName? = null
    ): TypeSpec {
        val classContent = TypeSpec.classBuilder(className)
            .addModifiers(Modifier.FINAL, Modifier.PUBLIC)

        superInterface?.let { classContent.addSuperinterface(it) }

        classContent.addField(uriField)
        classContent.addField(parseFlagField)
        classContent.addFields(uriMetadata.fieldSpecs)

        classContent.addMethod(generateConstructor())

        var uriPartIndex = 0
        uriMetadata.pathSegments.forEach {
            val method = generateGetterMethodImpl(PathSegmentUriPart(it), 1 shl uriPartIndex)
            classContent.addMethod(method)
            uriPartIndex++
        }

        uriMetadata.queryParameters.forEach {
            val method = generateGetterMethodImpl(QueryParameterUriPart(it), 1 shl uriPartIndex)
            classContent.addMethod(method)
            uriPartIndex++
        }

        onPostGenerateContent(classContent, sourceElement)

        classContent.addMethod(generateToString())

        return classContent.build()
    }

    protected open fun onPostGenerateContent(
        classContent: TypeSpec.Builder,
        sourceElement: Element
    ) {
        // NO-OP
    }

    private fun generateConstructor(): MethodSpec {
        val uriParam = ParameterSpec.builder(ANDROID_URI, "uri")
            .addAnnotations(uriField.annotations).build()

        return MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PUBLIC)
            .addParameter(uriParam)
            .addStatement("\$N = \$N", uriField, uriParam)
            .build()
    }

    private fun generateToString(): MethodSpec {
        return MethodSpec.methodBuilder("toString")
            .addAnnotation(OVERRIDE)
            .addAnnotation(NON_NULL)
            .addModifiers(Modifier.PUBLIC)
            .returns(STRING)
            .addStatement("return \$N.toString()", uriField)
            .build()
    }

    private fun generateGetterMethodImpl(
        uriPart: UriPart,
        parseFlagValue: Int
    ): MethodSpec {
        val field = uriPart.fieldSpec
        val method = uriPart.createMethodSignature(annotationHandler)

        method.beginControlFlow("if ((\$N & \$L) != 0)", parseFlagField, parseFlagValue)
        method.addStatement("return \$N", field)
        method.endControlFlow()

        method.addCode("\n")

        method.addCode(uriPart.createReadValueBlock(typeConverter))

        method.addCode("\n")
        method.addStatement("\$N |= \$L", parseFlagField, parseFlagValue)

        method.addCode("\n")
        method.addStatement("return \$N", field)

        return method.build()
    }

    protected data class UriMetadata(
        val fieldSpecs: List<FieldSpec>,
        val pathSegments: List<ReadPathSegment>,
        val queryParameters: List<ReadQueryParameter>
    )

    private interface UriPart {

        val fieldSpec: FieldSpec

        fun createMethodSignature(annotationHandler: AnnotationHandler): MethodSpec.Builder

        fun createReadValueBlock(typeConverter: TypeConverter): CodeBlock

    }

    private class PathSegmentUriPart(
        private val pathSegment: ReadPathSegment
    ) : UriPart {

        override val fieldSpec: FieldSpec
            get() = pathSegment.segmentField

        override fun createMethodSignature(
            annotationHandler: AnnotationHandler
        ): MethodSpec.Builder {
            return pathSegment.createMethodSignature(annotationHandler)
        }

        override fun createReadValueBlock(typeConverter: TypeConverter): CodeBlock {
            return pathSegment.createValueBlock(typeConverter)
        }

    }

    private class QueryParameterUriPart(
        private val queryParameter: ReadQueryParameter
    ) : UriPart {

        override val fieldSpec: FieldSpec
            get() = queryParameter.paramField

        override fun createMethodSignature(
            annotationHandler: AnnotationHandler
        ): MethodSpec.Builder {
            return queryParameter.createMethodSignature(annotationHandler)
        }

        override fun createReadValueBlock(typeConverter: TypeConverter): CodeBlock {
            return queryParameter.createValueBlock(typeConverter)
        }

    }

    companion object {
        private const val URI_FIELD_NAME = "mDataUri"
        private const val PARSE_FLAG_NAME = "mParseFlag"

        private val PATH_TEMPLATE_REGEX = "^\\{([a-zA-Z0-9_-]+)}$".toRegex()
    }

}