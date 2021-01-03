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

import com.google.auto.common.BasicAnnotationProcessor
import javax.annotation.processing.RoundEnvironment

abstract class BoringAnnotationProcessor : BasicAnnotationProcessor() {

    private var steps: Iterable<BoringProcessingStep>? = null

    abstract fun initSteps(session: ProcessingSession): Iterable<BoringProcessingStep>

    override fun steps(): Iterable<Step> {
        return initSteps(ProcessingSession(processingEnv)).also { steps = it }
    }

    override fun postRound(roundEnv: RoundEnvironment) {
        super.postRound(roundEnv)

        if (roundEnv.processingOver()) {
            steps?.forEach { it.onProcessingOver() }
        }
    }

}