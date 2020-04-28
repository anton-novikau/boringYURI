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

package boringyuri.processor.util

import boringyuri.api.adapter.BoringTypeAdapter
import boringyuri.processor.base.AbortProcessingException
import boringyuri.processor.util.CommonTypeName.*
import com.squareup.javapoet.*
import javax.lang.model.element.Element
import javax.lang.model.type.TypeMirror


class TypeConverter(private val logger: Logger) {

    fun buildSerializeBlock(
        param: ParameterSpec,
        typeAdapter: TypeMirror?,
        originatingElement: Element? = null
    ): CodeBlock = if (typeAdapter != null) {
        CodeBlock.of("new \$T().serialize(\$N)", TypeName.get(typeAdapter), param)
    } else if (param.type == STRING) {
        CodeBlock.of("\$N", param)
    } else if (param.type.isPrimitive
        || param.type.isBoxedPrimitive
        || param.type == ANDROID_URI) {
        CodeBlock.of("\$T.valueOf(\$N)", STRING, param)
    } else {
        throw AbortProcessingException(logger, originatingElement, "Unknown type ${param.type}")
    }

    fun buildCustomDeserializeBlock(
        localVariableName: String,
        field: FieldSpec,
        typeAdapter: TypeMirror
    ): CodeBlock {
        val deserializeBlock = CodeBlock.builder()
        val adapterName = "typeAdapter"
        val adapterType = ParameterizedTypeName.get(
            ClassName.get(BoringTypeAdapter::class.java),
            field.type
        )
        deserializeBlock.addStatement(
            "\$T \$L = new \$T()",
            adapterType,
            adapterName,
            TypeName.get(typeAdapter)
        )
        deserializeBlock.addStatement(
            "\$N = \$L.deserialize(\$L)",
            field,
            adapterName,
            localVariableName
        )

        return deserializeBlock.build()
    }

    fun buildStandardDeserializeBlock(
        localVariableName: String,
        field: FieldSpec,
        nullable: Boolean,
        originatingElement: Element? = null
    ): CodeBlock {
        val fieldType = field.type
        val deserializeBlock = CodeBlock.builder()

        if (TypeName.BOOLEAN == fieldType || TypeName.BOOLEAN.box() == fieldType) {
            deserializeBlock.addStatement(
                "$1N = \"true\".equalsIgnoreCase($2L) || \"1\".equals($2L)",
                field,
                localVariableName
            )
        } else if (TypeName.CHAR == fieldType || TypeName.CHAR.box() == fieldType) {
            deserializeBlock.addStatement(
                "$1N = $2L.length() > 0 ? $2L.charAt(0) : $3L",
                field,
                localVariableName,
                if (nullable) "null" else "'0'"
            )
        } else if (STRING == fieldType) {
            deserializeBlock.addStatement("\$N = \$L", field, localVariableName)
        } else if (ANDROID_URI == fieldType) {
            deserializeBlock.addStatement(
                "\$N = \$T.parse(\$L)",
                field,
                ANDROID_URI,
                localVariableName
            )
        } else if (fieldType.isPrimitive || fieldType.isBoxedPrimitive) {
            deserializeBlock.add(
                buildNumberDeserializeBlock(
                    localVariableName,
                    field,
                    nullable,
                    originatingElement
                )
            )
        } else {
            throw AbortProcessingException(
                logger,
                originatingElement,
                "Type $fieldType is not supported"
            )
        }


        return deserializeBlock.build()
    }

    private fun buildNumberDeserializeBlock(
        localVariableName: String,
        field: FieldSpec,
        nullable: Boolean,
        originatingElement: Element? = null
    ): CodeBlock {
        val fieldType = field.type

        val (parseBlock, defaultValueBlock) = when (fieldType) {
            TypeName.BYTE, TypeName.BYTE.box() -> {
                CodeBlock.builder().addStatement(
                    "\$N = \$T.parseByte(\$L)",
                    field,
                    TypeName.BYTE.box(),
                    localVariableName
                ).build() to CodeBlock.of("(byte) 0")
            }
            TypeName.SHORT, TypeName.SHORT.box() -> {
                CodeBlock.builder().addStatement(
                    "\$N = \$T.parseShort(\$L)",
                    field,
                    TypeName.SHORT.box(),
                    localVariableName
                ).build() to CodeBlock.of("(short) 0")
            }
            TypeName.INT, TypeName.INT.box() -> {
                CodeBlock.builder().addStatement(
                    "\$N = \$T.parseInt(\$L)",
                    field,
                    TypeName.INT.box(),
                    localVariableName
                ).build() to CodeBlock.of("0")
            }
            TypeName.LONG, TypeName.LONG.box() -> {
                CodeBlock.builder().addStatement(
                    "\$N = \$T.parseLong(\$L)",
                    field,
                    TypeName.LONG.box(),
                    localVariableName
                ).build() to CodeBlock.of("0L")
            }
            TypeName.FLOAT, TypeName.FLOAT.box() -> {
                CodeBlock.builder().addStatement(
                    "\$N = \$T.parseFloat(\$L)",
                    field,
                    TypeName.FLOAT.box(),
                    localVariableName
                ).build() to CodeBlock.of("0.0f")
            }
            TypeName.DOUBLE, TypeName.DOUBLE.box() -> {
                CodeBlock.builder().addStatement(
                    "\$N = \$T.parseDouble(\$L)",
                    field,
                    TypeName.DOUBLE.box(),
                    localVariableName
                ).build() to CodeBlock.of("0.0")
            }
            else -> {
                throw AbortProcessingException(
                    logger,
                    originatingElement,
                    "Type $fieldType is not supported"
                )
            }
        }

        val defaultAssignmentBlock = if (nullable) CodeBlock.of("null") else defaultValueBlock

        return CodeBlock.builder()
            .beginControlFlow("try")
            .add(parseBlock)
            .nextControlFlow("catch (\$T e)", NumberFormatException::class.java)
            .addStatement("\$N = \$L", field, defaultAssignmentBlock)
            .endControlFlow()
            .build()
    }
}