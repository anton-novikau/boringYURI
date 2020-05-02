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

import boringyuri.processor.ext.createMethodSignature
import boringyuri.processor.ext.findTypeAdapter
import boringyuri.processor.ext.valueMirror
import boringyuri.processor.util.AnnotationHandler
import boringyuri.processor.util.CommonTypeName.STRING
import boringyuri.processor.util.TypeConverter
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.TypeMirror

interface QueryParameter {

    val name: String

    fun createValueBlock(typeConverter: TypeConverter): CodeBlock

}

interface ReadQueryParameter : QueryParameter {

    val paramField: FieldSpec

    fun createMethodSignature(annotationHandler: AnnotationHandler): MethodSpec.Builder

}

class VariableWriteQueryParameter(
    override val name: String,
    private val methodParam: ParameterSpec,
    private val parameter: VariableElement,
    private val nullable: Boolean,
    private val builderName: String
) : QueryParameter {

    override fun createValueBlock(typeConverter: TypeConverter): CodeBlock {
        val typeAdapter = parameter.findTypeAdapter()?.valueMirror()

        val appendQueryBlock = CodeBlock.builder()

        if (nullable) {
            appendQueryBlock.beginControlFlow("if (\$N != null)", methodParam)
        }

        val serializedParam = typeConverter.buildSerializeBlock(
            methodParam,
            typeAdapter,
            parameter
        )

        appendQueryBlock.addStatement(
            "\$L.appendQueryParameter(\$S, \$L)",
            builderName,
            name,
            serializedParam
        )

        if (nullable) {
            appendQueryBlock.endControlFlow()
        }

        return appendQueryBlock.build()
    }

}

class VariableReadQueryParameter(
    name: String,
    paramField: FieldSpec,
    uriField: FieldSpec,
    nullable: Boolean,
    private val parameterElement: VariableElement
) : BaseReadQueryParameter(name, paramField, uriField, nullable, parameterElement) {

    override fun createMethodSignature(
        annotationHandler: AnnotationHandler
    ): MethodSpec.Builder {
        return parameterElement.createMethodSignature(annotationHandler)
    }

    override fun createValueBlock(typeConverter: TypeConverter): CodeBlock {
        return createValueBlock(typeConverter, parameterElement.findTypeAdapter()?.valueMirror())
    }

}

class MethodReadQueryParameter(
    name: String,
    paramField: FieldSpec,
    uriField: FieldSpec,
    nullable: Boolean,
    private val parameterElement: ExecutableElement
) : BaseReadQueryParameter(name, paramField, uriField, nullable, parameterElement) {

    override fun createMethodSignature(
        annotationHandler: AnnotationHandler
    ): MethodSpec.Builder {
        return parameterElement.createMethodSignature(annotationHandler)
    }

    override fun createValueBlock(typeConverter: TypeConverter): CodeBlock {
        return createValueBlock(typeConverter, parameterElement.findTypeAdapter()?.valueMirror())
    }

}

abstract class BaseReadQueryParameter(
    override val name: String,
    override val paramField: FieldSpec,
    private val uriField: FieldSpec,
    private val nullable: Boolean,
    private val originatingElement: Element
) : ReadQueryParameter {

    protected fun createValueBlock(
        typeConverter: TypeConverter,
        typeAdapter: TypeMirror?
    ): CodeBlock {
        val statement = CodeBlock.builder()
        val localVarName = "queryParam"

        statement.addStatement(
            "\$T \$L = \$N.getQueryParameter(\$S)",
            STRING,
            localVarName,
            uriField,
            name
        )

        if (!paramField.type.isPrimitive && !nullable) {
            statement.beginControlFlow("if (\$L == null)", localVarName)
            statement.addStatement(
                "throw new \$T(\$S + \$N)",
                NullPointerException::class.java,
                CodeBlock.of("Parameter '\$L' is not provided to ", name),
                uriField
            )
            statement.endControlFlow()
        }
        if (nullable) {
            statement.beginControlFlow("if (\$L == null)", localVarName)
            statement.addStatement("\$N = null", paramField)
            statement.nextControlFlow("else")
        }

        val deserializeBlock = if (typeAdapter != null) {
            typeConverter.buildCustomDeserializeBlock(
                localVarName,
                paramField,
                typeAdapter
            )
        } else {
            typeConverter.buildStandardDeserializeBlock(
                localVarName,
                paramField,
                nullable,
                originatingElement
            )
        }

        statement.add(deserializeBlock)

        if (nullable) {
            statement.endControlFlow()
        }

        return statement.build()
    }

}