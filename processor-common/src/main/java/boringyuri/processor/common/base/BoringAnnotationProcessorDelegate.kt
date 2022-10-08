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

package boringyuri.processor.common.base

import androidx.room.compiler.processing.ExperimentalProcessingApi
import androidx.room.compiler.processing.XBasicAnnotationProcessor
import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XProcessingStep
import androidx.room.compiler.processing.XRoundEnv

@OptIn(ExperimentalProcessingApi::class)
abstract class BoringAnnotationProcessorDelegate(
    override val xProcessingEnv: XProcessingEnv
) : XBasicAnnotationProcessor {

    private var steps: Iterable<BoringProcessingStep>? = null

    abstract fun initSteps(session: ProcessingSession): Iterable<BoringProcessingStep>

    override fun processingSteps(): Iterable<XProcessingStep> {
        return initSteps(ProcessingSession(xProcessingEnv)).also { steps = it }
    }

    override fun postRound(env: XProcessingEnv, round: XRoundEnv) {
        if (round.isProcessingOver) {
            steps?.forEach { it.onProcessingOver() }
        }
    }
}