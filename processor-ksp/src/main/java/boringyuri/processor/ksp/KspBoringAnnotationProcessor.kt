package boringyuri.processor.ksp

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

    abstract fun initSteps(session: ProcessingSession): Iterable<BoringProcessingStep>

    private val delegate: BoringAnnotationProcessorDelegate by lazy {
        object : BoringAnnotationProcessorDelegate(xProcessingEnv) {
            override fun initSteps(session: ProcessingSession): Iterable<BoringProcessingStep> {
                return this@KspBoringAnnotationProcessor.initSteps(session)
            }
        }
    }

    override fun processingSteps(): Iterable<XProcessingStep> {
        return delegate.processingSteps()
    }

    override fun postRound(env: XProcessingEnv, round: XRoundEnv) {
        super.postRound(env, round)

        delegate.postRound(env, round)
    }
}
