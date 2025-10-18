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