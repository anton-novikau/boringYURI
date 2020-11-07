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
import boringyuri.processor.UriFactoryGeneratorStep.Companion.CONTAINER_IMPL_SUFFIX
import boringyuri.processor.base.BoringProcessingStep
import boringyuri.processor.base.ProcessingSession
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

    override fun annotations(): Set<String> {
        return ImmutableSet.of(UriFactory::class.java.name)
    }

    override fun process(
        elementsByAnnotation: ImmutableSetMultimap<String, Element>
    ): Set<Element> {
        val factories = ElementFilter.typesIn(elementsByAnnotation[UriFactory::class.java.name])
        val moduleName = ProcessorOptions.getModuleName(session)

        return generateBoringDaggerModule(moduleName, factories)
    }

    private fun generateBoringDaggerModule(
        moduleName: ClassName,
        factories: Set<TypeElement>
    ): Set<Element> {
        val moduleContent = TypeSpec.classBuilder(moduleName)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addAnnotation(DaggerTypeName.MODULE)
            .addMethod(
                MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build()
            )

        for (factory in factories) {
            moduleContent.addMethod(buildProvidesMethod(factory))
        }

        writeSourceFile(moduleName, moduleContent.build(), null)
        return emptySet() // set of deferred annotated elements
    }

    private fun buildProvidesMethod(factory: TypeElement): MethodSpec {
        val factoryName = ClassName.get(factory)
        val factoryImplName = ClassName.get(
            factoryName.packageName(),
            "${factoryName.simpleName()}$CONTAINER_IMPL_SUFFIX"
        )

        return MethodSpec.methodBuilder("provide${factoryName.simpleName()}")
            .addModifiers(Modifier.STATIC)
            .addAnnotation(DaggerTypeName.PROVIDES)
            .returns(factoryName)
            .addStatement("return new \$T()", factoryImplName)
            .build()
    }

}