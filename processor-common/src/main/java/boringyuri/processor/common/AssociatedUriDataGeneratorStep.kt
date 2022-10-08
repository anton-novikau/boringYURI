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
package boringyuri.processor.common

import androidx.room.compiler.processing.ExperimentalProcessingApi
import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XExecutableElement
import androidx.room.compiler.processing.XFiler
import androidx.room.compiler.processing.XMethodElement
import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XTypeElement
import androidx.room.compiler.processing.isMethod
import boringyuri.api.DefaultValue
import boringyuri.api.Param
import boringyuri.api.Path
import boringyuri.api.UriBuilder
import boringyuri.api.WithUriData
import boringyuri.api.constant.BooleanParam
import boringyuri.api.constant.DoubleParam
import boringyuri.api.constant.LongParam
import boringyuri.api.constant.StringParam
import boringyuri.processor.common.base.ProcessingSession
import boringyuri.processor.common.ext.createFieldSpec
import boringyuri.processor.common.ext.extractPackage
import boringyuri.processor.common.type.CommonTypeName
import boringyuri.processor.common.uripart.ReadQueryParameter
import boringyuri.processor.common.uripart.TemplatePathSegment
import boringyuri.processor.common.uripart.VariableReadPathSegment
import boringyuri.processor.common.uripart.VariableReadQueryParameter
import boringyuri.processor.common.util.AnnotationHandler
import boringyuri.processor.common.util.buildGetterName
import com.squareup.javapoet.ArrayTypeName
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import org.apache.commons.lang3.StringUtils
import javax.lang.model.element.Modifier

@OptIn(ExperimentalProcessingApi::class)
class AssociatedUriDataGeneratorStep(
    session: ProcessingSession,
    annotationHandler: AnnotationHandler
) : UriDataGeneratorStep(session, annotationHandler) {

    override fun annotations(): Set<String> {
        return setOf(WithUriData::class.java.name)
    }

    override fun process(
        env: XProcessingEnv,
        elementsByAnnotation: Map<String, Set<XElement>>
    ): Set<XElement> {
        val annotatedMethods = elementsByAnnotation[WithUriData::class.java.name]
            ?.filter { it.isMethod() }
            ?.map { it as XMethodElement }
            ?: emptyList()

        val deferred = hashSetOf<XElement>()
        for (annotatedMethod in annotatedMethods) {
            if(!annotatedMethod.validate()){
                deferred.add(annotatedMethod)
                continue
            }

            val uriBuilder = annotatedMethod.getAnnotation(UriBuilder::class)?.value
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
        methodElement: XExecutableElement
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
            val paramName = param.name
            val defaultValue = param.getAnnotation(DefaultValue::class)?.value

            val field = param.createFieldSpec(
                paramName,
                defaultValue?.value,
                annotationHandler
            ).also { fieldSpecs.add(it) }

            val nullable = annotationHandler.isNullable(field.type, param)

            val pathAnnotation = param.getAnnotation(Path::class)
            if (pathAnnotation != null) {
                if (nullable && defaultValue == null) {
                    logger.error(param, param.type.nullability.toString())
                    logger.error(
                        param, "Path segment '$paramName' must be explicitly non-null or" +
                                " or have a @${DefaultValue::class.simpleName}."
                    )
                }

                val pathName = pathAnnotation.value.value.ifEmpty { paramName }
                val pathSegment = segments[pathName]
                if (pathSegment is TemplatePathSegment) {
                    // Previously saved VariableNameSegment helps to preserve
                    // the segment position of the expected Uri path.
                    segments[pathName] = VariableReadPathSegment(
                        pathSegment.segmentIndex,
                        pathName,
                        field,
                        uriField,
                        defaultValue?.value,
                        param
                    )
                }
            } else {
                val paramAnnotation = param.getAnnotation(Param::class)?.value
                if (paramAnnotation != null) {
                    val queryParamName = paramAnnotation.value.ifEmpty { paramName }
                    queryParams.add(
                        VariableReadQueryParameter(
                            queryParamName,
                            field,
                            uriField,
                            nullable,
                            defaultValue?.value,
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
        sourceElement: XMethodElement,
        uriMetadata: UriMetadata
    ): Boolean {
        val withUriDataAnnotation = sourceElement.requireAnnotation(WithUriData::class)
        val desiredClassName = withUriDataAnnotation.value.value

        val className = if (desiredClassName.isEmpty()) {
            val methodName = sourceElement.name
            val matchResult = BUILDER_NAME_REGEX.find(methodName)

            val classSimpleName = StringUtils.capitalize(
                matchResult?.run { groupValues[1] } ?: methodName
            ) + DATA_SUFFIX

            val packageName = sourceElement.extractPackage()

            ClassName.get(packageName, classSimpleName)
        } else {
            ClassName.bestGuess(desiredClassName).takeIf {
                it.packageName().isNotEmpty()
            } ?: ClassName.get(
                (sourceElement.enclosingElement as? XTypeElement)?.packageName,
                desiredClassName
            )
        }

        val classContent = generateUriDataClassContent(
            className,
            sourceElement,
            uriMetadata
        )

        session.fileWriter.writeSourceFile(className, classContent, XFiler.Mode.Isolating)

        return true
    }

    override fun onPostGenerateContent(classContent: TypeSpec.Builder, sourceElement: XElement) {
        generateStringConstParamsGetters(classContent, sourceElement)
        generateBooleanConstParamsGetters(classContent, sourceElement)
        generateLongConstParamsGetters(classContent, sourceElement)
        generateDoubleConstParamsGetters(classContent, sourceElement)
    }

    private fun generateStringConstParamsGetters(
        classContent: TypeSpec.Builder,
        sourceElement: XElement
    ) {
        val constParams = sourceElement.getAnnotations(StringParam::class)
            .map { it.value }

        constParams.groupBy({ it.name }, { it.value }).forEach { (name, params) ->
            generateConstParamGetter(classContent, CommonTypeName.STRING, name, params) {
                CodeBlock.of("\$S", it)
            }
        }
    }

    private fun generateBooleanConstParamsGetters(
        classContent: TypeSpec.Builder,
        sourceElement: XElement
    ) {
        val constParams = sourceElement.getAnnotations(BooleanParam::class)
            .map { it.value }

        constParams.groupBy({ it.name }, { it.value }).forEach { (name, params) ->
            generateConstParamGetter(classContent, TypeName.BOOLEAN, name, params)
        }
    }

    private fun generateLongConstParamsGetters(
        classContent: TypeSpec.Builder,
        sourceElement: XElement
    ) {
        val constParams = sourceElement.getAnnotations(LongParam::class).map { it.value }

        constParams.groupBy({ it.name }, { it.value }).forEach { (name, params) ->
            generateConstParamGetter(classContent, TypeName.LONG, name, params)
        }
    }

    private fun generateDoubleConstParamsGetters(
        classContent: TypeSpec.Builder,
        sourceElement: XElement
    ) {
        val constParams = sourceElement.getAnnotations(DoubleParam::class).map { it.value }

        constParams.groupBy({ it.name }, { it.value }).forEach { (name, params) ->
            generateConstParamGetter(classContent, TypeName.DOUBLE, name, params)
        }
    }

    private inline fun generateConstParamGetter(
        classContent: TypeSpec.Builder,
        type: TypeName,
        name: String,
        params: List<Any>,
        crossinline transform: (value: Any) -> Any = { it }
    ) {
        if (params.size == 1) {
            val getterName = buildGetterName(name, type)
            classContent.addMethod(
                MethodSpec.methodBuilder(getterName)
                    .apply { if (!type.isPrimitive) addAnnotation(CommonTypeName.NON_NULL) }
                    .addModifiers(Modifier.PUBLIC)
                    .returns(type)
                    .addStatement("return \$L", transform(params[0]))
                    .build()
            )
        } else if (params.size > 1) {
            val arrayType = ArrayTypeName.of(type)
            val getterName = buildGetterName(name, arrayType)
            classContent.addMethod(
                MethodSpec.methodBuilder(getterName)
                    .addAnnotation(CommonTypeName.NON_NULL)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(arrayType)
                    .addStatement("return new \$T[] { \$L }", type, params.joinToString {
                        transform(it).toString()
                    }).build()
            )
        } // else never happens
    }

    private companion object {
        val BUILDER_NAME_REGEX = "(?:build)?(\\w+)".toRegex()

        const val DATA_SUFFIX = "Data"
    }
}