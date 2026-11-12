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
class AlbumImplTest {

    private lateinit var uriString: String
    private lateinit var uri: Uri

    private lateinit var album: Album

    @Before
    fun setUp() {
        uriString = "content://albums/album/myAlbum/123"
        uri = Uri.parse(uriString)
        album = AlbumImpl(uri)
    }

    @Test
    fun testCategoryCorrect() {
        assertEquals("myAlbum", album.getCategory())
    }

    @Test
    fun testIdCorrect() {
        assertEquals(123L, album.getId())
    }

    @Test
    fun testAuthorCorrect() {
        assertEquals("John Doe", album.getAuthor())
    }

    @Test
    fun testToStringCorrect() {
        assertEquals(album.toString(), uriString)
    }
}