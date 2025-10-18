package boringyuri.sample.data

import android.net.Uri
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CroppedBackgroundDataTest {
    private lateinit var testId: String
    private var testOrientation: Int = 0
    private lateinit var testUriString: String
    private lateinit var uri: Uri

    private lateinit var croppedBackgroundData: CroppedBackgroundData

    @Before
    fun setUp() {
        testId = "bg123"
        testOrientation = 2
        testUriString = "content://boringyuri.sample.backgrounds/bg/thumbnail/$testId?" +
                "orientation=$testOrientation"
        uri = Uri.parse(testUriString)

        croppedBackgroundData = CroppedBackgroundData(uri)
    }

    @Test
    fun testBackgroundIdCorrect() {
        assertEquals(testId, croppedBackgroundData.backgroundId)
    }

    @Test
    fun testOrientationCorrect() {
        assertEquals(testOrientation, croppedBackgroundData.orientation)
    }

    @Test
    fun testToStringCorrect() {
        assertEquals(testUriString, croppedBackgroundData.toString())
    }
}