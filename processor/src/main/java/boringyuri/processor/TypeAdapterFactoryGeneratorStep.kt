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

import boringyuri.api.adapter.TypeAdapter
import boringyuri.processor.base.AbortProcessingException
import boringyuri.processor.base.BoringProcessingStep
import boringyuri.processor.base.ProcessingSession
import boringyuri.processor.ext.requireAnnotation
import boringyuri.processor.ext.valueMirror
import boringyuri.processor.type.CommonTypeName.ANY_TYPE_ADAPTER
import boringyuri.processor.type.CommonTypeName.CLASS
import boringyuri.processor.type.CommonTypeName.HASH_MAP
import boringyuri.processor.type.CommonTypeName.MAP
import boringyuri.processor.type.CommonTypeName.NON_NULL
import boringyuri.processor.util.ProcessorOptions.getTypeAdapterFactory
import com.google.auto.common.MoreTypes
import com.google.common.collect.ImmutableSet
import com.google.common.collect.ImmutableSetMultimap
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeSpec
import com.squareup.javapoet.WildcardTypeName
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement


class TypeAdapterFactoryGeneratorStep(
    session: ProcessingSession
) : BoringProcessingStep(session) {

    private val factoryMethods = mutableMapOf<TypeElement, MethodSpec>()
    private val deferredElements = mutableSetOf<Element>()

    private val cacheConstant by lazy { buildCacheConstant() }

    override fun annotations(): Set<String> {
        return ImmutableSet.of(TypeAdapter::class.java.name)
    }

    override fun process(
        elementsByAnnotation: ImmutableSetMultimap<String, Element>
    ): Set<Element> {
        // Stop processing Type Adapters if the factory class is not specified.
        // Every adapter instance will be created at use without caching.
        val typeAdapterFactory = getTypeAdapterFactory(session) ?: return emptySet()

        val adaptableElements = elementsByAnnotation[TypeAdapter::class.java.name]

        for (element in adaptableElements) {
            val typeAdapter = element.requireAnnotation<TypeAdapter>().valueMirror() ?: continue
            try {
                val adapterElement = MoreTypes.asTypeElement(typeAdapter)
                if (adapterElement != null) {
                    deferredElements.remove(element)
                    if (adapterElement !in factoryMethods) {
                        factoryMethods[adapterElement] = buildAdapterFactoryMethod(
                            ClassName.get(adapterElement),
                            cacheConstant
                        )
                    }
                } else {
                    deferredElements.add(element)
                }
            } catch (e: IllegalArgumentException) {
                deferredElements.add(element)
            } catch (e: NullPointerException) {
                deferredElements.add(element)
            }
        }

        if (deferredElements.isEmpty()) {
            generateTypeAdapterFactory(
                typeAdapterFactory,
                cacheConstant,
                factoryMethods.values
            )
        }

        return ImmutableSet.copyOf(deferredElements)
    }

    override fun onProcessingOver() {
        factoryMethods.clear()

        if (deferredElements.isNotEmpty()) {
            val missingAdapters = deferredElements.map { it.simpleName.toString() }
            deferredElements.clear()

            throw AbortProcessingException(
                logger = logger,
                message = "Type adapters are missing for $missingAdapters"
            )
        }
    }

    private fun generateTypeAdapterFactory(
        factoryClassName: ClassName,
        cacheConstant: FieldSpec,
        adapterFactoryMethods: Collection<MethodSpec>
    ) {
        val adapterFactory = TypeSpec.classBuilder(factoryClassName)
            .addModifiers(Modifier.PUBLIC)
            .addField(cacheConstant)
            .addMethod(
                MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build()
            )
            .addMethods(adapterFactoryMethods)
            .build()

        writeSourceFile(factoryClassName, adapterFactory, originatingElement = null)
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
        method.addStatement("\$T \$L = \$N.get(\$T.class)",
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