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

package boringyuri.processor.common.ext

import androidx.room.compiler.processing.XAnnotationBox
import androidx.room.compiler.processing.XArrayType
import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XExecutableElement
import androidx.room.compiler.processing.XHasModifiers
import androidx.room.compiler.processing.XType
import androidx.room.compiler.processing.XTypeElement
import androidx.room.compiler.processing.XVariableElement
import boringyuri.api.adapter.TypeAdapter
import boringyuri.processor.common.util.AnnotationHandler
import boringyuri.processor.common.util.buildGetterName
import boringyuri.processor.common.xvisitors.XTypeVisitor
import boringyuri.processor.common.xvisitors.accept
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import org.apache.commons.lang3.StringUtils
import javax.lang.model.element.Modifier

private const val FIELD_PREFIX = "m"

fun XVariableElement.createParamSpec(annotationHandler: AnnotationHandler): ParameterSpec {
    val paramType = type.typeName
    val paramName = name

    return ParameterSpec.builder(paramType, paramName)
        .addModifiers(createModifiers())
        .addAnnotations(annotationHandler.toAnnotationSpec(this.type, getAllAnnotations()))
        .build()
}

fun XElement.createModifiers(): Iterable<Modifier> {
    val result = mutableSetOf<Modifier>()
    (this as? XHasModifiers)?.run {
        if (isPublic()) {
            result += Modifier.PUBLIC
        }

        if (isProtected()) {
            result += Modifier.PROTECTED
        }

        if (isPrivate()) {
            result += Modifier.PRIVATE
        }

        if (isAbstract()) {
            result += Modifier.ABSTRACT
        }
        if (isStatic()) {
            result += Modifier.STATIC
        }
        if (isFinal()) {
            result += Modifier.FINAL
        }

        if (isTransient()) {
            result += Modifier.TRANSIENT
        }
    }
    return result
}

fun XExecutableElement.extractPackage(): String? {
    return (this.enclosingElement as? XTypeElement)?.packageName
}

fun XVariableElement.createFieldSpec(
    paramName: String,
    defaultValue: String?,
    annotationHandler: AnnotationHandler
): FieldSpec {
    val fieldName = FIELD_PREFIX + StringUtils.capitalize(paramName)
    val type = type.typeName
    val ensureNonNull = !type.isPrimitive && defaultValue != null

    return FieldSpec.builder(type, fieldName, Modifier.PRIVATE)
        .addAnnotations(
            annotationHandler.toAnnotationSpec(
                this.type,
                getAllAnnotations(),
                ensureNonNull
            )
        )
        .build()
}

fun XVariableElement.createMethodSignature(
    defaultValue: String?,
    annotationHandler: AnnotationHandler
): MethodSpec.Builder {
    val paramName = name
    val paramType = type.typeName
    val methodName = buildGetterName(paramName, paramType)
    val ensureNonNull = !paramType.isPrimitive && defaultValue != null

    return MethodSpec.methodBuilder(methodName)
        .addModifiers(Modifier.PUBLIC)
        .addAnnotations(
            annotationHandler.toAnnotationSpec(
                type,
                getAllAnnotations(),
                ensureNonNull
            )
        )
        .returns(paramType)
}

fun XVariableElement.findTypeAdapter(): XAnnotationBox<TypeAdapter>? {
    val adapter = getAnnotation(TypeAdapter::class)
    if (adapter != null) {
        return adapter
    }
    return type.accept(TypeAdapterVisitor(), null)
}

private class TypeAdapterVisitor : XTypeVisitor<XAnnotationBox<TypeAdapter>?, Unit?> {
    override fun visit(type: XType, param: Unit?): XAnnotationBox<TypeAdapter>? {
        return type.typeElement?.getAnnotation(TypeAdapter::class)
    }

    override fun visitArray(type: XArrayType, param: Unit?): XAnnotationBox<TypeAdapter>? {
        return type.componentType.accept(this, param)
    }
}
