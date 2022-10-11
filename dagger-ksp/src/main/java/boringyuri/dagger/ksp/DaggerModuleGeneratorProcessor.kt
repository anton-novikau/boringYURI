package boringyuri.dagger.ksp

import boringyuri.dagger.common.DaggerModuleGeneratorStep
import boringyuri.processor.common.base.BoringProcessingStep
import boringyuri.processor.common.base.ProcessingSession
import boringyuri.processor.ksp.KspBoringAnnotationProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment

class DaggerModuleGeneratorProcessor(
    environment: SymbolProcessorEnvironment
) : KspBoringAnnotationProcessor(environment) {
    override fun initSteps(session: ProcessingSession): Iterable<BoringProcessingStep> {
        return setOf(
            DaggerModuleGeneratorStep(session)
        )
    }
}