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

package boringyuri.dagger.sample.uri

import android.net.Uri
import boringyuri.api.Param
import boringyuri.api.UriBuilder
import boringyuri.api.UriFactory
import boringyuri.api.WithUriData
import boringyuri.api.adapter.TypeAdapter
import boringyuri.api.constant.BooleanParam
import boringyuri.api.constant.DoubleParam
import boringyuri.dagger.sample.data.Address
import boringyuri.dagger.sample.data.adapter.CoordinatesTypeAdapter

@UriFactory(scheme = "https", authority = "maps.example.com")
interface LocationUriBuilder {
    @UriBuilder("/maps/api/staticmap")
    @BooleanParam(name = "sensor", value = true)
    @DoubleParam(name = "zoom", value = 2.5)
    @WithUriData
    fun buildStaticMapUri(
        @Param("lat") latitude: Double,
        @Param("lng") longitude: Double
    ): Uri

    @UriBuilder("/maps/api/geocode")
    @BooleanParam(name = "sensor", value = true)
    fun buildAddressUri(@Param address: Address): Uri

    @UriBuilder("/maps/api/geocode")
    @BooleanParam(name = "sensor", value = true)
    fun buildGeocodeUri(
        @Param @TypeAdapter(CoordinatesTypeAdapter::class) latlng: Pair<Long, Long>,
        @Param address: Address): Uri
}