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

import androidx.room.compiler.codegen.toJavaPoet
import androidx.room.compiler.processing.ExperimentalProcessingApi
import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XFiler
import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XTypeElement
import androidx.room.compiler.processing.addOriginatingElement
import boringyuri.api.UriFactory
import boringyuri.dagger.util.DaggerTypeName
import boringyuri.dagger.util.ProcessorOptions
import boringyuri.processor.common.base.BoringProcessingStep
import boringyuri.processor.common.base.ProcessingSession
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import com.squareup.kotlinpoet.javapoet.KotlinPoetJavaPoetPreview
import javax.lang.model.element.Modifier


@OptIn(ExperimentalProcessingApi::class, KotlinPoetJavaPoetPreview::class)
class DaggerModuleGeneratorStep(session: ProcessingSession) : BoringProcessingStep(session) {

    private val providesFunctions = mutableListOf<MethodSpec>()
    private val originatingElements = mutableSetOf<XElement>()
    private val deferredFactoryNames = mutableSetOf<String>()

    override fun annotations(): Set<String> {
        return setOf(UriFactory::class.java.name)
    }

    @Deprecated(
        "We're combining processOver() and this process() overload.",
        replaceWith = ReplaceWith("process(XProcessingEnv, Map<String, Set<XElement>>, Boolean)"),
        level = DeprecationLevel.WARNING
    )
    override fun process(
        env: XProcessingEnv,
        elementsByAnnotation: Map<String, Set<XElement>>
    ): Set<XElement> {
        val deferredElements = mutableSetOf<XTypeElement>()

        val expectedFactories = elementsByAnnotation[UriFactory::class.java.name]
            ?.mapNotNull { it as? XTypeElement }
            ?: emptyList()

        for (factory in expectedFactories) {
            val factoryImpl = findFactoryImpl(factory)
            // if a factory implementation is not compiled or generated on this round,
            // we put the factory to a deferred list so we could get back to it
            // in the next processing round.
            if (factoryImpl == null) {
                deferredElements.add(factory)
                deferredFactoryNames += factory.qualifiedName
            } else {
                deferredFactoryNames.remove(factory.qualifiedName)
                providesFunctions.add(buildProvidesMethod(factory, factoryImpl))
                originatingElements.add(factory)
            }
        }

        // generate dagger module class only when all factory implementations are generated
        // and compiled by the current processing round.
        if (deferredElements.isEmpty()) {
            generateBoringDaggerModule(
                moduleName = ProcessorOptions.getModuleName(session),
                providesFunctions = providesFunctions.sortedBy { it.name },
                originatingElements = originatingElements,
            )
        }

        return deferredElements.toSet()
    }

    override fun onProcessingOver() {
        if (deferredFactoryNames.isNotEmpty()) {
            logger.warn(
                e = null,
                "Some of the Uri factory implementations were not generated: $deferredFactoryNames"
            )
        }
        deferredFactoryNames.clear()
        providesFunctions.clear()
        originatingElements.clear()
    }

    private fun findFactoryImpl(factory: XTypeElement): XTypeElement? {
        val factoryName = factory.asClassName().toJavaPoet()
        val factoryImplName = ClassName.get(
            factoryName.packageName(),
            "${factoryName.simpleName()}$CONTAINER_IMPL_SUFFIX"
        )

        return session.processingEnv.findTypeElement(factoryImplName)
    }

    private fun generateBoringDaggerModule(
        moduleName: ClassName,
        providesFunctions: List<MethodSpec>,
        originatingElements: Collection<XElement>,
    ) {
        val moduleContent = TypeSpec.classBuilder(moduleName)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addAnnotation(DaggerTypeName.MODULE)
            .addMethod(
                MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build()
            )
            .addMethods(providesFunctions)

        originatingElements.forEach {
            moduleContent.addOriginatingElement(it)
        }

        session.fileWriter.writeSourceFile(
            moduleName,
            moduleContent.build(),
            XFiler.Mode.Aggregating
        )
    }

    private fun buildProvidesMethod(factory: XTypeElement, factoryImpl: XTypeElement): MethodSpec {
        val factoryImplName = factoryImpl.asClassName().toJavaPoet()
        val factoryName = factory.asClassName().toJavaPoet()

        return MethodSpec.methodBuilder("provide${factoryName.simpleName()}")
            .addModifiers(Modifier.STATIC)
            .addAnnotation(DaggerTypeName.PROVIDES)
            .addAnnotation(DaggerTypeName.NON_NULL)
            .returns(factoryName)
            .addStatement("return new \$T()", factoryImplName)
            .build()
    }

    companion object{
        const val CONTAINER_IMPL_SUFFIX = "Impl"
    }
}