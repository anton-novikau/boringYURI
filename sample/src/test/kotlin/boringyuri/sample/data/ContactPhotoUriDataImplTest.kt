package boringyuri.sample.data

import android.graphics.Rect
import android.net.Uri
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ContactPhotoUriDataImplTest {
    private lateinit var testGroup: String
    private var testId: Long = 0
    private lateinit var testDimension: Rect
    private lateinit var uriString: String
    private lateinit var testUri: Uri

    private lateinit var contactPhotoUriData: ContactPhotoUriData
    @Before
    fun setUp() {
        testGroup = "testGroup"
        testId = 123L
        testDimension = Rect(0, 0, 300, 300)
        uriString = "content://contacts/groups/$testGroup/person/$testId?" +
                "desired_dimensions=${testDimension.flattenToString()}"
        testUri = Uri.parse(uriString)

        contactPhotoUriData = ContactPhotoUriDataImpl(testUri)
    }

    @Test
    fun testGroupCorrect() {
        assertEquals(testGroup, contactPhotoUriData.group)
    }

    @Test
    fun testContactIdCorrect() {
        assertEquals(testId, contactPhotoUriData.contactId)
    }

    @Test
    fun testDesiredDimensCorrect() {
        assertEquals(testDimension, contactPhotoUriData.desiredDimens)
    }

    @Test
    fun testToStringCorrect() {
        assertEquals(uriString, contactPhotoUriData.toString())
    }
}