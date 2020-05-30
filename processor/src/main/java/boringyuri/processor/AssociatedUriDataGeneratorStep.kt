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

import boringyuri.api.*
import boringyuri.api.constant.BooleanParam
import boringyuri.api.constant.DoubleParam
import boringyuri.api.constant.LongParam
import boringyuri.api.constant.StringParam
import boringyuri.processor.base.ProcessingSession
import boringyuri.processor.ext.createFieldSpec
import boringyuri.processor.uripart.ReadQueryParameter
import boringyuri.processor.uripart.TemplatePathSegment
import boringyuri.processor.uripart.VariableReadPathSegment
import boringyuri.processor.uripart.VariableReadQueryParameter
import boringyuri.processor.util.AnnotationHandler
import boringyuri.processor.type.CommonTypeName
import boringyuri.processor.util.ProcessorOptions
import boringyuri.processor.util.buildGetterName
import com.google.common.collect.ImmutableSet
import com.google.common.collect.SetMultimap
import com.squareup.javapoet.*
import org.apache.commons.lang3.StringUtils
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.util.ElementFilter

class AssociatedUriDataGeneratorStep(
    session: ProcessingSession,
    annotationHandler: AnnotationHandler
) : UriDataGeneratorStep(session, annotationHandler) {

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
                logger.warn(
                    annotatedMethod,
                    "@%s must be used only in combination with @%s",
                    WithUriData::class.java.simpleName,
                    UriBuilder::class.java.simpleName
                )
                continue  // skip invalid usage of @WithData
            }

            val uriMetadata = obtainUriMetadata(uriBuilder, annotatedMethod)

            val generated = generateUriDataClass(annotatedMethod, uriMetadata)
            if (!generated) {
                deferred.add(annotatedMethod)
            }
        }

        return deferred
    }

    private fun obtainUriMetadata(
        builderAnnotation: UriBuilder,
        methodElement: ExecutableElement
    ): UriMetadata {
        val basePath = builderAnnotation.value
        // Base path may contain constant segments and templates for method parameters.
        // We will replace the templates with the path parameters on the next step.
        // All constants will be filtered out on obtaining the base path segments.
        val segments = obtainBasePathSegments(basePath, methodElement)

        val methodParameters = methodElement.parameters
        val fieldSpecs = arrayListOf<FieldSpec>()
        val queryParams = arrayListOf<ReadQueryParameter>()
        // Iterating over method parameters we'll find all the replacements for
        // the method templates found on the previous step and create the params list.
        methodParameters.forEach { param ->
            val paramName = param.simpleName.toString()
            val defaultValue = param.getAnnotation(DefaultValue::class.java)?.value

            val field = param.createFieldSpec(
                paramName,
                defaultValue,
                annotationHandler
            ).also { fieldSpecs.add(it) }
            val nullable = annotationHandler.isNullable(field.type, param)

            val pathAnnotation = param.getAnnotation(Path::class.java)
            if (pathAnnotation != null) {
                if (nullable && defaultValue == null) {
                    logger.error(param, "Path segment '$paramName' must be explicitly non-null or" +
                            " or have a @${DefaultValue::class.simpleName}.")
                }

                val pathName = pathAnnotation.value.ifEmpty { paramName }
                val pathSegment = segments[pathName]
                val segmentIndex = if (pathSegment is TemplatePathSegment) {
                    pathSegment.segmentIndex
                } else {
                    ProcessorOptions.warnOrderedSegmentsUsage(
                        session,
                        pathName,
                        basePath,
                        UriBuilder::class,
                        param
                    )

                    // non-named path segment will be added to the end of the map
                    segments.size
                }
                // Previously saved VariableNameSegment helps to preserve
                // the segment position of the expected Uri path.
                segments[pathName] = VariableReadPathSegment(
                    segmentIndex,
                    pathName,
                    field,
                    uriField,
                    defaultValue,
                    param
                )
            } else {
                val paramAnnotation = param.getAnnotation(Param::class.java)
                if (paramAnnotation != null) {
                    val queryParamName = paramAnnotation.value.ifEmpty { paramName }
                    queryParams.add(
                        VariableReadQueryParameter(
                            queryParamName,
                            field,
                            uriField,
                            nullable,
                            defaultValue,
                            param
                        )
                    )
                } else {
                    logger.warn(param, "Parameter '$paramName' is ignored")
                }
            }
        }

        return UriMetadata(fieldSpecs, segments.values.toList(), queryParams)
    }

    private fun generateUriDataClass(
        sourceElement: ExecutableElement,
        uriMetadata: UriMetadata
    ): Boolean {
        val methodName = sourceElement.simpleName.toString()
        val matchResult = BUILDER_NAME_REGEX.find(methodName)

        val classSimpleName = StringUtils.capitalize(
            matchResult?.run { groupValues[1] } ?: methodName
        ) + DATA_SUFFIX

        val packageName = elementUtils.getPackageOf(sourceElement)
        val className = ClassName.get(packageName.qualifiedName.toString(), classSimpleName)

        val classContent = generateUriDataClassContent(
            className,
            sourceElement,
            uriMetadata
        )

        writeSourceFile(className, classContent, sourceElement)

        return true
    }

    override fun onPostGenerateContent(classContent: TypeSpec.Builder, sourceElement: Element) {
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
            val getterName = buildGetterName(constParam.name, CommonTypeName.STRING)

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
            val getterName = buildGetterName(constParam.name, TypeName.BOOLEAN)

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
            val getterName = buildGetterName(constParam.name, TypeName.LONG)

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
            val getterName = buildGetterName(constParam.name, TypeName.DOUBLE)

            classContent.addMethod(
                MethodSpec.methodBuilder(getterName)
                    .returns(TypeName.DOUBLE)
                    .addStatement("return \$L", constParam.value)
                    .build()
            )
        }
    }

    private companion object {
        val BUILDER_NAME_REGEX = "(?:build)?(\\w+)".toRegex()

        const val DATA_SUFFIX = "Data"
    }

}