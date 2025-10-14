package boringyuri.sample.data

import android.graphics.Rect
import android.net.Uri
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ContactPhotoUriDataImplTest {
    private val testGroup = "testGroup"
    private val testId = 123L
    private val testDimension = Rect(0, 0, 300, 300)

    private val uriString =
        "content://contacts/groups/$testGroup/person/$testId?desired_dimensions=${testDimension.flattenToString()}"
    private val testUri = Uri.parse(uriString)

    private val sut = ContactPhotoUriDataImpl(testUri)

    @Test
    fun testGroupCorrect() {
        Assert.assertEquals(testGroup, sut.group)
    }

    @Test
    fun testContactIdCorrect() {
        Assert.assertEquals(testId, sut.contactId)
    }

    @Test
    fun testDesiredDimensCorrect() {
        Assert.assertEquals(testDimension, sut.desiredDimens)
    }

    @Test
    fun testToStringCorrect() {
        Assert.assertEquals(uriString, sut.toString())
    }
}