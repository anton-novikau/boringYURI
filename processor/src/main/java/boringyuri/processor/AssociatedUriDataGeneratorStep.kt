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

import boringyuri.api.UriBuilder
import boringyuri.api.WithUriData
import boringyuri.api.adapter.TypeAdapter
import boringyuri.api.constant.BooleanParam
import boringyuri.api.constant.DoubleParam
import boringyuri.api.constant.LongParam
import boringyuri.api.constant.StringParam
import boringyuri.processor.base.AbortProcessingException
import boringyuri.processor.base.ProcessingSession
import boringyuri.processor.util.AnnotationHandler
import boringyuri.processor.util.CommonTypeName
import com.google.common.collect.ImmutableSet
import com.google.common.collect.SetMultimap
import com.squareup.javapoet.*
import org.apache.commons.lang3.StringUtils
import org.apache.commons.text.CaseUtils
import java.util.*
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.element.VariableElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.util.ElementFilter

class AssociatedUriDataGeneratorStep(
    session: ProcessingSession,
    private val annotationHandler: AnnotationHandler
) : UriDataGeneratorStep(session) {

    override fun annotations(): Set<Class<out Annotation>> {
        return ImmutableSet.of(WithUriData::class.java)
    }

    override fun process(elementsByAnnotation: SetMultimap<Class<out Annotation>, Element>): Set<Element> {
        val annotatedMethods = ElementFilter.methodsIn(
            elementsByAnnotation[WithUriData::class.java]
        )
        val deferred = hashSetOf<Element>()
        for (annotatedMethod in annotatedMethods) {
            val uriBuilder = annotatedMethod.getAnnotation(UriBuilder::class.java)
            if (uriBuilder == null) {
                session.logger.warn(
                    annotatedMethod,
                    "@%s must be used only in combination with @%s",
                    WithUriData::class.java.simpleName,
                    UriBuilder::class.java.simpleName
                )
                continue  // skip invalid usage of @WithData
            }

            val generated = generateUriDataClass(annotatedMethod)
            if (!generated) {
                deferred.add(annotatedMethod)
            }
        }

        return deferred
    }

    private fun generateUriDataClass(annotatedMethod: ExecutableElement): Boolean {
        val nameMatcher = BUILDER_NAME_REGEX.matcher(annotatedMethod.simpleName)
        if (!nameMatcher.find()) {
            throw AbortProcessingException(
                session.logger,
                annotatedMethod,
                "Builder method must start with 'build'"
            )
        }

        val classSimpleName = StringUtils.capitalize(nameMatcher.group(1)) + DATA_SUFFIX
        val packageName = session.elementUtils.getPackageOf(annotatedMethod)
        val className = ClassName.get(packageName.qualifiedName.toString(), classSimpleName)

        val classContent = generateUriDataClassContent(
            className,
            ExecutableSourceElement(annotatedMethod, annotationHandler)
        )

        writeSourceFile(className, classContent, annotatedMethod)

        return true
    }

    override fun onPostGenerateContent(classContent: TypeSpec.Builder, source: SourceElement) {
        val sourceElement = source.asElement()

        generateStringConstParamsGetters(classContent, sourceElement)
        generateBooleanConstParamsGetters(classContent, sourceElement)
        generateLongConstParamsGetters(classContent, sourceElement)
        generateDoubleConstParamsGetters(classContent, sourceElement)
    }

    private fun generateStringConstParamsGetters(
        classContent: TypeSpec.Builder,
        sourceElement: Element
    ) {
        val constParams = sourceElement.getAnnotationsByType(StringParam::class.java) ?: return

        for (constParam in constParams) {
            val getterName = buildGetterName(GETTER_PREFIX, constParam.name)

            classContent.addMethod(
                MethodSpec.methodBuilder(getterName)
                    .addAnnotation(CommonTypeName.NON_NULL)
                    .returns(CommonTypeName.STRING)
                    .addStatement("return \$S", constParam.value)
                    .build()
            )
        }
    }

    private fun generateBooleanConstParamsGetters(
        classContent: TypeSpec.Builder,
        sourceElement: Element
    ) {
        val constParams = sourceElement.getAnnotationsByType(BooleanParam::class.java) ?: return

        for (constParam in constParams) {
            val paramName = constParam.name
            val getterName = if (BOOLEAN_GETTER_PATTERN.matches(paramName)) {
                paramName
            } else {
                buildGetterName(BOOLEAN_GETTER_PREFIX, paramName)
            }

            classContent.addMethod(
                MethodSpec.methodBuilder(getterName)
                    .returns(TypeName.BOOLEAN)
                    .addStatement("return \$L", constParam.value)
                    .build()
            )
        }
    }

    private fun generateLongConstParamsGetters(
        classContent: TypeSpec.Builder,
        sourceElement: Element
    ) {
        val constParams = sourceElement.getAnnotationsByType(LongParam::class.java) ?: return

        for (constParam in constParams) {
            val getterName = buildGetterName(GETTER_PREFIX, constParam.name)

            classContent.addMethod(
                MethodSpec.methodBuilder(getterName)
                    .returns(TypeName.LONG)
                    .addStatement("return \$L", constParam.value)
                    .build()
            )
        }
    }

    private fun generateDoubleConstParamsGetters(
        classContent: TypeSpec.Builder,
        sourceElement: Element
    ) {
        val constParams = sourceElement.getAnnotationsByType(DoubleParam::class.java) ?: return

        for (constParam in constParams) {
            val getterName = buildGetterName(GETTER_PREFIX, constParam.name)

            classContent.addMethod(
                MethodSpec.methodBuilder(getterName)
                    .returns(TypeName.DOUBLE)
                    .addStatement("return \$L", constParam.value)
                    .build()
            )
        }
    }

    private fun buildGetterName(prefix: String, paramName: String): String {
        val getter = StringBuilder(prefix)
        if (paramName.contains("_")) {
            getter.append(CaseUtils.toCamelCase(paramName, true, '_'))
        } else {
            getter.append(StringUtils.capitalize(paramName))
        }

        return getter.toString()
    }

    private class ExecutableSourceElement internal constructor(
        private val element: ExecutableElement,
        private val annotationHandler: AnnotationHandler
    ) : SourceElement {

        // the generated data class doesn't have any interface to implement
        override fun superInterface(): TypeName? = null

        override fun asElement(): Element = element

        override fun obtainParamElements(): List<ParameterElement> {
            val parameters = element.parameters
            val parameterWrappers = ArrayList<ParameterElement>(parameters.size)
            for (parameter in parameters) {
                parameterWrappers.add(VariableParameterElement(parameter, annotationHandler))
            }

            return parameterWrappers
        }
    }

    private class VariableParameterElement internal constructor(
        private val element: VariableElement,
        private val annotationHandler: AnnotationHandler
    ) : ParameterElement {

        override fun asElement(): Element = element

        override val paramName: String
            get() = element.simpleName.toString()

        override val fieldSpec by lazy { createFieldSpec() }

        override val typeAdapter by lazy { obtainTypeAdapter() }

        override val isNullable: Boolean
            get() = annotationHandler.isNullable(element.asType(), element)

        override fun createMethodSignature(): MethodSpec.Builder {
            val paramName = paramName
            val paramType = TypeName.get(element.asType())
            val isBoolType = TypeName.BOOLEAN == paramType || TypeName.BOOLEAN.box() == paramType
            val methodName = if (isBoolType) {
                if (BOOLEAN_GETTER_PATTERN.matches(paramName)) {
                    paramName
                } else {
                    BOOLEAN_GETTER_PREFIX + StringUtils.capitalize(paramName)
                }
            } else {
                GETTER_PREFIX + StringUtils.capitalize(paramName)
            }

            return MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotations(annotationHandler.toAnnotationSpec(element.annotationMirrors))
                .returns(paramType)
        }

        private fun createFieldSpec(): FieldSpec {
            val fieldName = FIELD_PREFIX + StringUtils.capitalize(paramName)

            return FieldSpec.builder(TypeName.get(element.asType()), fieldName, Modifier.PRIVATE)
                .addAnnotations(annotationHandler.toAnnotationSpec(element.annotationMirrors))
                .build()
        }

        private fun obtainTypeAdapter(): TypeAdapter? {
            val adapter = element.getAnnotation(TypeAdapter::class.java)
            if (adapter != null) {
                return adapter
            }

            val paramType = element.asType()

            return if (paramType is DeclaredType) {
                paramType.asElement().getAnnotation(TypeAdapter::class.java)
            } else null
        }
    }

    private companion object {
        val BUILDER_NAME_REGEX = "(?:build)?(\\w+)".toRegex().toPattern()
        val BOOLEAN_GETTER_PATTERN = "^(?:is|has|are|can)[A-Z]\\w+$".toRegex()

        const val DATA_SUFFIX = "Data"
        const val GETTER_PREFIX = "get"
        const val BOOLEAN_GETTER_PREFIX = "is"
    }

}