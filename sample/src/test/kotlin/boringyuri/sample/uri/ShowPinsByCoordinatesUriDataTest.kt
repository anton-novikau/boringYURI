/*
 * Copyright 2026 Anton Novikau
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

package boringyuri.sample.uri

import android.net.Uri
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Tests the data class generated for [LocationUriBuilder.buildShowPinsByCoordinatesUri]
 * with `@WithUriData`.
 */
@RunWith(RobolectricTestRunner::class)
class ShowPinsByCoordinatesUriDataTest {
    private lateinit var baseUri: String
    private lateinit var testUriString: String

    private lateinit var showPinsData: ShowPinsByCoordinatesUriData

    @Before
    fun setUp() {
        baseUri = "https://maps.example.com/maps/api/pins"
        // DoubleArrayTypeAdapter joins the coordinates with ';'
        testUriString = "$baseUri?pin=0.1%3B0.2&pin=-0.3%3B0.4&zoom=4.5"

        showPinsData = ShowPinsByCoordinatesUriData(Uri.parse(testUriString))
    }

    @Test
    fun testCoordinatesCorrect() {
        assertArrayEquals(
            arrayOf(doubleArrayOf(0.1, 0.2), doubleArrayOf(-0.3, 0.4)),
            showPinsData.coordinates,
        )
    }

    @Test
    fun testZoomCorrect() {
        assertEquals(4.5, showPinsData.zoom, 0.0)
    }

    @Test
    fun testToStringCorrect() {
        assertEquals(testUriString, showPinsData.toString())
    }

    @Test
    fun testCoordinatesAreMissing_ReturnDefaultValue() {
        val data = ShowPinsByCoordinatesUriData(Uri.parse(baseUri))

        assertArrayEquals(
            arrayOf(doubleArrayOf(53.893009, 27.567444)),
            data.coordinates,
        )
    }

    @Test
    fun testParseUriBuiltWithLocationUriBuilder() {
        val coordinates = arrayOf(doubleArrayOf(0.5, 0.6), doubleArrayOf(0.7, 0.8))
        val data = ShowPinsByCoordinatesUriData(
            LocationUriBuilderImpl().buildShowPinsByCoordinatesUri(coordinates)
        )

        assertArrayEquals(coordinates, data.coordinates)
        assertEquals(4.5, data.zoom, 0.0)
    }

    @Test
    fun testParseUriBuiltWithNullCoordinates_ReturnDefaultValue() {
        // the builder adds the default value to the Uri when the coordinates are null
        val uri = LocationUriBuilderImpl().buildShowPinsByCoordinatesUri(null)

        assertEquals(Uri.parse("$baseUri?pin=53.893009%3B27.567444&zoom=4.5"), uri)
        assertArrayEquals(
            arrayOf(doubleArrayOf(53.893009, 27.567444)),
            ShowPinsByCoordinatesUriData(uri).coordinates,
        )
    }
}
