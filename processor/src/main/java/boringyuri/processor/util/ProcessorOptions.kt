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

import boringyuri.api.adapter.BoringTypeAdapter
import boringyuri.processor.base.ProcessingSession
import com.squareup.javapoet.ClassName

internal object ProcessorOptions {

    /**
     * Option to specify the [BoringTypeAdapter] factory class and to enable instance caching
     * for the created adapters. It must be a fully qualified name of the factory class.
     *
     * Type: [String]
     */
    const val OPT_TYPE_ADAPTER_FACTORY = "boringyuri.type_adapter_factory"

    fun getTypeAdapterFactory(session: ProcessingSession): ClassName? {
        return try {
            session.getOption(OPT_TYPE_ADAPTER_FACTORY)?.let { ClassName.bestGuess(it) }
        } catch (e: IllegalArgumentException) {
            session.logger.warn(null, "Invalid class name in '$OPT_TYPE_ADAPTER_FACTORY' option.")
            null
        }
    }
}
