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
 * Tests the data class generated for [LocationUriBuilder.buildShowPinsUri] with `@WithUriData`.
 */
@RunWith(RobolectricTestRunner::class)
class ShowPinsUriDataTest {
    private lateinit var baseUri: String
    private lateinit var testUriString: String

    private lateinit var showPinsUriData: ShowPinsUriData

    @Before
    fun setUp() {
        baseUri = "https://maps.example.com/maps/api/pins"
        // CoordinatesTypeAdapter joins the coordinates with ','
        testUriString = "$baseUri?latlng=1%2C2&latlng=-3%2C4&zoom=4.5"

        showPinsUriData = ShowPinsUriData(Uri.parse(testUriString))
    }

    @Test
    fun testLatlngCorrect() {
        assertArrayEquals(
            arrayOf(1L to 2L, -3L to 4L),
            showPinsUriData.latlng,
        )
    }

    @Test
    fun testZoomCorrect() {
        assertEquals(4.5, showPinsUriData.zoom, 0.0)
    }

    @Test
    fun testToStringCorrect() {
        assertEquals(testUriString, showPinsUriData.toString())
    }

    @Test(expected = NullPointerException::class)
    fun testLatlngIsMissing_ThrowException() {
        ShowPinsUriData(Uri.parse(baseUri)).latlng
    }

    @Test
    fun testParseUriBuiltWithLocationUriBuilder() {
        val latlng = arrayOf(5L to 6L, 7L to 8L)
        val data = ShowPinsUriData(LocationUriBuilderImpl().buildShowPinsUri(latlng))

        assertArrayEquals(latlng, data.latlng)
        assertEquals(4.5, data.zoom, 0.0)
    }
}
