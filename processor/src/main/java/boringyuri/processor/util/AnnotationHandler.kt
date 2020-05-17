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
package boringyuri.processor.util

import boringyuri.processor.util.CommonTypeName.*
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.Element
import javax.lang.model.type.TypeMirror

class AnnotationHandler @JvmOverloads constructor(
    private val internalAnnotations: Set<TypeName> = emptySet()
) {

    fun toAnnotationSpec(
        annotations: List<AnnotationMirror>,
        ensureNonNull: Boolean = false
    ): List<AnnotationSpec> {
        val annotationSpecs = ArrayList<AnnotationSpec>(annotations.size)
        var isNonNullPresent = false
        for (annotation in annotations) {
            val annotationTypeName = ClassName.get(annotation.annotationType)
            if (internalAnnotations.contains(annotationTypeName)) {
                // exclude the internal library annotations
                // defined in api module from method and
                // parameter annotations
                continue
            }

            // replace JetBrains @Nullable and @NotNull with the appropriate androidx annotations
            when(annotationTypeName) {
                JB_NON_NULL, NON_NULL -> {
                    isNonNullPresent = true
                    annotationSpecs.add(AnnotationSpec.builder(NON_NULL).build())
                }
                JB_NULLABLE, NULLABLE -> {
                    if (ensureNonNull) {
                        // we'll replace @Nullable with @NonNull so it's present then
                        isNonNullPresent = true
                    }
                    // replace @Nullable with @NonNull if requested to ensure that it present
                    annotationSpecs.add(
                        AnnotationSpec.builder(if (ensureNonNull) NON_NULL else NULLABLE).build()
                    )
                }
                else -> {
                    annotationSpecs.add(AnnotationSpec.get(annotation))
                }
            }
        }

        if (ensureNonNull && !isNonNullPresent) {
            // add @NonNull if the list of annotations doesn't contain it
            annotationSpecs.add(AnnotationSpec.builder(NON_NULL).build())
        }

        return annotationSpecs
    }

    fun isNullable(type: TypeMirror, element: Element): Boolean {
        return isNullable(TypeName.get(type), element)
    }

    fun isNullable(type: TypeName, element: Element): Boolean {
        if (type.isPrimitive) {
            return false
        }

        element.annotationMirrors.forEach { annotation ->
            val annotationType = ClassName.get(annotation.annotationType)
            if (NON_NULL == annotationType || JB_NON_NULL == annotationType) {
                return false
            } else if (NULLABLE == annotationType || JB_NULLABLE == annotationType) {
                return true
            }
        }

        return true
    }

}