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

import boringyuri.processor.base.AbortProcessingException
import boringyuri.processor.type.CommonTypeName.*
import boringyuri.processor.util.Logger
import com.google.auto.common.MoreTypes
import com.squareup.javapoet.*
import javax.lang.model.element.Element
import javax.lang.model.type.TypeMirror


class TypeConverter(
    private val logger: Logger,
    private val typeAdapterFactory: ClassName? = null
) {

    fun buildSerializeBlock(
        param: ParameterSpec,
        typeAdapter: TypeMirror?,
        originatingElement: Element? = null
    ): CodeBlock = buildSerializeBlock(
        CodeBlock.of("\$N", param),
        param.type,
        typeAdapter,
        originatingElement
    )

    fun buildSerializeBlock(
        param: CodeBlock,
        paramType: TypeName,
        typeAdapter: TypeMirror?,
        originatingElement: Element? = null
    ): CodeBlock = if (typeAdapter != null) {
        CodeBlock.of("\$L.serialize(\$L)", buildCreateTypeAdapterBlock(typeAdapter), param)
    } else if (paramType == STRING) {
        CodeBlock.of("\$L", param)
    } else if (paramType.isPrimitive
        || paramType.isBoxedPrimitive
        || paramType == ANDROID_URI) {
        CodeBlock.of("\$T.valueOf(\$L)", STRING, param)
    } else {
        throw AbortProcessingException(logger, originatingElement, "Unknown type $paramType")
    }

    fun buildCustomDeserializeBlock(
        value: CodeBlock,
        field: FieldSpec,
        typeAdapter: TypeMirror
    ): CodeBlock {
        return buildCustomDeserializeBlock(value, CodeBlock.of("\$N", field), typeAdapter)
    }

    fun buildCustomDeserializeBlock(
        value: CodeBlock,
        field: CodeBlock,
        typeAdapter: TypeMirror
    ): CodeBlock {
        val deserializeBlock = CodeBlock.builder()

        deserializeBlock.addStatement(
            "\$L = \$L.deserialize(\$L)",
            field,
            buildCreateTypeAdapterBlock(typeAdapter),
            value
        )

        return deserializeBlock.build()
    }

    fun buildStandardDeserializeBlockForDefault(
        value: String,
        type: TypeName,
        originatingElement: Element?
    ): CodeBlock {
        if (value.isEmpty() && STRING != type && ANDROID_URI != type) {
            throw AbortProcessingException(
                logger,
                originatingElement,
                "Default value for $type can not be empty.")
        }

        return when (type) {
            ANDROID_URI -> if (value.isEmpty()) {
                CodeBlock.of("\$T.EMPTY", ANDROID_URI)
            } else {
                CodeBlock.of("\$T.parse(\$S)", ANDROID_URI, value)
            }
            STRING -> CodeBlock.of("\$S", value)
            TypeName.BOOLEAN, TypeName.BOOLEAN.box() -> CodeBlock.of("\$L", value.toBoolean())
            TypeName.CHAR, TypeName.CHAR.box() -> CodeBlock.of("'\$L'", value.toCharArray()[0])
            TypeName.BYTE, TypeName.BYTE.box() -> CodeBlock.of("(byte) \$L", value.toByte())
            TypeName.SHORT, TypeName.SHORT.box() -> CodeBlock.of("(short) \$L", value.toShort())
            TypeName.INT, TypeName.INT.box() -> CodeBlock.of("\$L", value.toInt())
            TypeName.LONG, TypeName.LONG.box() -> CodeBlock.of("\$LL", value.toLong())
            TypeName.FLOAT, TypeName.FLOAT.box() -> CodeBlock.of("\$Lf", value.toFloat())
            TypeName.DOUBLE, TypeName.DOUBLE.box() -> CodeBlock.of("\$L", value.toDouble())
            else -> throw IllegalStateException("$type is not a standard type for conversion")
        }
    }

    fun buildStandardDeserializeBlock(
        value: String,
        field: FieldSpec,
        nullable: Boolean,
        defaultValue: String?,
        originatingElement: Element? = null
    ): CodeBlock {
        return buildStandardDeserializeBlock(
            CodeBlock.of("\$L", value),
            CodeBlock.of("\$N", field),
            field.type,
            nullable,
            defaultValue,
            originatingElement
        )
    }

    fun buildStandardDeserializeBlock(
        value: CodeBlock,
        field: CodeBlock,
        fieldType: TypeName,
        nullable: Boolean,
        defaultValue: String?,
        originatingElement: Element? = null
    ): CodeBlock {
        val deserializeBlock = CodeBlock.builder()

        when (fieldType) {
            TypeName.BOOLEAN, TypeName.BOOLEAN.box() -> {
                deserializeBlock.addStatement(
                    "$1L = \"true\".equalsIgnoreCase($2L) || \"1\".equals($2L)",
                    field,
                    value
                )
            }
            TypeName.CHAR, TypeName.CHAR.box() -> {
                deserializeBlock.addStatement(
                    "$1L = $2L.length() > 0 ? $2L.charAt(0) : $3L",
                    field,
                    value,
                    when {
                        defaultValue != null -> {
                            buildStandardDeserializeBlockForDefault(
                                defaultValue,
                                fieldType,
                                originatingElement
                            )
                        }
                        nullable -> "null"
                        else -> "'0'"
                    }
                )
            }
            STRING -> {
                deserializeBlock.addStatement("\$L = \$L", field, value)
            }
            ANDROID_URI -> {
                deserializeBlock.addStatement(
                    "\$L = \$T.parse(\$L)",
                    field,
                    ANDROID_URI,
                    value
                )
            }
            else -> {
                deserializeBlock.add(
                    buildNumberDeserializeBlock(
                        value,
                        field,
                        fieldType,
                        nullable,
                        defaultValue,
                        originatingElement
                    )
                )
            }
        }


        return deserializeBlock.build()
    }

    private fun buildNumberDeserializeBlock(
        value: CodeBlock,
        field: CodeBlock,
        fieldType: TypeName,
        nullable: Boolean,
        defaultValue: String?,
        originatingElement: Element? = null
    ): CodeBlock {
        val (parseBlock, defaultValueBlock) = when (fieldType) {
            TypeName.BYTE, TypeName.BYTE.box() -> {
                CodeBlock.builder().addStatement(
                    "\$L = \$T.parseByte(\$L)",
                    field,
                    TypeName.BYTE.box(),
                    value
                ).build() to CodeBlock.of("(byte) 0")
            }
            TypeName.SHORT, TypeName.SHORT.box() -> {
                CodeBlock.builder().addStatement(
                    "\$L = \$T.parseShort(\$L)",
                    field,
                    TypeName.SHORT.box(),
                    value
                ).build() to CodeBlock.of("(short) 0")
            }
            TypeName.INT, TypeName.INT.box() -> {
                CodeBlock.builder().addStatement(
                    "\$L = \$T.parseInt(\$L)",
                    field,
                    TypeName.INT.box(),
                    value
                ).build() to CodeBlock.of("0")
            }
            TypeName.LONG, TypeName.LONG.box() -> {
                CodeBlock.builder().addStatement(
                    "\$L = \$T.parseLong(\$L)",
                    field,
                    TypeName.LONG.box(),
                    value
                ).build() to CodeBlock.of("0L")
            }
            TypeName.FLOAT, TypeName.FLOAT.box() -> {
                CodeBlock.builder().addStatement(
                    "\$L = \$T.parseFloat(\$L)",
                    field,
                    TypeName.FLOAT.box(),
                    value
                ).build() to CodeBlock.of("0.0f")
            }
            TypeName.DOUBLE, TypeName.DOUBLE.box() -> {
                CodeBlock.builder().addStatement(
                    "\$L = \$T.parseDouble(\$L)",
                    field,
                    TypeName.DOUBLE.box(),
                    value
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

        val defaultAssignmentBlock = when {
            defaultValue != null -> {
                buildStandardDeserializeBlockForDefault(
                    defaultValue,
                    fieldType,
                    originatingElement
                )
            }
            nullable -> CodeBlock.of("null")
            else -> defaultValueBlock
        }

        return CodeBlock.builder()
            .beginControlFlow("try")
            .add(parseBlock)
            .nextControlFlow("catch (\$T e)", NumberFormatException::class.java)
            .addStatement("\$L = \$L", field, defaultAssignmentBlock)
            .endControlFlow()
            .build()
    }

    private fun buildCreateTypeAdapterBlock(typeAdapter: TypeMirror): CodeBlock {
        return if (typeAdapterFactory == null) {
            CodeBlock.of("new \$T()", TypeName.get(typeAdapter))
        } else {
            val typeAdapterName = MoreTypes.asTypeElement(typeAdapter).simpleName
            CodeBlock.of("\$T.create\$L()", typeAdapterFactory, typeAdapterName.toString())
        }
    }
}