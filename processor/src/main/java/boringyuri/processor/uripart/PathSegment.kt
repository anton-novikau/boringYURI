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

package boringyuri.processor.uripart

import boringyuri.processor.base.AbortProcessingException
import boringyuri.processor.ext.createMethodSignature
import boringyuri.processor.ext.findTypeAdapter
import boringyuri.processor.ext.valueMirror
import boringyuri.processor.type.CommonTypeName.STRING
import boringyuri.processor.type.TypeConverter
import boringyuri.processor.util.AnnotationHandler
import boringyuri.processor.util.Logger
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterizedTypeName
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.type.TypeMirror

interface PathSegment {

    fun createValueBlock(typeConverter: TypeConverter): CodeBlock

}

interface ReadPathSegment : PathSegment {

    val segmentField: FieldSpec

    fun createMethodSignature(annotationHandler: AnnotationHandler): MethodSpec.Builder
}

class TemplatePathSegment(
    val segmentIndex: Int,
    private val name: String,
    private val originatingElement: Element,
    private val logger: Logger
) : ReadPathSegment, PathSegment {

    override val segmentField: FieldSpec
        get() = throw createException()

    override fun createValueBlock(typeConverter: TypeConverter): CodeBlock {
        throw createException()
    }

    override fun createMethodSignature(annotationHandler: AnnotationHandler): MethodSpec.Builder {
        throw createException()
    }

    private fun createException(): AbortProcessingException {
        return AbortProcessingException(
            logger,
            originatingElement,
            "Path template {$name} doesn't have an appropriate substitute"
        )
    }

}

class MethodReadPathSegment(
    segmentIndex: Int,
    segmentName: String,
    segmentField: FieldSpec,
    uriField: FieldSpec,
    private val defaultValue: String?,
    private val segment: ExecutableElement
) : BaseReadPathSegment(segmentIndex, segmentName, segmentField, uriField, defaultValue, segment) {

    override fun createMethodSignature(
        annotationHandler: AnnotationHandler
    ): MethodSpec.Builder = segment.createMethodSignature(defaultValue, annotationHandler)

    override fun createValueBlock(typeConverter: TypeConverter): CodeBlock {
        return createValueBlock(typeConverter, segment.findTypeAdapter()?.valueMirror())
    }

}

abstract class BaseReadPathSegment(
    private val segmentIndex: Int,
    private val segmentName: String,
    override val segmentField: FieldSpec,
    private val uriField: FieldSpec,
    private val defaultValue: String?,
    private val segment: Element
) : ReadPathSegment {

    protected fun createValueBlock(
        typeConverter: TypeConverter,
        typeAdapter: TypeMirror?
    ): CodeBlock {
        val codeBlock = CodeBlock.builder()

        val segmentListVariableName = "segments"
        val segmentVariableName = "pathSegment"

        codeBlock.addStatement(
            "\$T \$L = \$N.getPathSegments()",
            ParameterizedTypeName.get(ClassName.get(List::class.java), STRING),
            segmentListVariableName,
            uriField
        )

        codeBlock.beginControlFlow("if (\$L.size() <= \$L)", segmentListVariableName, segmentIndex)

        codeBlock.addStatement(
            "throw new \$T(\$S + \$N)",
            NullPointerException::class.java,
            CodeBlock.of("Segment '\$L' is not provided to ", segmentName),
            uriField
        )

        codeBlock.nextControlFlow("else")

        codeBlock.addStatement(
            "\$T \$L = \$L.get(\$L)",
            STRING,
            segmentVariableName,
            segmentListVariableName,
            segmentIndex
        )


        val deserializeBlock = if (typeAdapter != null) {
            typeConverter.buildCustomDeserializeBlock(
                CodeBlock.of("\$L", segmentVariableName),
                segmentField,
                typeAdapter
            )
        } else {
            typeConverter.buildStandardDeserializeBlock(
                segmentVariableName,
                segmentField,
                false,
                defaultValue,
                segment
            )
        }

        codeBlock.add(deserializeBlock)
        codeBlock.endControlFlow()

        return codeBlock.build()
    }

}