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
package boringyuri.processor.type

import boringyuri.processor.util.Counter
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import javax.lang.model.element.Element
import javax.lang.model.type.ArrayType
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.PrimitiveType
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.SimpleTypeVisitor8

object ConversionStrategyFactory {

    fun createQueryStrategy(
        type: TypeMirror,
        typeAdapter: TypeMirror?,
        typeConverter: TypeConverter,
        originatingElement: Element
    ): QueryConversionStrategy {
        val componentType = type.accept(ComponentTypeVisitor(), null)

        return if (componentType != null) {
            ArrayQueryConversionStrategy(
                componentType,
                typeAdapter,
                typeConverter,
                originatingElement
            )
        } else {
            DefaultQueryConversionStrategy(typeAdapter, typeConverter, originatingElement)
        }
    }
}

interface QueryConversionStrategy {

    fun buildSerializeBlock(
        builderName: String,
        paramName: String,
        methodParam: ParameterSpec
    ): CodeBlock

    fun buildReadRawParameterBlock(paramName: String, uriField: FieldSpec): CodeBlock

    fun buildCheckRawParameterBlock(): CodeBlock

    fun buildDeserializeDefaultBlock(defaultValue: String, paramField: FieldSpec): CodeBlock

    fun buildDeserializeBlock(
        paramField: FieldSpec,
        nullable: Boolean,
        defaultValue: String?
    ): CodeBlock
}

private class DefaultQueryConversionStrategy(
    private val typeAdapter: TypeMirror?,
    private val typeConverter: TypeConverter,
    private val originatingElement: Element
) : QueryConversionStrategy {

    private val deserializeVariableName = "queryParam"

    override fun buildSerializeBlock(
        builderName: String,
        paramName: String,
        methodParam: ParameterSpec
    ): CodeBlock {
        return CodeBlock.builder()
            .addStatement(
                "\$L.appendQueryParameter(\$S, \$L)",
                builderName,
                paramName,
                typeConverter.buildSerializeBlock(methodParam, typeAdapter, originatingElement)
            ).build()
    }

    override fun buildReadRawParameterBlock(
        paramName: String,
        uriField: FieldSpec
    ): CodeBlock {
        return CodeBlock.builder()
            .addStatement(
                "\$T \$L = \$N.getQueryParameter(\$S)",
                CommonTypeName.STRING,
                deserializeVariableName,
                uriField,
                paramName
            ).build()
    }

    override fun buildCheckRawParameterBlock(): CodeBlock {
        return CodeBlock.of("\$L == null", deserializeVariableName)
    }

    override fun buildDeserializeDefaultBlock(
        defaultValue: String,
        paramField: FieldSpec
    ): CodeBlock {
        return if (typeAdapter != null) {
            CodeBlock.builder().add(
                typeConverter.buildCustomDeserializeBlock(
                    CodeBlock.of("\$S", defaultValue),
                    paramField,
                    typeAdapter
                )
            )
        } else {
            CodeBlock.builder().addStatement(
                "\$N = \$L",
                paramField,
                typeConverter.buildStandardDeserializeBlockForDefault(
                    defaultValue,
                    paramField.type,
                    originatingElement
                )
            )
        }.build()
    }

    override fun buildDeserializeBlock(
        paramField: FieldSpec,
        nullable: Boolean,
        defaultValue: String?
    ): CodeBlock {
        return if (typeAdapter != null) {
            typeConverter.buildCustomDeserializeBlock(
                CodeBlock.of("\$L", deserializeVariableName),
                paramField,
                typeAdapter
            )
        } else {
            typeConverter.buildStandardDeserializeBlock(
                deserializeVariableName,
                paramField,
                nullable,
                defaultValue,
                originatingElement
            )
        }
    }
}

private class ArrayQueryConversionStrategy(
    private val componentType: TypeMirror,
    private val typeAdapter: TypeMirror?,
    private val typeConverter: TypeConverter,
    private val originatingElement: Element
) : QueryConversionStrategy {

    private val deserializeVariableName = "queryParams"

    override fun buildSerializeBlock(
        builderName: String,
        paramName: String,
        methodParam: ParameterSpec
    ): CodeBlock {
        val serializeBlock = CodeBlock.builder()

        val indexName = "i"
        serializeBlock.beginControlFlow(
            "for (int \$1L = 0, size = \$2N.length; \$1L < size; \$1L++)",
            indexName,
            methodParam
        )
        val componentTypeName = TypeName.get(componentType)

        if (!componentTypeName.isPrimitive) {
            serializeBlock.beginControlFlow("if (\$N[\$L] != null)", methodParam, indexName)
        }

        serializeBlock.addStatement(
            "\$L.appendQueryParameter(\$S, \$L)",
            builderName,
            paramName,
            typeConverter.buildSerializeBlock(
                CodeBlock.of("\$N[\$L]", methodParam, indexName),
                componentTypeName,
                typeAdapter,
                originatingElement
            )
        )
        if (!componentTypeName.isPrimitive) {
            serializeBlock.endControlFlow()
        }

        serializeBlock.endControlFlow()

        return serializeBlock.build()
    }

    override fun buildReadRawParameterBlock(
        paramName: String,
        uriField: FieldSpec
    ): CodeBlock {
        return CodeBlock.builder().addStatement(
            "\$T \$L = \$N.getQueryParameters(\$S)",
            CommonTypeName.STRING_LIST,
            deserializeVariableName,
            uriField,
            paramName
        ).build()
    }


    override fun buildCheckRawParameterBlock(): CodeBlock {
        return CodeBlock.of("\$1L == null || \$1L.isEmpty()", deserializeVariableName)
    }

    override fun buildDeserializeDefaultBlock(
        defaultValue: String,
        paramField: FieldSpec
    ): CodeBlock {
        val depthCounter = Counter()
        val rawComponentType = componentType.accept(RawTypeNameVisitor(), depthCounter)

        return if (typeAdapter != null) {
            CodeBlock.builder()
                .addStatement(
                    "\$N = new \$T[1]${"[]".repeat(depthCounter.value)}",
                    paramField,
                    rawComponentType
                ).add(
                    typeConverter.buildCustomDeserializeBlock(
                        CodeBlock.of("\$S", defaultValue),
                        CodeBlock.of("\$N[0]", paramField),
                        typeAdapter
                    )
                ).build()
        } else {
            CodeBlock.builder()
                .addStatement(
                    "\$N = new \$T[] { \$L }",
                    paramField,
                    rawComponentType,
                    typeConverter.buildStandardDeserializeBlockForDefault(
                        defaultValue,
                        TypeName.get(componentType),
                        originatingElement
                    )
                ).build()
        }
    }

    override fun buildDeserializeBlock(
        paramField: FieldSpec,
        nullable: Boolean,
        defaultValue: String?
    ): CodeBlock {
        val deserializeBlock = CodeBlock.builder()
        val depthCounter = Counter()
        val componentTypeName = componentType.accept(RawTypeNameVisitor(), depthCounter)

        deserializeBlock.addStatement(
            "\$N = new \$T[\$L.size()]${"[]".repeat(depthCounter.value)}",
            paramField,
            componentTypeName,
            deserializeVariableName
        )
        val indexName = "i"
        deserializeBlock.beginControlFlow(
            "for (int \$1L = 0, size = \$2L.size(); \$1L < size; \$1L++)",
            indexName,
            deserializeVariableName
        )
        if (typeAdapter != null) {
            deserializeBlock.add(
                typeConverter.buildCustomDeserializeBlock(
                    CodeBlock.of("\$L.get(\$L)", deserializeVariableName, indexName),
                    CodeBlock.of("\$N[\$L]", paramField, indexName),
                    typeAdapter
                )
            )
        } else {
            deserializeBlock.add(
                typeConverter.buildStandardDeserializeBlock(
                    CodeBlock.of("\$L.get(\$L)", deserializeVariableName, indexName),
                    CodeBlock.of("\$N[\$L]", paramField, indexName),
                    TypeName.get(componentType),
                    nullable,
                    defaultValue,
                    originatingElement
                )
            )
        }

        deserializeBlock.endControlFlow()

        return deserializeBlock.build()
    }
}

private class ComponentTypeVisitor : SimpleTypeVisitor8<TypeMirror?, Void>() {

    override fun visitArray(type: ArrayType, parameter: Void?): TypeMirror = type.componentType
}

private class RawTypeNameVisitor : SimpleTypeVisitor8<TypeName, Counter>() {

    override fun visitArray(type: ArrayType, counter: Counter): TypeName {
        return type.componentType.accept(this, counter.increment())
    }

    override fun visitDeclared(type: DeclaredType, counter: Counter): TypeName {
        return when (val declaredType = TypeName.get(type)) {
            is ParameterizedTypeName -> declaredType.rawType
            else -> declaredType
        }
    }

    override fun visitPrimitive(type: PrimitiveType, counter: Counter): TypeName {
        return TypeName.get(type)
    }

    override fun defaultAction(e: TypeMirror, p: Counter): TypeName {
        throw IllegalArgumentException("$e is not supported")
    }
}