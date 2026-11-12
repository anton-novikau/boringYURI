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

package boringyuri.sample.uri

import android.net.Uri
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BackgroundProviderUriBuilderImplTest {

    private lateinit var uriBuilder: BackgroundProviderUriBuilder
    private lateinit var baseUriString: String

    @Before
    fun setUp() {
        uriBuilder = BackgroundProviderUriBuilderImpl()
        baseUriString = "content://boringyuri.sample.backgrounds/bg"
    }

    @Test
    fun buildColorBackgroundUri() {
        val expected = Uri.parse("$baseUriString/color/255")
        val actual = uriBuilder.buildColorBackgroundUri(255)

        assertEquals(expected, actual)
    }

    @Test
    fun buildGalleryBackgroundUri() {
        val expected = Uri.parse("$baseUriString/original/123")
        val actual = uriBuilder.buildGalleryBackgroundUri(123)

        assertEquals(expected, actual)
    }

    @Test
    fun buildCroppedBackgroundUri() {
        val backgroundId = "bgid1"
        val expected = Uri.parse("$baseUriString/thumbnail/$backgroundId?orientation=1")
        val actual = uriBuilder.buildCroppedBackgroundUri(backgroundId, 1)

        assertEquals(expected, actual)
    }

    @Test
    fun buildDebugBackgroundUri() {
        val expected = Uri.parse("$baseUriString/debug")
        val actual = uriBuilder.buildDebugBackgroundUri()

        assertEquals(expected, actual)
    }
}