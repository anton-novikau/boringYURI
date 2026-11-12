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

import android.graphics.Rect
import android.net.Uri
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Tests the data class generated for [ContactUriBuilder.buildContactPhotoUri] with `@WithUriData`.
 */
@RunWith(RobolectricTestRunner::class)
class ContactPhotoUriDataTest {
    private lateinit var baseUri: String
    private lateinit var testUriString: String

    private lateinit var contactPhotoUriData: ContactPhotoUriData

    @Before
    fun setUp() {
        baseUri = "content://boringyuri.sample.provider/file/photo"
        testUriString = "$baseUri/best%20friends/42?desired_dimensions=0%200%20100%20200"

        contactPhotoUriData = ContactPhotoUriData(Uri.parse(testUriString))
    }

    @Test
    fun testGroupCorrect() {
        assertEquals("best friends", contactPhotoUriData.group)
    }

    @Test
    fun testContactIdCorrect() {
        assertEquals(42L, contactPhotoUriData.contactId)
    }

    @Test
    fun testDesiredDimensCorrect() {
        assertEquals(Rect(0, 0, 100, 200), contactPhotoUriData.desiredDimens)
    }

    @Test
    fun testToStringCorrect() {
        assertEquals(testUriString, contactPhotoUriData.toString())
    }

    @Test
    fun testDesiredDimensIsMissing_ReturnsNull() {
        val data = ContactPhotoUriData(Uri.parse("$baseUri/friends/42"))

        assertNull(data.desiredDimens)
    }

    @Test
    fun testContactIdIsInvalid_ReturnsZero() {
        val data = ContactPhotoUriData(Uri.parse("$baseUri/friends/abc"))

        assertEquals(0L, data.contactId)
    }

    @Test(expected = NullPointerException::class)
    fun testContactIdIsMissing_ThrowException() {
        ContactPhotoUriData(Uri.parse("$baseUri/friends")).contactId
    }

    @Test(expected = NullPointerException::class)
    fun testGroupIsMissing_ThrowException() {
        ContactPhotoUriData(Uri.parse(baseUri)).group
    }

    @Test
    fun testParseUriBuiltWithContactUriBuilder() {
        val uri = ContactUriBuilderImpl().buildContactPhotoUri("friends", 24, Rect(1, 2, 3, 4))
        val data = ContactPhotoUriData(uri)

        assertEquals("friends", data.group)
        assertEquals(24L, data.contactId)
        assertEquals(Rect(1, 2, 3, 4), data.desiredDimens)
    }
}
