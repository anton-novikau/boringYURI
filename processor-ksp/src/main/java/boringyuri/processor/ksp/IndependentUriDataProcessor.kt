package boringyuri.processor.ksp

import boringyuri.api.DefaultValue
import boringyuri.api.Param
import boringyuri.api.Path
import boringyuri.api.UriData
import boringyuri.api.adapter.TypeAdapter
import boringyuri.processor.common.IndependentUriDataGeneratorStep
import boringyuri.processor.common.base.BoringProcessingStep
import boringyuri.processor.common.base.ProcessingSession
import boringyuri.processor.common.type.CommonTypeName.OVERRIDE
import boringyuri.processor.common.util.AnnotationHandler
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName

class IndependentUriDataProcessor(environment: SymbolProcessorEnvironment) :
    KspBoringAnnotationProcessor(environment) {
    override fun initSteps(session: ProcessingSession): Iterable<BoringProcessingStep> {
        val annotationHandler = AnnotationHandler(INTERNAL_ANNOTATIONS)

        return setOf(
            IndependentUriDataGeneratorStep(session, annotationHandler)
        )
    }

    companion object {
        private val INTERNAL_ANNOTATIONS: Set<TypeName> = hashSetOf(
            OVERRIDE,
            ClassName.get(UriData::class.java),
            ClassName.get(Path::class.java),
            ClassName.get(Param::class.java),
            ClassName.get(DefaultValue::class.java),
            ClassName.get(TypeAdapter::class.java)
        )
    }
}
