package boringyuri.sample.data

import android.net.Uri
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ContactDetailsDataImplTest {

    private var id: Long = 0
    private lateinit var title: String
    private lateinit var firstName: String
    private lateinit var lastName: String
    private lateinit var city: String
    private lateinit var street: String
    private lateinit var zipCode: String
    private lateinit var address: String

    private lateinit var uriString: String
    private lateinit var testUri: Uri

    private lateinit var contactDetails: ContactDetailsData

    @Before
    fun setUp() {
        id = 123L
        title = "FamilyGuy"
        firstName = "Peter"
        lastName = "Griffin"
        city = "Quahog"
        street = "Spooner str."
        zipCode = "12345"
        address = "$city;$street;$zipCode"

        uriString =
            "content://contacts/users/user/$id/$title?firstName=$firstName&lastName=$lastName&address=$address"
        testUri = Uri.parse(uriString)

        contactDetails = ContactDetailsDataImpl(testUri)
    }

    @Test
    fun testIdCorrect() {
        assertEquals(id, contactDetails.getId())
    }

    @Test
    fun testTitleCorrect() {
        assertEquals(title, contactDetails.getTitle())
    }

    @Test
    fun testFirstNameCorrect() {
        assertEquals(firstName, contactDetails.getFirstName())
    }

    @Test
    fun testLastNameCorrect() {
        assertEquals(lastName, contactDetails.getLastName())
    }

    @Test
    fun testHomeAddressCorrect() {
        assertEquals(Address(city, street, zipCode), contactDetails.getHomeAddress())
    }

    @Test
    fun testToStringCorrect() {
        assertEquals(uriString, contactDetails.toString())
    }
}