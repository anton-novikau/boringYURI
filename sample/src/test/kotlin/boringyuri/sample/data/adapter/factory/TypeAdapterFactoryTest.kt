/*
 * Copyright 2025 Anton Novikau
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

package boringyuri.sample.data.adapter.factory

import org.junit.Assert.assertSame
import org.junit.Test

class TypeAdapterFactoryTest {
    @Test
    fun testCreateAddressTypeAdapter() {
        val addressTypeAdapter = TypeAdapterFactory.createAddressTypeAdapter()
        val addressTypeAdapter2 = TypeAdapterFactory.createAddressTypeAdapter()
        assertSame(addressTypeAdapter, addressTypeAdapter2)
    }

    @Test
    fun testCreateAdminTypeAdapter() {
        val adminTypeAdapter = TypeAdapterFactory.createAdminTypeAdapter()
        val adminTypeAdapter2 = TypeAdapterFactory.createAdminTypeAdapter()
        assertSame(adminTypeAdapter, adminTypeAdapter2)
    }

    @Test
    fun testCreateCoordinatesTypeAdapter() {
        val coordinatesTypeAdapter = TypeAdapterFactory.createCoordinatesTypeAdapter()
        val coordinatesTypeAdapter2 = TypeAdapterFactory.createCoordinatesTypeAdapter()
        assertSame(coordinatesTypeAdapter, coordinatesTypeAdapter2)
    }

    @Test
    fun testCreateDoubleArrayTypeAdapter() {
        val doubleArrayTypeAdapter = TypeAdapterFactory.createDoubleArrayTypeAdapter()
        val doubleArrayTypeAdapter2 = TypeAdapterFactory.createDoubleArrayTypeAdapter()
        assertSame(doubleArrayTypeAdapter, doubleArrayTypeAdapter2)
    }

    @Test
    fun testCreateRectTypeAdapter() {
        val rectTypeAdapter = TypeAdapterFactory.createRectTypeAdapter()
        val rectTypeAdapter2 = TypeAdapterFactory.createRectTypeAdapter()
        assertSame(rectTypeAdapter, rectTypeAdapter2)
    }

    @Test
    fun testCreateUserTypeAdapter() {
        val userTypeAdapter = TypeAdapterFactory.createUserTypeAdapter()
        val userTypeAdapter2 = TypeAdapterFactory.createUserTypeAdapter()
        assertSame(userTypeAdapter, userTypeAdapter2)
    }
}