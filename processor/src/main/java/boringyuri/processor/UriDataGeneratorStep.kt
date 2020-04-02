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

import boringyuri.api.Param
import boringyuri.api.Path
import boringyuri.api.adapter.BoringTypeAdapter
import boringyuri.api.adapter.TypeAdapter
import boringyuri.processor.base.AbortProcessingException
import boringyuri.processor.base.BoringProcessingStep
import boringyuri.processor.base.ProcessingSession
import boringyuri.processor.ext.valueMirror
import boringyuri.processor.util.CommonTypeName.*
import com.squareup.javapoet.*
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.type.TypeMirror

abstract class UriDataGeneratorStep protected constructor(
    session: ProcessingSession
) : BoringProcessingStep(session) {

    protected fun generateUriDataClassContent(
        className: ClassName,
        source: SourceElement
    ): TypeSpec {
        val classContent = TypeSpec.classBuilder(className)
            .addModifiers(Modifier.FINAL, Modifier.PUBLIC)

        source.superInterface()?.let { classContent.addSuperinterface(it) }

        val uriField = FieldSpec.builder(
            ANDROID_URI,
            URI_FIELD_NAME,
            Modifier.PRIVATE,
            Modifier.FINAL
        )
            .addAnnotation(NON_NULL)
            .build()
        val parseFlagField = FieldSpec.builder(
            TypeName.INT,
            PARSE_FLAG_NAME,
            Modifier.PRIVATE
        ).build()
        classContent.addField(uriField)
        classContent.addField(parseFlagField)

        var pathCounter = 0
        val paramData = arrayListOf<ParamData>()
        obtainParamData(source) { data ->
            if (data.paramType == ParamType.PATH) {
                pathCounter++
            }
            classContent.addField(data.paramElement.fieldSpec)
            paramData.add(data)
        }

        classContent.addMethod(generateConstructor(uriField))

        var pathIndex = 0
        for (data in paramData) {
            classContent.addMethod(
                generateGetterMethodImpl(
                    data,
                    uriField,
                    parseFlagField,
                    if (data.paramType == ParamType.PATH) pathIndex++ else -1,
                    pathCounter
                )
            )
        }

        onPostGenerateContent(classContent, source)

        classContent.addMethod(generateToString(uriField))

        return classContent.build()
    }

    protected open fun onPostGenerateContent(
        classContent: TypeSpec.Builder,
        source: SourceElement
    ) {
        // NO-OP
    }

    private fun obtainParamData(
        source: SourceElement,
        onParamAvailable: (ParamData) -> Unit
    ) {
        val parameters = source.obtainParamElements()
        parameters.forEachIndexed { index, param ->
            val paramElement = param.asElement()
            val type: ParamType? = when {
                paramElement.getAnnotation(Path::class.java) != null -> { ParamType.PATH }
                paramElement.getAnnotation(Param::class.java) != null -> { ParamType.QUERY }
                else -> null
            }

            if (type != null) {
                onParamAvailable(ParamData(1 shl index, type, param))
            } else {
                // skip unknown parameter
                session.logger.warn(
                    paramElement,
                    "%s must be annotated either with @%s or with @%s",
                    paramElement.simpleName,
                    Path::class.java.simpleName,
                    Param::class.java.simpleName
                )
            }
        }
    }

    private fun generateConstructor(uriField: FieldSpec): MethodSpec {
        val uriParam = ParameterSpec.builder(ANDROID_URI, "uri")
            .addAnnotations(uriField.annotations).build()

        return MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PUBLIC)
            .addParameter(uriParam)
            .addStatement("\$N = \$N", uriField, uriParam)
            .build()
    }

    private fun generateToString(uriField: FieldSpec): MethodSpec {
        return MethodSpec.methodBuilder("toString")
            .addAnnotation(OVERRIDE)
            .addAnnotation(NON_NULL)
            .addModifiers(Modifier.PUBLIC)
            .returns(STRING)
            .addStatement("return \$N.toString()", uriField)
            .build()
    }

    private fun generateGetterMethodImpl(
        data: ParamData,
        uriField: FieldSpec,
        parseFlagField: FieldSpec,
        pathIndex: Int,
        pathParamsCount: Int
    ): MethodSpec {
        val field = data.paramElement.fieldSpec
        val method = data.paramElement.createMethodSignature()

        method.beginControlFlow("if ((\$N & \$L) != 0)",
            parseFlagField,
            data.parseFlagValue
        )
        method.addStatement("return \$N", field)
        method.endControlFlow()

        method.addCode("\n")
        when (data.paramType) {
            ParamType.PATH -> method.addCode(
                generateParseFromPath(data, uriField, pathIndex, pathParamsCount)
            )
            ParamType.QUERY -> method.addCode(
                generateParseFromQuery(data, uriField)
            )
        }

        method.addCode("\n")
        method.addStatement("\$N |= \$L", parseFlagField, data.parseFlagValue)

        method.addCode("\n")
        method.addStatement("return \$N", field)

        return method.build()
    }

    private fun generateParseFromPath(
        paramData: ParamData,
        uriField: FieldSpec,
        pathIndex: Int,
        pathParamsCount: Int
    ): CodeBlock {
        val statement = CodeBlock.builder()
        val paramElement = paramData.paramElement
        val field = paramData.paramElement.fieldSpec
        val segmentsName = "segments"
        val segmentsIndexName = "pathSegmentIndex"

        statement.addStatement(
            "\$T \$L = \$N.getPathSegments()",
            ParameterizedTypeName.get(ClassName.get(List::class.java), STRING),
            segmentsName,
            uriField
        )
        statement.addStatement(
            "\$T \$L = \$L.size() - \$L",
            TypeName.INT,
            segmentsIndexName,
            segmentsName,
            pathParamsCount - pathIndex
        )

        val nullable = paramElement.isNullable
        statement.beginControlFlow("if (\$L < 0)", segmentsIndexName)
        if (nullable) {
            statement.addStatement("\$N = null", field)
        } else {
            statement.addStatement(
                "throw new \$T(\$S + \$N)",
                NullPointerException::class.java,
                CodeBlock.of("Segment '\$L' is not provided to ", paramElement.paramName),
                uriField
            )
        }
        statement.nextControlFlow("else")
        val pathSegmentName = "pathSegment"
        statement.addStatement(
            "\$T \$L = \$L.get(\$L)",
            STRING,
            pathSegmentName,
            segmentsName,
            segmentsIndexName
        )
        statement.add(generateDeserializeBlock(paramElement, pathSegmentName, nullable))
        statement.endControlFlow()

        return statement.build()
    }

    private fun generateParseFromQuery(paramData: ParamData, uriField: FieldSpec): CodeBlock {
        val statement = CodeBlock.builder()
        val paramElement = paramData.paramElement
        val field = paramElement.fieldSpec
        val paramAnnotation = paramElement.asElement().getAnnotation(Param::class.java)!!

        val paramLocalFieldName = paramElement.paramName
        val paramName = paramAnnotation.value.ifEmpty { paramLocalFieldName }

        statement.addStatement(
            "\$T \$L = \$N.getQueryParameter(\$S)",
            STRING,
            paramLocalFieldName,
            uriField,
            paramName
        )

        val nullable = paramElement.isNullable
        if (!field.type.isPrimitive && !nullable) {
            statement.beginControlFlow("if (\$L == null)", paramLocalFieldName)
            statement.addStatement(
                "throw new \$T(\$S + \$N)",
                NullPointerException::class.java,
                CodeBlock.of("Parameter '\$L' is not provided to ", paramName),
                uriField
            )
            statement.endControlFlow()
        }
        if (nullable) {
            statement.beginControlFlow("if (\$L == null)", paramLocalFieldName)
            statement.addStatement("\$N = null", field)
            statement.nextControlFlow("else")
        }
        statement.add(generateDeserializeBlock(paramElement, paramLocalFieldName, nullable))
        if (nullable) {
            statement.endControlFlow()
        }

        return statement.build()
    }

    private fun generateDeserializeBlock(
        paramElement: ParameterElement,
        localFieldName: String,
        nullable: Boolean
    ): CodeBlock {
        val field = paramElement.fieldSpec
        val fieldType = field.type
        val typeAdapter = paramElement.typeAdapter?.valueMirror()

        val deserializeBlock = CodeBlock.builder()

        if (typeAdapter != null) {
            deserializeBlock.add(
                generateCustomTypeDeserializeBlock(
                    field,
                    typeAdapter,
                    localFieldName
                )
            )
        } else {
            if (TypeName.BOOLEAN == fieldType || TypeName.BOOLEAN.box() == fieldType) {
                deserializeBlock.addStatement(
                    "$1N = \"true\".equalsIgnoreCase($2L) || \"1\".equals($2L)",
                    field,
                    localFieldName
                )
            } else if (TypeName.CHAR == fieldType || TypeName.CHAR.box() == fieldType) {
                deserializeBlock.addStatement(
                    "$1N = $2L.length() > 0 ? $2L.charAt(0) : $3L",
                    field,
                    localFieldName,
                    if (nullable) "null" else "'0'"
                )
            } else if (STRING == fieldType) {
                deserializeBlock.addStatement("\$N = \$L", field, localFieldName)
            } else if (ANDROID_URI == fieldType) {
                deserializeBlock.addStatement(
                    "\$N = \$T.parse(\$L)",
                    field,
                    ANDROID_URI,
                    localFieldName
                )
            } else if (fieldType.isPrimitive || fieldType.isBoxedPrimitive) {
                deserializeBlock.add(
                    generateNumberDeserializeBlock(
                        field,
                        paramElement,
                        localFieldName,
                        nullable
                    )
                )
            } else {
                throw AbortProcessingException(
                    session.logger,
                    paramElement.asElement(),
                    "Type $fieldType is not supported"
                )
            }
        }

        return deserializeBlock.build()
    }

    private fun generateCustomTypeDeserializeBlock(
        field: FieldSpec,
        typeAdapter: TypeMirror,
        localFieldName: String): CodeBlock {
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
            localFieldName
        )

        return deserializeBlock.build()
    }

    private fun generateNumberDeserializeBlock(
        field: FieldSpec,
        paramElement: ParameterElement,
        localFieldName: String,
        nullable: Boolean
    ): CodeBlock {
        val deserializeBlock = CodeBlock.builder()
        val fieldType = field.type

        deserializeBlock.beginControlFlow("try")
        val defaultTypeBlock: CodeBlock =
            if (TypeName.BYTE == fieldType || TypeName.BYTE.box() == fieldType) {
                deserializeBlock.addStatement(
                    "\$N = \$T.parseByte(\$L)",
                    field,
                    TypeName.BYTE.box(),
                    localFieldName
                )
                CodeBlock.of("(byte) 0")
            } else if (TypeName.SHORT == fieldType || TypeName.SHORT.box() == fieldType) {
                deserializeBlock.addStatement(
                    "\$N = \$T.parseShort(\$L)",
                    field,
                    TypeName.SHORT.box(),
                    localFieldName
                )
                CodeBlock.of("(short) 0")
            } else if (TypeName.INT == fieldType || TypeName.INT.box() == fieldType) {
                deserializeBlock.addStatement(
                    "\$N = \$T.parseInt(\$L)",
                    field,
                    TypeName.INT.box(),
                    localFieldName
                )
                CodeBlock.of("0")
            } else if (TypeName.LONG == fieldType || TypeName.LONG.box() == fieldType) {
                deserializeBlock.addStatement(
                    "\$N = \$T.parseLong(\$L)",
                    field,
                    TypeName.LONG.box(),
                    localFieldName
                )
                CodeBlock.of("0L")
            } else if (TypeName.FLOAT == fieldType || TypeName.FLOAT.box() == fieldType) {
                deserializeBlock.addStatement(
                    "\$N = \$T.parseFloat(\$L)",
                    field,
                    TypeName.FLOAT.box(),
                    localFieldName
                )
                CodeBlock.of("0.0f")
            } else if (TypeName.DOUBLE == fieldType || TypeName.DOUBLE.box() == fieldType) {
                deserializeBlock.addStatement(
                    "\$N = \$T.parseDouble(\$L)",
                    field,
                    TypeName.DOUBLE.box(),
                    localFieldName
                )
                CodeBlock.of("0.0")
            } else {
                throw AbortProcessingException(
                    session.logger,
                    paramElement.asElement(),
                    "Type $fieldType is not supported"
                )
            }
        deserializeBlock.nextControlFlow(
            "catch (\$T e)",
            NumberFormatException::class.java
        )
        deserializeBlock.addStatement(
            "\$N = \$L",
            field,
            if (nullable) CodeBlock.of("null") else defaultTypeBlock
        )
        deserializeBlock.endControlFlow()

        return deserializeBlock.build()
    }

    private enum class ParamType {
        PATH, QUERY
    }

    private data class ParamData(
        val parseFlagValue: Int,
        val paramType: ParamType,
        val paramElement: ParameterElement
    )

    protected interface SourceElement {
        fun superInterface(): TypeName?
        fun asElement(): Element
        fun obtainParamElements(): List<ParameterElement>
    }

    protected interface ParameterElement {
        val fieldSpec: FieldSpec
        val typeAdapter: TypeAdapter?
        val paramName: String
        val isNullable: Boolean

        fun asElement(): Element
        fun createMethodSignature(): MethodSpec.Builder
    }

    companion object {
        const val FIELD_PREFIX = "m"

        private const val URI_FIELD_NAME = "mDataUri"
        private const val PARSE_FLAG_NAME = "mParseFlag"
    }

}