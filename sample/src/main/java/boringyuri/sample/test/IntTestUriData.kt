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

@UriData("/path/segment/{int}/{int_with_default}")
interface IntTestUriData {

    @Path("int")
    fun getIntSegment(): Int

    @Path("int_with_default")
    @DefaultValue("1")
    fun getIntWithDefaultSegment(): Int

    @Param
    fun getNullableParam(): Int?

    @Param
    fun getNonNullParam(): Int

    @Param
    @DefaultValue("1")
    fun getNonNullWithDefaultParam(): Int

    @Param
    @DefaultValue("1")
    fun getNullableWithDefaultParam(): Int?

    @Param
    fun getNullableArrayParam(): Array<Int>?

    @Param
    fun getNonNullArrayParam(): IntArray

    @Param
    @DefaultValue("1")
    fun getNonNullWithDefaultArrayParam(): IntArray

    @Param
    @DefaultValue("1")
    fun getNullableWithDefaultArrayParam(): Array<Int>?

}