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

package boringyuri.sample.data

import android.net.Uri
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CroppedBackgroundDataTest {
    private lateinit var testId: String
    private var testOrientation: Int = 0
    private lateinit var testUriString: String
    private lateinit var uri: Uri

    private lateinit var croppedBackgroundData: CroppedBackgroundData

    @Before
    fun setUp() {
        testId = "bg123"
        testOrientation = 2
        testUriString = "content://boringyuri.sample.backgrounds/bg/thumbnail/$testId?" +
                "orientation=$testOrientation"
        uri = Uri.parse(testUriString)

        croppedBackgroundData = CroppedBackgroundData(uri)
    }

    @Test
    fun testBackgroundIdCorrect() {
        assertEquals(testId, croppedBackgroundData.backgroundId)
    }

    @Test
    fun testOrientationCorrect() {
        assertEquals(testOrientation, croppedBackgroundData.orientation)
    }

    @Test
    fun testToStringCorrect() {
        assertEquals(testUriString, croppedBackgroundData.toString())
    }
}