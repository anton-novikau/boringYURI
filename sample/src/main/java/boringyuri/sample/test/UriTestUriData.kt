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

import android.net.Uri
import boringyuri.api.DefaultValue
import boringyuri.api.Param
import boringyuri.api.Path
import boringyuri.api.UriData

@UriData("/path/segment/{uri}")
interface UriTestUriData {

    @Path("uri")
    fun getUriSegment(): Uri

    @Param
    fun getNullableParam(): Uri?

    @Param
    fun getNonNullParam(): Uri

    @Param
    @DefaultValue("https://example.com/uri")
    fun getNonNullWithDefaultParam(): Uri

    @Param
    fun getNullableArrayParam(): Array<Uri>?

    @Param
    fun getNonNullArrayParam(): Array<Uri>

    @Param
    @DefaultValue("https://example.com/uri")
    fun getNonNullWithDefaultArrayParam(): Array<Uri>

}