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
import boringyuri.api.UriData
import boringyuri.api.adapter.TypeAdapter
import boringyuri.processor.base.BoringAnnotationProcessor
import boringyuri.processor.base.BoringProcessingStep
import boringyuri.processor.base.ProcessingSession
import boringyuri.processor.type.CommonTypeName
import boringyuri.processor.util.AnnotationHandler
import boringyuri.processor.util.ProcessorOptions
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
class IndependentUriDataProcessor : BoringAnnotationProcessor() {

    override fun initSteps(session: ProcessingSession): Iterable<BoringProcessingStep> {
        val annotationHandler = AnnotationHandler(INTERNAL_ANNOTATIONS)

        return ImmutableSet.of<BoringProcessingStep>(
            IndependentUriDataGeneratorStep(session, annotationHandler)
        )
    }

    companion object {
        private val INTERNAL_ANNOTATIONS: Set<TypeName> = hashSetOf(
            CommonTypeName.OVERRIDE,
            ClassName.get(UriData::class.java),
            ClassName.get(Path::class.java),
            ClassName.get(Param::class.java),
            ClassName.get(DefaultValue::class.java),
            ClassName.get(TypeAdapter::class.java)
        )
    }
}