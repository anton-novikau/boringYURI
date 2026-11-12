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
class VideoAlbumImplTest {

    private lateinit var category: String
    private var id: Long = 0L
    private var duration: Long = 0L
    private var fileSize: Long = 0L
    private lateinit var  mimeType: String
    private lateinit var  uriString: String
    private lateinit var  uri: Uri

    private lateinit var videoAlbum: VideoAlbum

    @Before
    fun setUp() {
        category = "vacation"
        id = 123L
        duration = 1234567L
        fileSize = 456789L
        mimeType = "video/mp4"
        uriString = "content://albums/album/$category/$id?" +
                "mediaType=$mimeType&totalDuration=$duration&fileSize=$fileSize"
        uri = Uri.parse(uriString)

        videoAlbum = VideoAlbumImpl(uri)
    }

    @Test
    fun getTotalDuration() {
        assertEquals(duration, videoAlbum.getTotalDuration())
    }

    @Test
    fun getCategory() {
        assertEquals(category, videoAlbum.getCategory())
    }

    @Test
    fun getId() {
        assertEquals(id, videoAlbum.getId())
    }

    @Test
    fun getMediaType() {
        assertEquals(mimeType, videoAlbum.getMediaType())
    }

    @Test
    fun getFileSize() {
        assertEquals(fileSize, videoAlbum.getFileSize())
    }

    @Test
    fun testToString() {
        assertEquals(uriString, videoAlbum.toString())
    }

    @Test
    fun testAuthorCorrect() {
        assertEquals("John Doe", videoAlbum.getAuthor())
    }
}
