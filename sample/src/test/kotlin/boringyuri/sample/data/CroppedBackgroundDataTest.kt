package boringyuri.sample.data

import android.net.Uri
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CroppedBackgroundDataTest {
    private val testId = "bg123"
    private val testOrientation = 2
    private val testUriString =
        "content://boringyuri.sample.backgrounds/bg/thumbnail/$testId?orientation=$testOrientation"
    private val uri = Uri.parse(testUriString)

    private val sut = CroppedBackgroundData(uri)

    @Test
    fun testBackgroundIdCorrect() {
        Assert.assertEquals(testId, sut.backgroundId)
    }

    @Test
    fun testOrientationCorrect() {
        Assert.assertEquals(testOrientation, sut.orientation)
    }

    @Test
    fun testToStringCorrect() {
        Assert.assertEquals(testUriString, sut.toString())
    }
}