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

package boringyuri.dagger.util

import androidx.room.compiler.processing.ExperimentalProcessingApi
import boringyuri.processor.common.base.ProcessingSession
import com.squareup.javapoet.ClassName


object ProcessorOptions {

    /**
     * Option to customize the dagger module class name for BoringYURI dependencies.
     * If not specified there will be used the default one: boringyuri.dagger.BoringYuriModule.
     *
     * Type: [String]
     */
    const val OPT_DAGGER_BORING_MODULE = "boringyuri.dagger.module"

    private val MODULE_DEFAULT_NAME = ClassName.get("boringyuri.dagger", "BoringYuriModule")

    @OptIn(ExperimentalProcessingApi::class)
    fun getModuleName(session: ProcessingSession): ClassName {
        return try {
            session.processingEnv.options[OPT_DAGGER_BORING_MODULE]?.let {
                ClassName.bestGuess(it)
            } ?: MODULE_DEFAULT_NAME
        } catch (e: IllegalArgumentException) {
            session.logger.warn(null, "Invalid class name in '$OPT_DAGGER_BORING_MODULE' option.")
            MODULE_DEFAULT_NAME
        }
    }

}