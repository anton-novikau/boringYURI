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
import boringyuri.api.adapter.TypeAdapter
import boringyuri.sample.data.adapter.DoubleArrayTypeAdapter

@UriData("/path/segment/{double}/{double_with_default}")
interface DoubleTestUriData {

    @Path("double")
    fun getDoubleSegment(): Double

    @Path("double_with_default")
    @DefaultValue("1.2")
    fun getDoubleWithDefaultSegment(): Double

    @Param
    fun getNullableParam(): Double?

    @Param
    fun getNonNullParam(): Double

    @Param
    @DefaultValue("1.2")
    fun getNonNullWithDefaultParam(): Double

    @Param
    @DefaultValue("1.2")
    fun getNullableWithDefaultParam(): Double?

    @Param
    fun getNullableArrayParam(): Array<Double>?

    @Param
    fun getNonNullArrayParam(): DoubleArray

    @Param
    @DefaultValue("1.2")
    fun getNonNullWithDefaultArrayParam(): DoubleArray

    @Param
    @DefaultValue("1.2")
    fun getNullableWithDefaultArrayParam(): Array<Double>?

    @Param
    @TypeAdapter(DoubleArrayTypeAdapter::class)
    fun getNullableDoubleArrayParam(): Array<DoubleArray>?

    @Param
    @TypeAdapter(DoubleArrayTypeAdapter::class)
    fun getNonNullDoubleArrayParam(): Array<DoubleArray>

    @Param
    @DefaultValue("1.2;2.3")
    @TypeAdapter(DoubleArrayTypeAdapter::class)
    fun getNonNullWithDefaultDoubleArrayParam(): Array<DoubleArray>

    @Param
    @DefaultValue("1.2;2.3")
    @TypeAdapter(DoubleArrayTypeAdapter::class)
    fun getNullableWithDefaultDoubleArrayParam(): Array<DoubleArray>?

}