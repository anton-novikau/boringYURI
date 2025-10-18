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