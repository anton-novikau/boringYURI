package boringyuri.sample.data

import android.net.Uri
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MediaAlbumImplTest {
    private lateinit var testMediaType: String
    private var testFileSize: Long = 0L
    private lateinit var testUriString: String
    private lateinit var testUri: Uri
    private lateinit var mediaAlbum: MediaAlbum

    @Before
    fun setUp() {
        testMediaType = "photo"
        testFileSize = 2048L
        testUriString = "content://any/uri?mediaType=$testMediaType&fileSize=$testFileSize"
        testUri = Uri.parse(testUriString)
        mediaAlbum = MediaAlbumImpl(testUri)
    }

    @Test
    fun getMediaType() {
        assertEquals(testMediaType, mediaAlbum.getMediaType())
    }

    @Test
    fun getFileSize() {
        assertEquals(testFileSize, mediaAlbum.getFileSize())
    }

    @Test
    fun testToString() {
        assertEquals(testUriString, mediaAlbum.toString())
    }
}