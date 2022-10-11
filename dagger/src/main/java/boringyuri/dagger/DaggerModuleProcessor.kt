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

package boringyuri.dagger

import boringyuri.dagger.common.DaggerModuleGeneratorStep
import boringyuri.dagger.common.util.ProcessorOptions
import boringyuri.processor.AptBoringAnnotationProcessor
import boringyuri.processor.common.base.BoringProcessingStep
import boringyuri.processor.common.base.ProcessingSession
import com.google.auto.service.AutoService
import javax.annotation.processing.Processor
import javax.annotation.processing.SupportedOptions
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion

@Suppress("unused") // class is used by @AutoService
@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@SupportedOptions(ProcessorOptions.OPT_DAGGER_BORING_MODULE)
class DaggerModuleProcessor : AptBoringAnnotationProcessor() {

    override fun initSteps(session: ProcessingSession): Iterable<BoringProcessingStep> {
        return setOf(DaggerModuleGeneratorStep(session))
    }

}