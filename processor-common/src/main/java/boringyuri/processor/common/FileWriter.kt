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

package boringyuri.processor.common

import androidx.room.compiler.processing.XFiler
import boringyuri.processor.common.base.AbortProcessingException
import boringyuri.processor.common.util.Logger
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import java.io.IOException

class FileWriter(private val logger: Logger, private val xFiler: XFiler) {

    fun writeSourceFile(
        className: ClassName,
        classContent: TypeSpec,
        xFilerMode: XFiler.Mode
    ) {
        try {
            val javaFile = JavaFile.builder(className.packageName(), classContent)
                .indent(DEFAULT_INDENTATION)
                .addFileComment(DEFAULT_FILE_COMMENT)
                .build()

            xFiler.write(javaFile, xFilerMode)
        } catch (e: IOException) {
            throw AbortProcessingException(
                logger,
                null,
                e,
                "Could not write generated class $className: ${e.message}"
            )
        }
    }

    companion object {
        const val DEFAULT_INDENTATION = "    "
        const val DEFAULT_FILE_COMMENT = "Boring YURI generated this code for you. Do not modify!"
    }
}
