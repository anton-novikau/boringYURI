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
package boringyuri.processor.common.util

import androidx.room.compiler.processing.XAnnotation
import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XMethodElement
import androidx.room.compiler.processing.XNullability
import androidx.room.compiler.processing.XType
import androidx.room.compiler.processing.XVariableElement
import boringyuri.processor.common.type.CommonTypeName.JB_NON_NULL
import boringyuri.processor.common.type.CommonTypeName.JB_NULLABLE
import boringyuri.processor.common.type.CommonTypeName.NON_NULL
import boringyuri.processor.common.type.CommonTypeName.NULLABLE
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.TypeName

class AnnotationHandler @JvmOverloads constructor(
    private val internalAnnotations: Set<TypeName> = emptySet()
) {

    fun toAnnotationSpec(
        nullabilityType: XType,
        annotations: List<XAnnotation>,
        ensureNonNull: Boolean = false
    ): List<AnnotationSpec> {
        val annotationSpecs = ArrayList<AnnotationSpec>(annotations.size)
        var nullabilityAnnotation = if (ensureNonNull) NON_NULL else null

        if (nullabilityAnnotation == null) {
            when (nullabilityType.nullability) {
                XNullability.NONNULL ->
                    nullabilityAnnotation = NON_NULL
                XNullability.NULLABLE ->
                    nullabilityAnnotation = NULLABLE
                else -> {}
            }
        }

        for (annotation in annotations) {
            val annotationTypeName = annotation.typeElement.className
            if (internalAnnotations.contains(annotationTypeName)) {
                // exclude the internal library annotations
                // defined in api module from method and
                // parameter annotations
                continue
            }


            // replace JetBrains @Nullable and @NotNull with the appropriate androidx annotations
            when (annotationTypeName) {
                JB_NON_NULL, NON_NULL -> {
                    if (nullabilityAnnotation == null) {
                        nullabilityAnnotation = NON_NULL
                    }
                }
                JB_NULLABLE, NULLABLE -> {
                    if (nullabilityAnnotation == null) {
                        nullabilityAnnotation = NULLABLE
                    }
                }
                else -> {
                    annotationSpecs.add(
                        AnnotationSpec.builder(annotation.className).build()
                    )
                }
            }
        }

        if (nullabilityAnnotation != null && !nullabilityType.typeName.isPrimitive) {
            annotationSpecs.add(AnnotationSpec.builder(nullabilityAnnotation).build())
        }

        return annotationSpecs
    }

    fun isNullable(type: TypeName, element: XElement): Boolean {
        if (type.isPrimitive) {
            return false
        }

        when (element.extractTypeNullability()) {
            XNullability.NULLABLE ->
                return true
            XNullability.NONNULL ->
                return false
            XNullability.UNKNOWN -> {}
        }

        element.getAllAnnotations().forEach { annotation ->
            val annotationType = annotation.typeElement.className
            if (NON_NULL == annotationType || JB_NON_NULL == annotationType) {
                return false
            } else if (NULLABLE == annotationType || JB_NULLABLE == annotationType) {
                return true
            }
        }

        return true
    }

}

fun XElement.extractTypeNullability(): XNullability {
    return when (this) {
        is XVariableElement -> type.nullability
        is XMethodElement -> returnType.nullability
        else -> XNullability.UNKNOWN
    }
}
