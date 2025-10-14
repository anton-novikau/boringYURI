package boringyuri.sample.data

import android.net.Uri
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class VideoAlbumImplTest {

    private val category = "vacation"
    private val id = 123L
    private val duration = 1234567L
    private val fileSize = 456789L
    private val mimeType = "video/mp4"
    private val uriString =
        "content://albums/album/$category/$id?mediaType=$mimeType&totalDuration=$duration&fileSize=$fileSize"
    private val uri = Uri.parse(uriString)

    private val sut = VideoAlbumImpl(uri)

    @Test
    fun getTotalDuration() {
        Assert.assertEquals(duration, sut.getTotalDuration())
    }

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
    fun testToString() {
        Assert.assertEquals(uriString, sut.toString())
    }

    @Test
    fun testAuthorCorrect() {
        Assert.assertEquals("John Doe", sut.getAuthor())
    }
}
