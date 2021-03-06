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
package boringyuri.processor.util

import javax.annotation.processing.Messager
import javax.lang.model.element.Element
import javax.tools.Diagnostic

class Logger(private val logWriter: Messager) {

    fun error(
        e: Element?,
        msg: String,
        vararg args: Any?
    ) = String.format(msg, *args).also { logWriter.printMessage(Diagnostic.Kind.ERROR, it, e) }

    fun warn(
        e: Element?,
        msg: String,
        vararg args: Any?
    ) = String.format(msg, *args).also { logWriter.printMessage(Diagnostic.Kind.WARNING, it, e) }

    fun info(
        e: Element?,
        msg: String,
        vararg args: Any?
    ) = String.format(msg, *args).also { logWriter.printMessage(Diagnostic.Kind.NOTE, it, e) }

}