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

import android.graphics.Rect
import android.net.Uri
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ContactPhotoUriDataImplTest {
    private lateinit var testGroup: String
    private var testId: Long = 0
    private lateinit var testDimension: Rect
    private lateinit var uriString: String
    private lateinit var testUri: Uri

    private lateinit var contactPhotoUriData: ContactPhotoUriData
    @Before
    fun setUp() {
        testGroup = "testGroup"
        testId = 123L
        testDimension = Rect(0, 0, 300, 300)
        uriString = "content://contacts/groups/$testGroup/person/$testId?" +
                "desired_dimensions=${testDimension.flattenToString()}"
        testUri = Uri.parse(uriString)

        contactPhotoUriData = ContactPhotoUriDataImpl(testUri)
    }

    @Test
    fun testGroupCorrect() {
        assertEquals(testGroup, contactPhotoUriData.group)
    }

    @Test
    fun testContactIdCorrect() {
        assertEquals(testId, contactPhotoUriData.contactId)
    }

    @Test
    fun testDesiredDimensCorrect() {
        assertEquals(testDimension, contactPhotoUriData.desiredDimens)
    }

    @Test
    fun testToStringCorrect() {
        assertEquals(uriString, contactPhotoUriData.toString())
    }
}