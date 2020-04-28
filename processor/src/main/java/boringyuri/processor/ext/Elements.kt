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

package boringyuri.processor.ext

import boringyuri.api.adapter.TypeAdapter
import boringyuri.processor.util.AnnotationHandler
import boringyuri.processor.util.buildGetterName
import com.squareup.javapoet.*
import org.apache.commons.lang3.StringUtils
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.element.VariableElement
import javax.lang.model.type.DeclaredType

private const val FIELD_PREFIX = "m"

fun VariableElement.createParamSpec(annotationHandler: AnnotationHandler): ParameterSpec {
    val paramType = ClassName.get(asType())
    val paramName = simpleName.toString()

    return ParameterSpec.builder(paramType, paramName)
        .addModifiers(modifiers)
        .addAnnotations(annotationHandler.toAnnotationSpec(annotationMirrors))
        .build()
}

fun VariableElement.createFieldSpec(
    paramName: String,
    annotationHandler: AnnotationHandler
): FieldSpec {
    val fieldName = FIELD_PREFIX + StringUtils.capitalize(paramName)

    return FieldSpec.builder(TypeName.get(asType()), fieldName, Modifier.PRIVATE)
        .addAnnotations(annotationHandler.toAnnotationSpec(annotationMirrors))
        .build()
}

fun ExecutableElement.createFieldSpec(
    paramName: String,
    annotationHandler: AnnotationHandler
): FieldSpec {
    val fieldName = FIELD_PREFIX + StringUtils.capitalize(paramName)

    return FieldSpec.builder(TypeName.get(returnType), fieldName, Modifier.PRIVATE)
        .addAnnotations(annotationHandler.toAnnotationSpec(annotationMirrors))
        .build()
}

fun VariableElement.createMethodSignature(
    annotationHandler: AnnotationHandler
): MethodSpec.Builder {
    val paramName = simpleName.toString()
    val paramType = TypeName.get(asType())
    val methodName = buildGetterName(paramName, paramType)

    return MethodSpec.methodBuilder(methodName)
        .addModifiers(Modifier.PUBLIC)
        .addAnnotations(annotationHandler.toAnnotationSpec(annotationMirrors))
        .returns(paramType)
}

fun ExecutableElement.createMethodSignature(
    annotationHandler: AnnotationHandler
): MethodSpec.Builder {
    return MethodSpec.methodBuilder(simpleName.toString())
        .addModifiers(Modifier.PUBLIC)
        .addAnnotations(annotationHandler.toAnnotationSpec(annotationMirrors))
        .addParameters(parameters.map { it.createParamSpec(annotationHandler) })
        .returns(TypeName.get(returnType))
}

fun VariableElement.findTypeAdapter(): TypeAdapter? {
    val adapter = getAnnotation(TypeAdapter::class.java)
    if (adapter != null) {
        return adapter
    }

    val type = asType()

    return if (type is DeclaredType) {
        type.asElement().getAnnotation(TypeAdapter::class.java)
    } else null
}

fun ExecutableElement.findTypeAdapter(): TypeAdapter? {
    val adapter = getAnnotation(TypeAdapter::class.java)
    if (adapter != null) {
        return adapter
    }

    val type = returnType

    return if (type is DeclaredType) {
        type.asElement().getAnnotation(TypeAdapter::class.java)
    } else null
}