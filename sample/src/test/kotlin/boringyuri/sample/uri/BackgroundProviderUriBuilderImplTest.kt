package boringyuri.sample.uri

import android.net.Uri
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BackgroundProviderUriBuilderImplTest {

    private val sut = BackgroundProviderUriBuilderImpl()
    val baseUriString = "content://boringyuri.sample.backgrounds/bg"

    @Test
    fun buildColorBackgroundUri() {
        val expected = Uri.parse("$baseUriString/color/255")
        val actual = sut.buildColorBackgroundUri(255)

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun buildGalleryBackgroundUri() {
        val expected = Uri.parse("$baseUriString/original/123")
        val actual = sut.buildGalleryBackgroundUri(123)

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun buildCroppedBackgroundUri() {
        val backgroundId = "bgid1"
        val expected = Uri.parse("$baseUriString/thumbnail/$backgroundId?orientation=1")
        val actual = sut.buildCroppedBackgroundUri(backgroundId, 1)

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun buildDebugBackgroundUri() {
        val expected = Uri.parse("$baseUriString/debug")
        val actual = sut.buildDebugBackgroundUri()

        Assert.assertEquals(expected, actual)
    }
}