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
class BaseAlbumImplTest {
    private lateinit var category: String
    private lateinit var author: String
    private lateinit var uriString: String
    private lateinit var uri: Uri

    private lateinit var baseAlbum: BaseAlbum

    @Before
    fun setUp() {
        category = "myCategory"
        author = "Favorite Author"
        uriString = "content://albums/album/$category?author=$author"
        uri = Uri.parse(uriString)
        baseAlbum = BaseAlbumImpl(uri)
    }


    @Test
    fun testCategoryCorrect() {
        assertEquals(category, baseAlbum.getCategory())
    }

    @Test
    fun testAuthorCorrect() {
        assertEquals(author, baseAlbum.getAuthor())
    }

    @Test
    fun testToStringCorrect() {
        assertEquals(baseAlbum.toString(), uriString)
    }
}