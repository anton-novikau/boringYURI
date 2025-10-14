package boringyuri.sample.data

import android.net.Uri
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MediaAlbumImplTest {
    private val testMediaType = "photo"
    private val testFileSize = 2048L
    private val testUriString = "content://any/uri?mediaType=$testMediaType&fileSize=$testFileSize"
    private val testUri = Uri.parse(testUriString)
    private val sut = MediaAlbumImpl(testUri)

    @Test
    fun getMediaType() {
        Assert.assertEquals(testMediaType, sut.getMediaType())
    }

    @Test
    fun getFileSize() {
        Assert.assertEquals(testFileSize, sut.getFileSize())
    }

    @Test
    fun testToString() {
        Assert.assertEquals(testUriString, sut.toString())
    }
}