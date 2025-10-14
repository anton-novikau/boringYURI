package boringyuri.sample.data

import android.net.Uri
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PhotoAlbumImplTest {
    private val category = "vacation"
    private val id = 123L
    private val fileSize = 123456L
    private val thumbnail = "http://random.domain.com/random"
    private val mimeType = "image/jpeg"
    private val uriString =
        "content://albums/album/$category/$id?thumbnail=$thumbnail&mimeType=$mimeType&mediaType=unused&fileSize=$fileSize"
    private val uri = Uri.parse(uriString)

    private val sut = PhotoAlbumImpl(uri)

    @Test
    fun getCategory() {
        Assert.assertEquals(category, sut.getCategory())
    }

    @Test
    fun getId() {
        Assert.assertEquals(id, sut.getId())
    }

    @Test
    fun getMediaType() {
        Assert.assertEquals(mimeType, sut.getMediaType())
    }

    @Test
    fun getFileSize() {
        Assert.assertEquals(fileSize, sut.getFileSize())
    }

    @Test
    fun getThumbnailUri() {
        Assert.assertEquals(thumbnail, sut.getThumbnailUri().toString())
    }

    @Test
    fun testToString() {
        Assert.assertEquals(uriString, sut.toString())
    }

    @Test
    fun testAuthorCorrect() {
        Assert.assertEquals("John Doe", sut.getAuthor())
    }
}