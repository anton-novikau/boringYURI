package boringyuri.processor.ksp

import boringyuri.processor.common.UriMatcherGeneratorStep
import boringyuri.processor.common.base.BoringProcessingStep
import boringyuri.processor.common.base.ProcessingSession
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment

class UriMatcherGeneratorProcessor(
    environment: SymbolProcessorEnvironment
) : KspBoringAnnotationProcessor(environment) {
    override fun initSteps(session: ProcessingSession): Iterable<BoringProcessingStep> {
        return listOf(
            UriMatcherGeneratorStep(session)
        )
    }
}
