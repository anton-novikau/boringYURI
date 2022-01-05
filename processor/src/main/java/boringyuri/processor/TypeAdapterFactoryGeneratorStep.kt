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
import boringyuri.processor.base.BoringProcessingStep
import boringyuri.processor.base.ProcessingSession
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


class TypeAdapterFactoryGeneratorStep(
    session: ProcessingSession
) : BoringProcessingStep(session) {

    private val typeAdapters = LinkedHashMap<ClassName, Element>()

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

        if (typeAdapters.isNotEmpty()) {
            adaptableElements.forEach {
                logger.error(
                    it,
                    "%s is already created. Can't add %s to it.",
                    typeAdapterFactory.simpleName(),
                    it.simpleName
                )
            }

            return emptySet() // early exit
        }

        adaptableElements.mapNotNull { element ->
            val typeAdapter = element.getAnnotation(TypeAdapter::class.java)?.valueMirror()

            // Map TypeAdapter class name to originating element to be able to
            // log an error or postpone element processing to the next step
            // in case it's possible.
            typeAdapter?.let { ClassName.get(MoreTypes.asTypeElement(it)) to element }
        }.associateTo(typeAdapters) { it }

        return generateTypeAdapterFactory(typeAdapterFactory, typeAdapters)
    }

    private fun generateTypeAdapterFactory(
        factoryClassName: ClassName,
        typeAdapters: Map<ClassName, Element>
    ): Set<Element> {

        val adapterFactory = TypeSpec.classBuilder(factoryClassName).addModifiers(Modifier.PUBLIC)

        val cacheKeyType = ParameterizedTypeName.get(
            CLASS,
            WildcardTypeName.subtypeOf(ANY_TYPE_ADAPTER)
        )
        val cacheValueType = ANY_TYPE_ADAPTER
        val cacheType = ParameterizedTypeName.get(MAP, cacheKeyType, cacheValueType)
        val cacheField = FieldSpec.builder(
            cacheType,
            "ADAPTER_CACHE",
            Modifier.PRIVATE,
            Modifier.STATIC,
            Modifier.FINAL
        ).initializer(
            "new \$T<>()",
            HASH_MAP
        ).addAnnotation(NON_NULL).build()

        adapterFactory.addField(cacheField)
        adapterFactory.addMethod(
            MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build()
        )

        typeAdapters.forEach { (adapterName, _) ->
            adapterFactory.addMethod(buildCreateAdapterMethod(adapterName, cacheField))
        }

        writeSourceFile(factoryClassName, adapterFactory.build(), null)

        return emptySet() // set of deferred elements
    }

    private fun buildCreateAdapterMethod(
        adapterName: ClassName,
        cacheFiled: FieldSpec
    ): MethodSpec {
        val method = MethodSpec.methodBuilder("create${adapterName.simpleName()}")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addAnnotation(NON_NULL)
            .returns(adapterName)

        val adapterVarName = "adapter"
        method.addStatement("\$T \$L = \$N.get(\$T.class)",
            ANY_TYPE_ADAPTER,
            adapterVarName,
            cacheFiled,
            adapterName
        )

        method.beginControlFlow("if (\$L == null)", adapterVarName)
        method.addStatement("\$L = new \$T()", adapterVarName, adapterName)
        method.addStatement("\$N.put(\$T.class, \$L)", cacheFiled, adapterName, adapterVarName)
        method.endControlFlow()
        method.addCode("\n")
        method.addStatement("return (\$T) \$L", adapterName, adapterVarName)

        return method.build()
    }
}