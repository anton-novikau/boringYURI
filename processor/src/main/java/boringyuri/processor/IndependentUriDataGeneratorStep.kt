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

import boringyuri.api.DefaultValue
import boringyuri.api.Param
import boringyuri.api.Path
import boringyuri.api.UriData
import boringyuri.processor.base.ProcessingSession
import boringyuri.processor.ext.createFieldSpec
import boringyuri.processor.ext.getAnnotation
import boringyuri.processor.ext.requireAnnotation
import boringyuri.processor.uripart.MethodReadPathSegment
import boringyuri.processor.uripart.MethodReadQueryParameter
import boringyuri.processor.uripart.ReadQueryParameter
import boringyuri.processor.uripart.TemplatePathSegment
import boringyuri.processor.util.AnnotationHandler
import com.google.auto.common.MoreElements
import com.google.common.collect.ImmutableSet
import com.google.common.collect.ImmutableSetMultimap
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.TypeName
import org.apache.commons.lang3.StringUtils
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.util.ElementFilter

class IndependentUriDataGeneratorStep(
    session: ProcessingSession,
    annotationHandler: AnnotationHandler
) : UriDataGeneratorStep(session, annotationHandler) {

    override fun annotations(): Set<String> {
        return ImmutableSet.of(UriData::class.java.name)
    }

    override fun process(
        elementsByAnnotation: ImmutableSetMultimap<String, Element>
    ): Set<Element> {
        val annotatedClasses = ElementFilter.typesIn(elementsByAnnotation[UriData::class.java.name])
        val deferred = hashSetOf<Element>()
        for (annotatedClass in annotatedClasses) {
            if (annotatedClass.kind != ElementKind.INTERFACE) {
                logger.warn(
                    annotatedClass,
                    "@%s can only be applied to interface",
                    UriData::class.java.simpleName
                )
                continue
            }

            val modifiers = annotatedClass.modifiers
            if (modifiers.contains(Modifier.PRIVATE)) {
                logger.warn(
                    annotatedClass,
                    "@%s can not be applied to a private interface",
                    UriData::class.simpleName
                )
                continue
            }

            val uriMetadata = obtainUriMetadata(annotatedClass)

            val generated = generateUriDataClass(annotatedClass, uriMetadata)
            if (!generated) {
                deferred.add(annotatedClass)
            }
        }

        return deferred
    }

    private fun obtainUriMetadata(sourceElement: TypeElement): UriMetadata {
        val uriDataAnnotation = sourceElement.requireAnnotation<UriData>()

        val basePath = uriDataAnnotation.value
        // Base path may contain constant segments, wildcard segments and templates
        // for method parameters. We will replace the templates with the path parameters
        // on the next step. All constants and wildcards will be filtered out on obtaining
        // the base path segments.
        val segments = obtainBasePathSegments(basePath, sourceElement)
        val fieldSpecs = arrayListOf<FieldSpec>()
        val queryParams = arrayListOf<ReadQueryParameter>()

        for (method in collectMethodsToImplement(sourceElement)) {
            val methodName = method.simpleName.toString()
            val paramName = StringUtils.uncapitalize(
                GETTER_PATTERN.find(methodName)?.run { groupValues[1] } ?: methodName
            )

            val defaultValue = method.getAnnotation<DefaultValue>()?.value
            val field = method.createFieldSpec(
                paramName,
                defaultValue,
                annotationHandler
            ).also { fieldSpecs.add(it) }
            val nullable = annotationHandler.isNullable(method.returnType, method)

            val pathAnnotation = method.getAnnotation<Path>()
            if (pathAnnotation != null) {
                if (nullable) {
                    logger.error(method, "Return type of the path segment getter '$methodName()'" +
                            " must be explicitly non-null.")
                }

                val pathName = pathAnnotation.value.ifEmpty { paramName }
                val pathSegment = segments[pathName]
                if (pathSegment is TemplatePathSegment) {

                    // Previously saved VariableNameSegment helps to preserve
                    // the segment position of the expected Uri path.
                    segments[pathName] = MethodReadPathSegment(
                        pathSegment.segmentIndex,
                        pathName,
                        field,
                        uriField,
                        defaultValue,
                        method
                    )
                } else {
                    logger.error(
                        method,
                        "Path segment {$pathName} is not defined in '$basePath'"
                    )
                }

            } else {
                val paramAnnotation = method.requireAnnotation<Param>()
                val queryParamName = paramAnnotation.value.ifEmpty { paramName }
                queryParams.add(
                    MethodReadQueryParameter(
                        queryParamName,
                        field,
                        uriField,
                        nullable,
                        defaultValue,
                        method
                    )
                )
            }
        }

        return UriMetadata(fieldSpecs, segments.values.toList(), queryParams)
    }

    private fun collectMethodsToImplement(sourceElement: TypeElement): List<ExecutableElement> {
        @Suppress("UnstableApiUsage")
        val allDefinedMethods = MoreElements.getAllMethods(sourceElement, typeUtils, elementUtils)

        return allDefinedMethods.filter { canBeImplemented(it) }
    }

    private fun canBeImplemented(method: ExecutableElement): Boolean {
        val modifiers = method.modifiers
        if (modifiers.contains(Modifier.STATIC) || modifiers.contains(Modifier.DEFAULT)) {
            return false  // skip static or default methods
        }

        if (method.getAnnotation<Path>() == null && method.getAnnotation<Param>() == null) {
            logger.error(
                method,
                "'${method.simpleName}' must have either @%s or @%s",
                Path::class.simpleName,
                Param::class.simpleName
            )
            return false // skip not annotated method
        }

        val returnType = TypeName.get(method.returnType)
        if (TypeName.VOID == returnType) {
            logger.error(
                method,
                "Method annotated with @%s or @%s can not return 'void'",
                Path::class.simpleName,
                Param::class.simpleName
            )
            return false  // skip unsupported method
        }

        if (method.parameters.isNotEmpty()) {
            val parameters = method.parameters.joinToString { it.simpleName }
            logger.warn(method, "Method parameters [$parameters] will be ignored")
        }

        return true
    }

    private fun generateUriDataClass(
        sourceElement: TypeElement,
        uriMetadata: UriMetadata
    ): Boolean {
        val packageElement = elementUtils.getPackageOf(sourceElement)
        val packageName = packageElement.qualifiedName.toString()
        val simpleClassName = sourceElement.simpleName.toString() + CONTAINER_IMPL_SUFFIX
        val className = ClassName.get(packageName, simpleClassName)

        val content = generateUriDataClassContent(
            className,
            sourceElement,
            uriMetadata,
            TypeName.get(sourceElement.asType())
        )

        writeSourceFile(className, content, sourceElement)

        return true
    }

    private companion object {
        const val CONTAINER_IMPL_SUFFIX = "Impl"
        val GETTER_PATTERN = "^(?:get|is|has|are)([A-Z][a-zA-Z0-9]+)$".toRegex()
    }

}