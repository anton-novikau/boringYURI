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

package boringyuri.processor.common.steps

import androidx.room.compiler.processing.ExperimentalProcessingApi
import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XFiler
import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.addOriginatingElement
import boringyuri.api.adapter.TypeAdapter
import boringyuri.processor.common.base.BoringProcessingStep
import boringyuri.processor.common.base.ProcessingSession
import boringyuri.processor.common.steps.ext.requireTypeAdapter
import boringyuri.processor.common.steps.type.CommonTypeName.ANY_TYPE_ADAPTER
import boringyuri.processor.common.steps.type.CommonTypeName.CLASS
import boringyuri.processor.common.steps.type.CommonTypeName.HASH_MAP
import boringyuri.processor.common.steps.type.CommonTypeName.MAP
import boringyuri.processor.common.steps.type.CommonTypeName.NON_NULL
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeSpec
import com.squareup.javapoet.WildcardTypeName
import javax.lang.model.element.Modifier

@OptIn(ExperimentalProcessingApi::class)
class TypeAdapterFactoryGeneratorStep(session: ProcessingSession) : BoringProcessingStep(session) {

    private val sourceClasses = mutableSetOf<ClassName>()
    private val factoryMethods = mutableMapOf<ClassName, MethodSpec>()

    private val cacheConstant by lazy { buildCacheConstant() }

    private val originatingElements: MutableSet<XElement> = mutableSetOf()

    override fun annotations(): Set<String> {
        return setOf(TypeAdapter::class.java.name)
    }

    @Deprecated(
        "We're combining processOver() and this process() overload.",
        replaceWith = ReplaceWith("process(XProcessingEnv, Map<String, Set<XElement>>, Boolean)"),
        level = DeprecationLevel.WARNING
    )
    @Suppress("ReturnCount")
    override fun process(
        env: XProcessingEnv,
        elementsByAnnotation: Map<String, Set<XElement>>
    ): Set<XElement> {

        // Stop processing Type Adapters if the factory class is not specified.
        // Every adapter instance will be created at use without caching.
        val typeAdapterFactory = ProcessorOptions.getTypeAdapterFactory(session)
            ?: return emptySet()

        val adaptableElements = elementsByAnnotation[TypeAdapter::class.java.name]
            ?: return emptySet()

        if (adaptableElements.isEmpty() || adaptableElements.any { !it.validate() }) {
            return adaptableElements
        }

        val roundClasses = adaptableElements.map {
            it.requireTypeAdapter()
        }

        val roundClassNames = roundClasses.map {
            ClassName.bestGuess(it.typeName.toString())
        }
        if (sourceClasses.addAll(roundClassNames)) {
            roundClassNames.forEach {
                factoryMethods[it] = buildAdapterFactoryMethod(it, cacheConstant)
            }

            originatingElements.addAll(adaptableElements)
            originatingElements.addAll(roundClasses.mapNotNull { it.typeElement })

            val adapterFactoryMethods = factoryMethods.values.sortedBy { it.name }
            generateTypeAdapterFactory(
                typeAdapterFactory,
                cacheConstant,
                adapterFactoryMethods,
                originatingElements
            )
            return emptySet()
        }

        return adaptableElements
    }

    override fun onProcessingOver() {
        sourceClasses.clear()
        factoryMethods.clear()
        originatingElements.clear()
    }

    private fun generateTypeAdapterFactory(
        className: ClassName,
        cacheConstant: FieldSpec,
        adapterFactoryMethods: Collection<MethodSpec>,
        originatingElements: Collection<XElement>
    ) {
        val classContent = TypeSpec.classBuilder(className)
            .addModifiers(Modifier.PUBLIC)
            .addField(cacheConstant)
            .addMethod(
                MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build()
            )
            .addMethods(adapterFactoryMethods)
            .apply {
                originatingElements.forEach { xElement ->
                    addOriginatingElement(xElement)
                }
            }
            .build()

        session.fileWriter.writeSourceFile(className, classContent, XFiler.Mode.Aggregating)
    }

    private fun buildCacheConstant(): FieldSpec {
        val cacheKeyType = ParameterizedTypeName.get(
            CLASS,
            WildcardTypeName.subtypeOf(ANY_TYPE_ADAPTER)
        )
        val cacheValueType = ANY_TYPE_ADAPTER
        val cacheType = ParameterizedTypeName.get(MAP, cacheKeyType, cacheValueType)

        return FieldSpec.builder(
            cacheType,
            "ADAPTER_CACHE",
            Modifier.PRIVATE,
            Modifier.STATIC,
            Modifier.FINAL
        ).initializer(
            "new \$T<>()",
            HASH_MAP
        ).addAnnotation(NON_NULL).build()
    }


    private fun buildAdapterFactoryMethod(
        adapterName: ClassName,
        cacheConstant: FieldSpec
    ): MethodSpec {
        val method = MethodSpec.methodBuilder("create${adapterName.simpleName()}")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addAnnotation(NON_NULL)
            .returns(adapterName)

        val adapterVarName = "adapter"
        method.addStatement(
            "\$T \$L = \$N.get(\$T.class)",
            ANY_TYPE_ADAPTER,
            adapterVarName,
            cacheConstant,
            adapterName
        )

        method.beginControlFlow("if (\$L == null)", adapterVarName)
        method.addStatement("\$L = new \$T()", adapterVarName, adapterName)
        method.addStatement("\$N.put(\$T.class, \$L)", cacheConstant, adapterName, adapterVarName)
        method.endControlFlow()
        method.addCode("\n")
        method.addStatement("return (\$T) \$L", adapterName, adapterVarName)

        return method.build()
    }
}
