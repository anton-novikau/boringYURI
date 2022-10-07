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
package boringyuri.processor

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
import boringyuri.processor.common.ProcessorOptions
import boringyuri.processor.common.base.BoringProcessingStep
import boringyuri.processor.common.base.ProcessingSession
import boringyuri.processor.common.util.AnnotationHandler
import com.google.auto.service.AutoService
import com.google.common.collect.ImmutableSet
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType
import javax.annotation.processing.Processor
import javax.annotation.processing.SupportedOptions
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion

@Suppress("unused") // class is used by @AutoService
@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.ISOLATING)
@SupportedOptions(
    ProcessorOptions.OPT_TYPE_ADAPTER_FACTORY
)
//TODO: Rename this after all steps migrated
class TEMPUriFactoryProcessor : AptBoringAnnotationProcessor() {

    override fun initSteps(session: ProcessingSession): Iterable<BoringProcessingStep> {
        val annotationHandler = AnnotationHandler(INTERNAL_ANNOTATIONS)

        return ImmutableSet.of(
            AssociatedUriDataGeneratorStep(session, annotationHandler),
//            UriFactoryGeneratorStep(session, annotationHandler),
//            UriMatcherGeneratorStep(session)
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