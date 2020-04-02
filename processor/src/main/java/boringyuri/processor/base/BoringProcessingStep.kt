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

package boringyuri.processor.base

import com.google.auto.common.BasicAnnotationProcessor
import com.google.common.collect.SetMultimap
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import org.apache.commons.lang3.StringUtils
import java.io.IOException
import javax.lang.model.element.Element


abstract class BoringProcessingStep(
    protected val session: ProcessingSession
) : BasicAnnotationProcessor.ProcessingStep {
    abstract override fun annotations(): Set<Class<out Annotation>>
    abstract override fun process(
        elementsByAnnotation: SetMultimap<Class<out Annotation>, Element>
    ): Set<Element>

    open fun onProcessingOver() {
        // NO-OP
    }

    protected fun writeSourceFile(
        className: ClassName,
        classContent: TypeSpec,
        originatingElement: Element?
    ) {
        try {
            val packageName = className.packageName()
            val qualifiedName = if (StringUtils.isEmpty(packageName)) {
                className.simpleName()
            } else {
                packageName + "." + className.simpleName()
            }

            val javaFile = JavaFile.builder(className.packageName(), classContent)
                .indent(DEFAULT_INDENTATION)
                .addFileComment(DEFAULT_FILE_COMMENT)
                .build()
            val sourceFile = session.filer.createSourceFile(qualifiedName, originatingElement)
            sourceFile.openWriter().use { writer -> writer.write(javaFile.toString()) }
        } catch (e: IOException) {
            throw AbortProcessingException(
                session.logger,
                originatingElement,
                "Could not write generated class $className: ${e.message}"
            )
        }
    }

    private companion object {
        const val DEFAULT_INDENTATION = "    "
        const val DEFAULT_FILE_COMMENT = "Boring YURI generated this code for you. Do not modify!"
    }
}