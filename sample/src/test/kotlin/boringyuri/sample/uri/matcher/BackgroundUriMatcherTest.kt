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

package boringyuri.sample.uri.matcher

import android.content.UriMatcher
import android.net.Uri
import boringyuri.sample.BuildConfig
import boringyuri.sample.uri.BackgroundProviderUriBuilder
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BackgroundUriMatcherTest {
    private lateinit var uriMatcher: BackgroundUriMatcher

    @Before
    fun setUp() {
        uriMatcher = BackgroundUriMatcher()
    }

    @Test(expected = UnsupportedOperationException::class)
    fun addURI() {
        uriMatcher.addURI("", "", 1)
    }

    @Test
    fun matchCodeColor() {
        assertEquals(
            BackgroundProviderUriBuilder.Contract.CODE_COLOR,
            uriMatcher.match(Uri.parse("content://boringyuri.sample.backgrounds/bg/color/1"))
        )
    }

    @Test
    fun matchCodeOriginal() {
        assertEquals(
            BackgroundProviderUriBuilder.Contract.CODE_ORIGINAL,
            uriMatcher.match(Uri.parse("content://boringyuri.sample.backgrounds/bg/original/1"))
        )
    }

    @Test
    fun matchCodeCropped() {
        assertEquals(
            BackgroundProviderUriBuilder.Contract.CODE_CROPPED,
            uriMatcher.match(Uri.parse("content://boringyuri.sample.backgrounds/bg/thumbnail/abc"))
        )
    }

    @Test
    fun matchCodeDebugOnly() {
        // DEBUG_ONLY is a build config dependant flag, not a constant
        @Suppress("KotlinConstantConditions")
        val expectedMatchCode = if (BuildConfig.DEBUG_ONLY) {
            BackgroundProviderUriBuilder.Contract.CODE_DEBUG
        } else {
            UriMatcher.NO_MATCH
        }

        assertEquals(
            expectedMatchCode,
            uriMatcher.match(Uri.parse("content://boringyuri.sample.backgrounds/bg/debug"))
        )
    }

    @Test
    fun matchUnknownCode() {
        assertEquals(
            UriMatcher.NO_MATCH,
            uriMatcher.match(Uri.parse("content://boringyuri.sample.backgrounds/bg/random"))
        )
    }
}