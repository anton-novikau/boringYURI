/*
 * Copyright 2025 Anton Novikau
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

package boringyuri.processor.common.steps.ext

import androidx.room.compiler.processing.XAnnotation

private const val VALUE = "value"
private const val NAME = "name"
private const val ENCODED = "encoded"
private const val AUTHORITY = "authority"
private const val SCHEME = "scheme"
private const val ENABLED = "enabled"

fun XAnnotation.valueAsString() = getAsString(VALUE)

fun XAnnotation.valueAsInt() = getAsInt(VALUE)

fun XAnnotation.valueAsLong() = getAsLong(VALUE)

fun XAnnotation.valueAsDouble() = getAsDouble(VALUE)

fun XAnnotation.valueAsBoolean() = getAsBoolean(VALUE)

fun XAnnotation.name() = getAsString(NAME)

fun XAnnotation.scheme() = getAsString(SCHEME)

fun XAnnotation.authority() = getAsString(AUTHORITY)

fun XAnnotation.encoded() = getAsBoolean(ENCODED)

fun XAnnotation.enabled() = getAsBoolean(ENABLED)