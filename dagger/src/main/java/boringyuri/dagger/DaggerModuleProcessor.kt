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

import boringyuri.dagger.util.ProcessorOptions
import boringyuri.processor.base.BoringAnnotationProcessor
import boringyuri.processor.base.BoringProcessingStep
import boringyuri.processor.base.ProcessingSession
import com.google.auto.service.AutoService
import com.google.common.collect.ImmutableSet
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType
import javax.annotation.processing.Processor
import javax.annotation.processing.SupportedOptions
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion

@Suppress("unused") // class is used by @AutoService
@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.AGGREGATING)
@SupportedOptions(ProcessorOptions.OPT_DAGGER_BORING_MODULE)
class DaggerModuleProcessor : BoringAnnotationProcessor() {

    override fun initSteps(session: ProcessingSession): Iterable<BoringProcessingStep> {
        return ImmutableSet.of(DaggerModuleGeneratorStep(session))
    }

}