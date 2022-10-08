package boringyuri.processor.ksp

import boringyuri.api.DefaultValue
import boringyuri.api.Param
import boringyuri.api.Path
import boringyuri.api.UriBuilder
import boringyuri.api.UriFactory
import boringyuri.api.WithUriData
import boringyuri.api.adapter.TypeAdapter
import boringyuri.api.constant.BooleanParam
import boringyuri.api.constant.BooleanParams
import boringyuri.api.constant.DoubleParam
import boringyuri.api.constant.DoubleParams
import boringyuri.api.constant.LongParam
import boringyuri.api.constant.LongParams
import boringyuri.api.constant.StringParam
import boringyuri.api.constant.StringParams
import boringyuri.api.matcher.MatcherCode
import boringyuri.api.matcher.MatchesTo
import boringyuri.api.matcher.WithUriMatcher
import boringyuri.processor.common.AssociatedUriDataGeneratorStep
import boringyuri.processor.common.base.BoringProcessingStep
import boringyuri.processor.common.base.ProcessingSession
import boringyuri.processor.common.util.AnnotationHandler
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName

class AssociatedUriDataGeneratorProcessor(
    environment: SymbolProcessorEnvironment
) : KspBoringAnnotationProcessor(environment) {
    override fun initSteps(session: ProcessingSession): Iterable<BoringProcessingStep> {
        val annotationHandler = AnnotationHandler(INTERNAL_ANNOTATIONS)

        return listOf(
            AssociatedUriDataGeneratorStep(session, annotationHandler),
            // For some reason if we pair AssociatedUriDataGeneratorStep and UriFactoryGeneratorStep
            // or UriMatcherGeneratorStep in the same processor it will try to create some file twice
            // and crashes with error (FileAlreadyExistsException). So moved other steps to their
            // own processors unlike apt version
        )
    }

    companion object {
        private val INTERNAL_ANNOTATIONS: Set<TypeName> = hashSetOf(
            ClassName.get(UriFactory::class.java),
            ClassName.get(WithUriMatcher::class.java),
            ClassName.get(UriBuilder::class.java),
            ClassName.get(MatchesTo::class.java),
            ClassName.get(MatcherCode::class.java),
            ClassName.get(WithUriData::class.java),
            ClassName.get(TypeAdapter::class.java),
            ClassName.get(Path::class.java),
            ClassName.get(Param::class.java),
            ClassName.get(DefaultValue::class.java),
            ClassName.get(StringParam::class.java),
            ClassName.get(StringParams::class.java),
            ClassName.get(LongParam::class.java),
            ClassName.get(LongParams::class.java),
            ClassName.get(DoubleParam::class.java),
            ClassName.get(DoubleParams::class.java),
            ClassName.get(BooleanParam::class.java),
            ClassName.get(BooleanParams::class.java)
        )
    }
}
