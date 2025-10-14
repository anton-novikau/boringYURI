package boringyuri.sample.data

import android.net.Uri
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AlbumImplTest {

    private val uriString = "content://albums/album/myAlbum/123"
    private val uri = Uri.parse(uriString)

    val sut = AlbumImpl(uri)

    @Test
    fun testCategoryCorrect() {
        Assert.assertEquals("myAlbum", sut.getCategory())
    }

    @Test
    fun testIdCorrect() {
        Assert.assertEquals(123L, sut.getId())
    }

    @Test
    fun testAuthorCorrect() {
        Assert.assertEquals("John Doe", sut.getAuthor())
    }

    @Test
    fun testToStringCorrect() {
        Assert.assertEquals(sut.toString(), uriString)
    }
}