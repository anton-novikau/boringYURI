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

package boringyuri.sample.test

import boringyuri.api.DefaultValue
import boringyuri.api.Param
import boringyuri.api.Path
import boringyuri.api.UriData

@UriData("/path/segment/{byte}/{byte_with_default}")
interface ByteTestUriData {

    @Path("byte")
    fun getByteSegment(): Byte

    @Path("byte_with_default")
    @DefaultValue("1")
    fun getByteWithDefaultSegment(): Byte

    @Param
    fun getNullableParam(): Byte?

    @Param
    fun getNonNullParam(): Byte

    @Param
    @DefaultValue("1")
    fun getNonNullWithDefaultParam(): Byte

    @Param
    @DefaultValue("1")
    fun getNullableWithDefaultParam(): Byte?

    @Param
    fun getNullableArrayParam(): Array<Byte>?

    @Param
    fun getNonNullArrayParam(): ByteArray

    @Param
    @DefaultValue("1")
    fun getNonNullWithDefaultArrayParam(): ByteArray

    @Param
    @DefaultValue("1")
    fun getNullableWithDefaultArrayParam(): Array<Byte>?

}