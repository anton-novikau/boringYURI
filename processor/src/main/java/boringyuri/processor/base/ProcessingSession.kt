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

package boringyuri.processor.base

import boringyuri.processor.util.Logger
import javax.annotation.processing.Filer
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

/**
 * Represents a session object that can be shared between the [BoringProcessingStep]
 * implementations of a single [BoringAnnotationProcessor].
 */
open class ProcessingSession(private val processingEnv: ProcessingEnvironment) {

    val logger = Logger(processingEnv.messager)

    val typeUtils: Types by lazy { processingEnv.typeUtils }

    val elementUtils: Elements by lazy { processingEnv.elementUtils }

    val filer: Filer by lazy { processingEnv.filer }

    fun getOption(key: String): String? = processingEnv.options[key]

    fun getOptionOrDefault(
        key: String,
        defaultValue: String
    ): String = getOption(key) ?: defaultValue

    fun getBooleanOption(key: String): Boolean? = processingEnv.options[key]?.toBoolean()

    fun getBooleanOptionOrDefault(
        key: String,
        defaultValue: Boolean
    ): Boolean = getBooleanOption(key) ?: defaultValue

    fun getIntOption(key: String): Int? = processingEnv.options[key]?.toIntOrNull()

    fun getIntOptionOrDefault(
        key: String,
        defaultValue: Int
    ): Int = getIntOption(key) ?: defaultValue

    fun getFloatOption(key: String): Float? = processingEnv.options[key]?.toFloatOrNull()

    fun getFloatOptionOrDefault(
        key: String,
        defaultValue: Float
    ): Float = getFloatOption(key) ?: defaultValue
}