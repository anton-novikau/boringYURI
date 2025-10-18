package boringyuri.sample.uri

import android.net.Uri
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BackgroundProviderUriBuilderImplTest {

    private lateinit var uriBuilder: BackgroundProviderUriBuilder
    private lateinit var baseUriString: String

    @Before
    fun setUp() {
        uriBuilder = BackgroundProviderUriBuilderImpl()
        baseUriString = "content://boringyuri.sample.backgrounds/bg"
    }

    @Test
    fun buildColorBackgroundUri() {
        val expected = Uri.parse("$baseUriString/color/255")
        val actual = uriBuilder.buildColorBackgroundUri(255)

        assertEquals(expected, actual)
    }

    @Test
    fun buildGalleryBackgroundUri() {
        val expected = Uri.parse("$baseUriString/original/123")
        val actual = uriBuilder.buildGalleryBackgroundUri(123)

        assertEquals(expected, actual)
    }

    @Test
    fun buildCroppedBackgroundUri() {
        val backgroundId = "bgid1"
        val expected = Uri.parse("$baseUriString/thumbnail/$backgroundId?orientation=1")
        val actual = uriBuilder.buildCroppedBackgroundUri(backgroundId, 1)

        assertEquals(expected, actual)
    }

    @Test
    fun buildDebugBackgroundUri() {
        val expected = Uri.parse("$baseUriString/debug")
        val actual = uriBuilder.buildDebugBackgroundUri()

        assertEquals(expected, actual)
    }
}