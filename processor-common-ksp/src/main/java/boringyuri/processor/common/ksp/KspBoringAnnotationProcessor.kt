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

package boringyuri.processor.common.ksp

import androidx.room.compiler.processing.ExperimentalProcessingApi
import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XProcessingEnvConfig
import androidx.room.compiler.processing.XProcessingStep
import androidx.room.compiler.processing.XRoundEnv
import androidx.room.compiler.processing.ksp.KspBasicAnnotationProcessor
import boringyuri.processor.common.base.BoringAnnotationProcessorDelegate
import boringyuri.processor.common.base.BoringProcessingStep
import boringyuri.processor.common.base.ProcessingSession
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment

@OptIn(ExperimentalProcessingApi::class)
abstract class KspBoringAnnotationProcessor(
    environment: SymbolProcessorEnvironment
) : KspBasicAnnotationProcessor(
    environment,
    XProcessingEnvConfig
        .Builder()
        .disableAnnotatedElementValidation(true)
        .build()
) {

    abstract fun initStep(session: ProcessingSession): BoringProcessingStep

    private val delegate: BoringAnnotationProcessorDelegate by lazy {
        object : BoringAnnotationProcessorDelegate(xProcessingEnv) {
            override fun initSteps(session: ProcessingSession): Iterable<BoringProcessingStep> {
                return listOf(this@KspBoringAnnotationProcessor.initStep(session))
            }
        }
    }

    final override fun processingSteps(): Iterable<XProcessingStep> {
        return delegate.processingSteps()
    }

    override fun postRound(env: XProcessingEnv, round: XRoundEnv) {
        super.postRound(env, round)

        delegate.postRound(env, round)
    }
}
