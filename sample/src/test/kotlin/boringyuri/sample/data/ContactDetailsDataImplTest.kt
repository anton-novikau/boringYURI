package boringyuri.sample.data

import android.net.Uri
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ContactDetailsDataImplTest {

    private val id = 123L
    private val title = "FamilyGuy"
    private val firstName = "Peter"
    private val lastName = "Griffin"
    private val city = "Quahog"
    private val street = "Spooner str."
    private val zipCode = "12345"
    private val address = "$city;$street;$zipCode"

    private val uriString =
        "content://contacts/users/user/$id/$title?firstName=$firstName&lastName=$lastName&address=$address"
    private val testUri = Uri.parse(uriString)

    private val sut = ContactDetailsDataImpl(testUri)

    @Test
    fun testIdCorrect() {
        Assert.assertEquals(id, sut.getId())
    }

    @Test
    fun testTitleCorrect() {
        Assert.assertEquals(title, sut.getTitle())
    }

    @Test
    fun testFirstNameCorrect() {
        Assert.assertEquals(firstName, sut.getFirstName())
    }

    @Test
    fun testLastNameCorrect() {
        Assert.assertEquals(lastName, sut.getLastName())
    }

    @Test
    fun testHomeAddressCorrect() {
        Assert.assertEquals(Address(city, street, zipCode), sut.getHomeAddress())
    }

    @Test
    fun testToStringCorrect() {
        Assert.assertEquals(uriString, sut.toString())
    }
}