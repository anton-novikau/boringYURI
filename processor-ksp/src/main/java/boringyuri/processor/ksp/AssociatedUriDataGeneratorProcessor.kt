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

package boringyuri.processor.ksp

import boringyuri.processor.common.base.BoringProcessingStep
import boringyuri.processor.common.base.ProcessingSession
import boringyuri.processor.common.ksp.KspBoringAnnotationProcessor
import boringyuri.processor.common.steps.AssociatedUriDataGeneratorStep
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment

class AssociatedUriDataGeneratorProcessor(
    environment: SymbolProcessorEnvironment
) : KspBoringAnnotationProcessor(environment) {
    override fun initSteps(session: ProcessingSession): Iterable<BoringProcessingStep> {
        return listOf(
            AssociatedUriDataGeneratorStep.create(session),
            // For some reason if we pair AssociatedUriDataGeneratorStep and UriFactoryGeneratorStep
            // or UriMatcherGeneratorStep in the same processor it will try to create some file twice
            // and crashes with error (FileAlreadyExistsException). So moved other steps to their
            // own processors unlike apt version
        )
    }


}
