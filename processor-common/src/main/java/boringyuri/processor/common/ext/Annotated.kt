/*
 * Copyright 2022 Anton Novikau
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

import androidx.room.compiler.processing.XAnnotated
import androidx.room.compiler.processing.XType

inline fun <reified T : Annotation> XAnnotated.getAnnotation(): T? {
    return getAnnotation(T::class)?.value
}

inline fun <reified T : Annotation> XAnnotated.getAnnotations(): Iterable<T> {
    return getAnnotations(T::class).map { it.value }
}

inline fun <reified T : Annotation> XAnnotated.requireAnnotation(): T {
    return requireAnnotation(T::class).value
}

inline fun <reified T : Annotation> XAnnotated.getAnnotationValueAsType(): XType? {
    return getAnnotation(T::class)?.getAsType("value")
}

inline fun <reified T : Annotation> XAnnotated.requireAnnotationValueAsType(): XType {
    return checkNotNull(getAnnotationValueAsType<T>()) {
        "Value is required for ${T::class.java.name}"
    }
}
