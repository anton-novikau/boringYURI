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

import boringyuri.api.UriFactory
import boringyuri.dagger.util.DaggerTypeName
import boringyuri.dagger.util.ProcessorOptions
import boringyuri.processor.base.BoringProcessingStep
import boringyuri.processor.base.ProcessingSession
import boringyuri.processor.type.CommonTypeName
import com.google.common.collect.ImmutableSet
import com.google.common.collect.ImmutableSetMultimap
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.util.ElementFilter


class DaggerModuleGeneratorStep(session: ProcessingSession) : BoringProcessingStep(session) {

    private val providesFunctions = mutableListOf<MethodSpec>()
    private val deferredElements = mutableSetOf<Element>()

    override fun annotations(): Set<String> {
        return ImmutableSet.of(UriFactory::class.java.name)
    }

    override fun process(
        elementsByAnnotation: ImmutableSetMultimap<String, Element>
    ): Set<Element> {
        val expectedFactories = ElementFilter.typesIn(
            elementsByAnnotation[UriFactory::class.java.name]
        )

        for (factory in expectedFactories) {
            val factoryImpl = findFactoryImpl(factory)
            // if a factory implementation is not compiled or generated on this round,
            // we put the factory to a deferred list so we could get back to it
            // in the next processing round.
            if (factoryImpl == null) {
                deferredElements.add(factory)
            } else {
                deferredElements.remove(factory)
                providesFunctions.add(buildProvidesMethod(factory, factoryImpl))
            }
        }

        // generate dagger module class only when all factory implementations are generated
        // and compiled by the current processing round.
        if (deferredElements.isEmpty()) {
            generateBoringDaggerModule(
                moduleName = ProcessorOptions.getModuleName(session),
                providesFunctions = providesFunctions
            )
        }

        return ImmutableSet.copyOf(deferredElements)
    }

    override fun onProcessingOver() {
        if (deferredElements.isNotEmpty()) {
            val missingFactories = deferredElements.map { it.simpleName.toString() }
            logger.warn(
                e = null,
                "Some of the Uri factory implementations were not generated: $missingFactories"
            )
        }
        deferredElements.clear()
        providesFunctions.clear()
    }

    private fun findFactoryImpl(factory: TypeElement): TypeElement? {
        val factoryName = ClassName.get(factory)
        val factoryImplName = ClassName.get(
            factoryName.packageName(),
            "${factoryName.simpleName()}$CONTAINER_IMPL_SUFFIX"
        )

        return elementUtils.getTypeElement(factoryImplName.canonicalName())
    }

    private fun generateBoringDaggerModule(
        moduleName: ClassName,
        providesFunctions: List<MethodSpec>
    ) {
        val moduleContent = TypeSpec.classBuilder(moduleName)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addAnnotation(DaggerTypeName.MODULE)
            .addMethod(
                MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build()
            )
            .addMethods(providesFunctions)

        writeSourceFile(moduleName, moduleContent.build(), originatingElement = null)
    }

    private fun buildProvidesMethod(factory: TypeElement, factoryImpl: TypeElement): MethodSpec {
        val factoryImplName = ClassName.get(factoryImpl)
        val factoryName = ClassName.get(factory)

        return MethodSpec.methodBuilder("provide${factoryName.simpleName()}")
            .addModifiers(Modifier.STATIC)
            .addAnnotation(DaggerTypeName.PROVIDES)
            .addAnnotation(CommonTypeName.NON_NULL)
            .returns(factoryName)
            .addStatement("return new \$T()", factoryImplName)
            .build()
    }

    companion object{
        const val CONTAINER_IMPL_SUFFIX = "Impl"
    }
}