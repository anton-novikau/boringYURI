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
class PhotoAlbumImplTest {
    private lateinit var category: String
    private var id: Long = 0L
    private var fileSize: Long = 0L
    private lateinit var thumbnail: String
    private lateinit var mimeType: String
    private lateinit var uriString: String
    private lateinit var uri: Uri

    private lateinit var photoAlbum: PhotoAlbum

    @Before
    fun setUp() {
        category = "vacation"
        id = 123L
        fileSize = 123456L
        thumbnail = "http://random.domain.com/random"
        mimeType = "image/jpeg"
        uriString = "content://albums/album/$category/$id?thumbnail=$thumbnail&" +
                "mimeType=$mimeType&mediaType=unused&fileSize=$fileSize"
        uri = Uri.parse(uriString)

        photoAlbum = PhotoAlbumImpl(uri)
    }

    @Test
    fun getCategory() {
        assertEquals(category, photoAlbum.getCategory())
    }

    @Test
    fun getId() {
        assertEquals(id, photoAlbum.getId())
    }

    @Test
    fun getMediaType() {
        assertEquals(mimeType, photoAlbum.getMediaType())
    }

    @Test
    fun getFileSize() {
        assertEquals(fileSize, photoAlbum.getFileSize())
    }

    @Test
    fun getThumbnailUri() {
        assertEquals(thumbnail, photoAlbum.getThumbnailUri().toString())
    }

    @Test
    fun testToString() {
        assertEquals(uriString, photoAlbum.toString())
    }

    @Test
    fun testAuthorCorrect() {
        assertEquals("John Doe", photoAlbum.getAuthor())
    }
}