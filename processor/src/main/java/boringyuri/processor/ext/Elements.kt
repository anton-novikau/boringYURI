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
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeName
import org.apache.commons.lang3.StringUtils
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.element.VariableElement
import javax.lang.model.type.ArrayType
import javax.lang.model.type.DeclaredType
import javax.lang.model.util.SimpleTypeVisitor8

private const val FIELD_PREFIX = "m"

inline fun <reified T : Annotation> Element.getAnnotation(): T? = getAnnotation(T::class.java)

inline fun <reified T : Annotation> Element.requireAnnotation(): T {
    return requireNotNull(getAnnotation(T::class.java)) {
        "Annotation @${T::class.simpleName} is missing in $simpleName"
    }
}

inline fun <reified T : Annotation> Element.getAnnotationsByType(): Array<T>? {
    return getAnnotationsByType(T::class.java)
}

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
    defaultValue: String?,
    annotationHandler: AnnotationHandler
): FieldSpec {
    val fieldName = FIELD_PREFIX + StringUtils.capitalize(paramName)
    val type = TypeName.get(asType())
    val ensureNonNull = !type.isPrimitive && defaultValue != null

    return FieldSpec.builder(type, fieldName, Modifier.PRIVATE)
        .addAnnotations(annotationHandler.toAnnotationSpec(annotationMirrors, ensureNonNull))
        .build()
}

fun ExecutableElement.createFieldSpec(
    paramName: String,
    defaultValue: String?,
    annotationHandler: AnnotationHandler
): FieldSpec {
    val fieldName = FIELD_PREFIX + StringUtils.capitalize(paramName)
    val type = TypeName.get(returnType)
    val ensureNonNull = !type.isPrimitive && defaultValue != null

    return FieldSpec.builder(type, fieldName, Modifier.PRIVATE)
        .addAnnotations(annotationHandler.toAnnotationSpec(annotationMirrors, ensureNonNull))
        .build()
}

fun VariableElement.createMethodSignature(
    defaultValue: String?,
    annotationHandler: AnnotationHandler
): MethodSpec.Builder {
    val paramName = simpleName.toString()
    val paramType = TypeName.get(asType())
    val methodName = buildGetterName(paramName, paramType)
    val ensureNonNull = !paramType.isPrimitive && defaultValue != null

    return MethodSpec.methodBuilder(methodName)
        .addModifiers(Modifier.PUBLIC)
        .addAnnotations(annotationHandler.toAnnotationSpec(annotationMirrors, ensureNonNull))
        .returns(paramType)
}

fun ExecutableElement.createMethodSignature(
    defaultValue: String?,
    annotationHandler: AnnotationHandler
): MethodSpec.Builder {
    val paramType = TypeName.get(returnType)
    val ensureNonNull = !paramType.isPrimitive && defaultValue != null

    return MethodSpec.methodBuilder(simpleName.toString())
        .addModifiers(Modifier.PUBLIC)
        .addAnnotations(annotationHandler.toAnnotationSpec(annotationMirrors, ensureNonNull))
        .addParameters(parameters.map { it.createParamSpec(annotationHandler) })
        .returns(paramType)
}

fun VariableElement.findTypeAdapter(): TypeAdapter? {
    val adapter = getAnnotation(TypeAdapter::class.java)
    if (adapter != null) {
        return adapter
    }

    return asType().accept(TypeAdapterVisitor(), null)
}

fun ExecutableElement.findTypeAdapter(): TypeAdapter? {
    val adapter = getAnnotation(TypeAdapter::class.java)
    if (adapter != null) {
        return adapter
    }

    return returnType.accept(TypeAdapterVisitor(), null)
}

private class TypeAdapterVisitor : SimpleTypeVisitor8<TypeAdapter?, Void>() {
    override fun visitDeclared(t: DeclaredType, p: Void?): TypeAdapter? {
        return t.asElement().getAnnotation(TypeAdapter::class.java)
    }

    override fun visitArray(t: ArrayType, p: Void?): TypeAdapter? {
        return t.componentType.accept(this, null)
    }
}