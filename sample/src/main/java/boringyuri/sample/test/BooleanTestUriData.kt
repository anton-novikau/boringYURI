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

@UriData("/path/segment/{bool}/{bool_with_default}")
interface BooleanTestUriData {

    @Path("bool")
    fun getBoolSegment(): Boolean

    @Path("bool_with_default")
    @DefaultValue("true")
    fun getBoolWithDefaultSegment(): Boolean

    @Param
    fun getNullableParam(): Boolean?

    @Param
    fun getNonNullParam(): Boolean

    @Param
    @DefaultValue("true")
    fun getNonNullWithDefaultParam(): Boolean

    @Param
    @DefaultValue("true")
    fun getNullableWithDefaultParam(): Boolean?

    @Param
    fun getNullableArrayParam(): Array<Boolean>?

    @Param
    fun getNonNullArrayParam(): BooleanArray

    @Param
    @DefaultValue("true")
    fun getNonNullWithDefaultArrayParam(): BooleanArray

    @Param
    @DefaultValue("true")
    fun getNullableWithDefaultArrayParam(): Array<Boolean>?
}