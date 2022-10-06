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

package boringyuri.processor.common.base

import androidx.room.compiler.processing.ExperimentalProcessingApi
import androidx.room.compiler.processing.XProcessingEnv
import boringyuri.processor.common.FileWriter
import boringyuri.processor.common.util.Logger

@OptIn(ExperimentalProcessingApi::class)
class ProcessingSession(val processingEnv: XProcessingEnv) {

    val logger = Logger(processingEnv.messager)

    val fileWriter = FileWriter(logger, processingEnv.filer)
}
