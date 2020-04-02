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
import boringyuri.api.UriData
import boringyuri.api.adapter.TypeAdapter
import boringyuri.processor.base.ProcessingSession
import boringyuri.processor.util.AnnotationHandler
import boringyuri.processor.util.Logger
import com.google.common.collect.ImmutableSet
import com.google.common.collect.SetMultimap
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import org.apache.commons.lang3.StringUtils
import javax.lang.model.element.*
import javax.lang.model.type.DeclaredType
import javax.lang.model.util.ElementFilter

class IndependentUriDataGeneratorStep(
    session: ProcessingSession,
    private val annotationHandler: AnnotationHandler
) : UriDataGeneratorStep(session) {

    override fun annotations(): Set<Class<out Annotation>> {
        return ImmutableSet.of(UriData::class.java)
    }

    override fun process(
        elementsByAnnotation: SetMultimap<Class<out Annotation>, Element>
    ): Set<Element> {
        val annotatedClasses = ElementFilter.typesIn(elementsByAnnotation[UriData::class.java])
        val deferred = hashSetOf<Element>()
        for (annotatedClass in annotatedClasses) {
            if (annotatedClass.kind != ElementKind.INTERFACE) {
                session.logger.warn(
                    annotatedClass,
                    "@%s can only be applied to interface",
                    UriData::class.java.simpleName
                )
                continue
            }

            val generated = generateUriDataClass(annotatedClass)
            if (!generated) {
                deferred.add(annotatedClass)
            }
        }

        return deferred
    }

    private fun generateUriDataClass(uriDataInterface: TypeElement): Boolean {
        val packageElement = session.elementUtils.getPackageOf(uriDataInterface)
        val packageName = packageElement.qualifiedName.toString()
        val simpleClassName = uriDataInterface.simpleName.toString() + CONTAINER_IMPL_SUFFIX
        val className = ClassName.get(packageName, simpleClassName)

        val content = generateUriDataClassContent(
            className,
            TypeSourceElement(uriDataInterface, annotationHandler, session.logger)
        )

        writeSourceFile(className, content, uriDataInterface)

        return true
    }

    private class TypeSourceElement internal constructor(
        private val element: TypeElement,
        private val annotationHandler: AnnotationHandler,
        private val logger: Logger
    ) : SourceElement {

        override fun superInterface(): TypeName = TypeName.get(element.asType())

        override fun asElement(): Element = element

        override fun obtainParamElements(): List<ParameterElement> {
            val methods = arrayListOf<ParameterElement>()
            val enclosedElements = element.enclosedElements
            for (element in enclosedElements) {
                if (ElementKind.METHOD != element.kind) {
                    continue  // skip non-method members
                }

                val modifiers = element.modifiers
                if (modifiers.contains(Modifier.STATIC) || modifiers.contains(Modifier.DEFAULT)) {
                    continue  // skip static or default methods
                }

                val methodElement = element as ExecutableElement
                val returnType = TypeName.get(methodElement.returnType)
                if (TypeName.VOID == returnType) {
                    if (element.getAnnotation(Path::class.java) != null
                        || element.getAnnotation(Param::class.java) != null
                    ) {
                        logger.warn(
                            element,
                            "@%s and @%s can be applied only to getter methods",
                            Path::class.java.simpleName,
                            Param::class.java.simpleName
                        )
                    }
                    continue  // skip unsupported method
                }

                methods.add(ExecutableParameterElement(methodElement, annotationHandler))
            }

            return methods
        }

    }

    private class ExecutableParameterElement internal constructor(
        private val element: ExecutableElement,
        private val annotationHandler: AnnotationHandler
    ) : ParameterElement {

        override fun asElement(): Element = element

        override val paramName by lazy { obtainParamName() }

        override val fieldSpec by lazy { createFieldSpec() }

        override val typeAdapter by lazy { obtainTypeAdapter() }

        override val isNullable: Boolean
            get() = annotationHandler.isNullable(element.returnType, element)

        override fun createMethodSignature(): MethodSpec.Builder {
            return MethodSpec.methodBuilder(element.simpleName.toString())
                .addModifiers(Modifier.PUBLIC)
                .addAnnotations(annotationHandler.toAnnotationSpec(element.annotationMirrors))
                .returns(fieldSpec.type)
        }

        private fun obtainParamName(): String {
            val methodName = element.simpleName.toString()
            val matcher = GETTER_PATTERN.matcher(methodName)

            return StringUtils.uncapitalize(
                if (matcher.matches()) matcher.group(1) else methodName
            )
        }

        private fun createFieldSpec(): FieldSpec {
            val fieldName = FIELD_PREFIX + StringUtils.capitalize(paramName)
            val returnType = TypeName.get(element.returnType)

            return FieldSpec.builder(returnType, fieldName, Modifier.PRIVATE)
                .addAnnotations(annotationHandler.toAnnotationSpec(element.annotationMirrors))
                .build()
        }

        private fun obtainTypeAdapter(): TypeAdapter? {
            val adapter = element.getAnnotation(TypeAdapter::class.java)
            if (adapter != null) {
                return adapter
            }

            val returnType = element.returnType

            return if (returnType is DeclaredType) {
                returnType.asElement().getAnnotation(TypeAdapter::class.java)
            } else null
        }

    }

    private companion object {
        const val CONTAINER_IMPL_SUFFIX = "Impl"
        val GETTER_PATTERN = "^(?:get|is|has|are)([A-Z][a-zA-Z0-9]+)$".toRegex().toPattern()
    }

}