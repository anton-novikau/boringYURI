package boringyuri.sample.uri.matcher

import android.content.UriMatcher
import android.net.Uri
import boringyuri.sample.uri.BackgroundProviderUriBuilder
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BackgroundUriMatcherTest {
    @Test(expected = UnsupportedOperationException::class)
    fun addURI() {
        BackgroundUriMatcher().addURI("", "", 1)
    }

    @Test
    fun match() {
        val sut = BackgroundUriMatcher()
        Assert.assertEquals(
            BackgroundProviderUriBuilder.Contract.CODE_COLOR,
            sut.match(Uri.parse("content://boringyuri.sample.backgrounds/bg/color/1"))
        )
        Assert.assertEquals(
            BackgroundProviderUriBuilder.Contract.CODE_ORIGINAL,
            sut.match(Uri.parse("content://boringyuri.sample.backgrounds/bg/original/1"))
        )
        Assert.assertEquals(
            BackgroundProviderUriBuilder.Contract.CODE_CROPPED,
            sut.match(Uri.parse("content://boringyuri.sample.backgrounds/bg/thumbnail/abc"))
        )
        Assert.assertEquals(
            BackgroundProviderUriBuilder.Contract.CODE_DEBUG,
            sut.match(Uri.parse("content://boringyuri.sample.backgrounds/bg/debug"))
        )
        Assert.assertEquals(
            UriMatcher.NO_MATCH,
            sut.match(Uri.parse("content://boringyuri.sample.backgrounds/bg/random"))
        )
    }
}