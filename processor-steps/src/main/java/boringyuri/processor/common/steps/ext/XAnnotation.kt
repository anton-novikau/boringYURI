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