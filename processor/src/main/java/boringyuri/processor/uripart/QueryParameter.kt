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
import boringyuri.processor.type.ConversionStrategyFactory
import boringyuri.processor.type.QueryConversionStrategy
import boringyuri.processor.type.TypeConverter
import boringyuri.processor.util.AnnotationHandler
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.MethodSpec
import javax.lang.model.element.ExecutableElement

interface QueryParameter {

    val name: String

    fun createValueBlock(typeConverter: TypeConverter): CodeBlock

}

interface ReadQueryParameter : QueryParameter {

    val paramField: FieldSpec

    fun createMethodSignature(annotationHandler: AnnotationHandler): MethodSpec.Builder

}

class MethodReadQueryParameter(
    name: String,
    paramField: FieldSpec,
    uriField: FieldSpec,
    nullable: Boolean,
    private val defaultValue: String?,
    private val parameterElement: ExecutableElement
) : BaseReadQueryParameter(name, paramField, uriField, nullable, defaultValue) {

    override fun createMethodSignature(
        annotationHandler: AnnotationHandler
    ): MethodSpec.Builder {
        return parameterElement.createMethodSignature(defaultValue, annotationHandler)
    }

    override fun createValueBlock(typeConverter: TypeConverter): CodeBlock {
        val deserializeStrategy = ConversionStrategyFactory.createQueryStrategy(
            parameterElement.returnType,
            parameterElement.findTypeAdapter()?.valueMirror(),
            typeConverter,
            parameterElement
        )

        return createValueBlock(deserializeStrategy)
    }

}

abstract class BaseReadQueryParameter(
    override val name: String,
    override val paramField: FieldSpec,
    private val uriField: FieldSpec,
    private val nullable: Boolean,
    private val defaultValue: String?
) : ReadQueryParameter {

    protected fun createValueBlock(deserializeStrategy: QueryConversionStrategy): CodeBlock {
        val statement = CodeBlock.builder()

        statement.add(deserializeStrategy.buildReadRawParameterBlock(name, uriField))

        if (!nullable && defaultValue == null) {
            statement.beginControlFlow(
                "if (\$L)", deserializeStrategy.buildCheckRawParameterBlock()
            )
            statement.addStatement(
                "throw new \$T(\$S + \$N)",
                NullPointerException::class.java,
                CodeBlock.of("Parameter '\$L' is not provided to ", name),
                uriField
            )
            statement.endControlFlow()
        } else {
            statement.beginControlFlow(
                "if (\$L)", deserializeStrategy.buildCheckRawParameterBlock()
            )
            if (defaultValue == null) {
                statement.addStatement("\$N = null", paramField)
            } else {
                statement.add(
                    deserializeStrategy.buildDeserializeDefaultBlock(
                        defaultValue,
                        paramField
                    )
                )
            }
            statement.nextControlFlow("else")
        }

        statement.add(
            deserializeStrategy.buildDeserializeBlock(
                paramField,
                nullable,
                defaultValue
            )
        )

        if (nullable || defaultValue != null) {
            statement.endControlFlow()
        }

        return statement.build()
    }

}