/*
 * Copyright 2022 Anton Novikau
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

package boringyuri.processor.common.visitor

import androidx.room.compiler.processing.XArrayType
import androidx.room.compiler.processing.XType

abstract class AbstractTypeVisitor<R, P> : TypeVisitor<R, P> {
    override fun visit(type: XType, param: P): R? = null

    override fun visitArray(type: XArrayType, param: P): R? = null
}