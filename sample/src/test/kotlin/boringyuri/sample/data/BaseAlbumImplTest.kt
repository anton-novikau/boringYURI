package boringyuri.sample.data

import android.net.Uri
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BaseAlbumImplTest {
    private val category = "myCategory"
    private val author = "Favorite Author"
    private val uriString = "content://albums/album/$category?author=$author"
    private val uri = Uri.parse(uriString)

    private val sut = BaseAlbumImpl(uri)

    @Test
    fun testCategoryCorrect() {
        assertEquals(category, sut.getCategory())
    }

    @Test
    fun testAuthorCorrect() {
        assertEquals(author, sut.getAuthor())
    }

    @Test
    fun testToStringCorrect() {
        assertEquals(sut.toString(), uriString)
    }
}