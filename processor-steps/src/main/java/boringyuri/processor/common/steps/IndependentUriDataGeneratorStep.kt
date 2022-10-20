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
package boringyuri.processor.common.steps

import androidx.room.compiler.processing.ExperimentalProcessingApi
import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XFiler
import androidx.room.compiler.processing.XMethodElement
import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XTypeElement
import androidx.room.compiler.processing.isTypeElement
import boringyuri.api.DefaultValue
import boringyuri.api.Param
import boringyuri.api.Path
import boringyuri.api.UriData
import boringyuri.processor.common.base.ProcessingSession
import boringyuri.processor.common.ext.getAnnotation
import boringyuri.processor.common.ext.requireAnnotation
import boringyuri.processor.common.steps.ext.createFieldSpec
import boringyuri.processor.common.steps.uripart.MethodReadPathSegment
import boringyuri.processor.common.steps.uripart.MethodReadQueryParameter
import boringyuri.processor.common.steps.uripart.ReadQueryParameter
import boringyuri.processor.common.steps.uripart.TemplatePathSegment
import boringyuri.processor.common.steps.util.AnnotationHandler
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.TypeName
import org.apache.commons.lang3.StringUtils
import kotlin.collections.set

@OptIn(ExperimentalProcessingApi::class)
class IndependentUriDataGeneratorStep(
    session: ProcessingSession,
    annotationHandler: AnnotationHandler
) : UriDataGeneratorStep(session, annotationHandler) {

    override fun annotations(): Set<String> {
        return setOf(UriData::class.java.name)
    }

    override fun process(
        env: XProcessingEnv,
        elementsByAnnotation: Map<String, Set<XElement>>
    ): Set<XElement> {
        val annotatedClasses =
            elementsByAnnotation[UriData::class.java.name]
                ?.filter { it.isTypeElement() }
                ?.mapNotNull { it as? XTypeElement }
                ?: emptyList()

        val deferred = hashSetOf<XElement>()
        for (annotatedClass in annotatedClasses) {
            if (!annotatedClass.isInterface()) {
                logger.warn(
                    annotatedClass,
                    "@%s can only be applied to interface",
                    UriData::class.java.simpleName
                )
                continue
            }

            if (annotatedClass.isPrivate()) {
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

    private fun obtainUriMetadata(sourceElement: XTypeElement): UriMetadata {
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
            val methodName = method.name
            val paramName = StringUtils.uncapitalize(
                GETTER_PATTERN.find(methodName)?.run { groupValues[1] } ?: methodName
            )

            val defaultValue = method.getAnnotation<DefaultValue>()?.value
            val field = method.createFieldSpec(
                paramName,
                defaultValue,
                annotationHandler
            ).also { fieldSpecs.add(it) }
            val nullable = annotationHandler.isNullable(method.returnType.typeName, method)

            val pathAnnotation = method.getAnnotation<Path>()
            if (pathAnnotation != null) {
                if (nullable) {
                    logger.error(
                        method, "Return type of the path segment getter '$methodName()'" +
                                " must be explicitly non-null."
                    )
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

    private fun collectMethodsToImplement(sourceElement: XTypeElement): List<XMethodElement> {
        val allDefinedMethods = sourceElement.getAllMethods()

        return allDefinedMethods.filter { canBeImplemented(it) }.toList()
    }

    private fun canBeImplemented(method: XMethodElement): Boolean {
        if (method.isStatic() || method.isJavaDefault() || method.hasKotlinDefaultImpl()) {
            return false  // skip static or default methods
        }

        if (method.getAnnotation<Path>() == null && method.getAnnotation<Param>() == null) {
            logger.error(
                method,
                "'${method.name}' must have either @%s or @%s",
                Path::class.simpleName,
                Param::class.simpleName
            )
            return false // skip not annotated method
        }

        val returnType = method.returnType.typeName
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
            val parameters = method.parameters.joinToString { it.name }
            logger.warn(method, "Method parameters [$parameters] will be ignored")
        }

        return true
    }

    private fun generateUriDataClass(
        sourceElement: XTypeElement,
        uriMetadata: UriMetadata
    ): Boolean {
        val packageName = sourceElement.packageName
        val simpleClassName = sourceElement.name + CONTAINER_IMPL_SUFFIX
        val className = ClassName.get(packageName, simpleClassName)

        val content = generateUriDataClassContent(
            className,
            sourceElement,
            uriMetadata,
            sourceElement.type.typeName
        )

        session.fileWriter.writeSourceFile(className, content, XFiler.Mode.Isolating)

        return true
    }

    companion object {
        private const val CONTAINER_IMPL_SUFFIX = "Impl"
        private val GETTER_PATTERN = "^(?:get|is|has|are)([A-Z][a-zA-Z0-9]+)$".toRegex()

        fun create(session: ProcessingSession): IndependentUriDataGeneratorStep {
            return IndependentUriDataGeneratorStep(session, AnnotationHandler(INDEPENDENT_DATA_ANNOTATIONS))
        }
    }

}