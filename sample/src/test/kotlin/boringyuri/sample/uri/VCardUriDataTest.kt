package boringyuri.sample.uri

import android.net.Uri
import boringyuri.sample.data.Address
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Tests the data class generated for [ContactUriBuilder.buildVCardUri] with `@WithUriData`.
 */
@RunWith(RobolectricTestRunner::class)
class VCardUriDataTest {
    private lateinit var baseUri: String
    private lateinit var testUriString: String

    private lateinit var vCardUriData: VCardUriData

    @Before
    fun setUp() {
        baseUri = "content://boringyuri.sample.provider/file/vcard"
        testUriString = "$baseUri/42?firstName=John&lastName=Doe&address=Minsk%3BNemiga%3B220030"

        vCardUriData = VCardUriData(Uri.parse(testUriString))
    }

    @Test
    fun testContactIdCorrect() {
        assertEquals(42L, vCardUriData.contactId)
    }

    @Test
    fun testFirstNameCorrect() {
        assertEquals("John", vCardUriData.firstName)
    }

    @Test
    fun testLastNameCorrect() {
        assertEquals("Doe", vCardUriData.lastName)
    }

    @Test
    fun testHomeAddressCorrect() {
        // the getter name is based on the method parameter name, not the query parameter name
        assertEquals(Address("Minsk", "Nemiga", "220030"), vCardUriData.homeAddress)
    }

    @Test
    fun testToStringCorrect() {
        assertEquals(testUriString, vCardUriData.toString())
    }

    @Test
    fun testNullableParamsAreMissing_ReturnNull() {
        val data = VCardUriData(Uri.parse("$baseUri/42?firstName=John"))

        assertNull(data.lastName)
        assertNull(data.homeAddress)
    }

    @Test(expected = NullPointerException::class)
    fun testFirstNameIsMissing_ThrowException() {
        VCardUriData(Uri.parse("$baseUri/42")).firstName
    }

    @Test(expected = NullPointerException::class)
    fun testContactIdIsMissing_ThrowException() {
        VCardUriData(Uri.parse("$baseUri?firstName=John")).contactId
    }

    @Test
    fun testParseUriBuiltWithContactUriBuilder() {
        val address = Address("Minsk", "Nemiga", "220030")
        val uri = ContactUriBuilderImpl().buildVCardUri(24, "Jane", "Smith", address)
        val data = VCardUriData(uri)

        assertEquals(24L, data.contactId)
        assertEquals("Jane", data.firstName)
        assertEquals("Smith", data.lastName)
        assertEquals(address, data.homeAddress)
    }

    @Test
    fun testParseUriBuiltWithContactUriBuilder_AddressWithoutZipCode() {
        val address = Address("Minsk", "Nemiga")
        val uri = ContactUriBuilderImpl().buildVCardUri(24, "Jane", null, address)

        assertEquals(Uri.parse("$baseUri/24?firstName=Jane&address=Minsk%3BNemiga%3B"), uri)
        assertEquals(address, VCardUriData(uri).homeAddress)
    }

    @Test
    fun testParseUriBuiltWithContactUriBuilder_AddressWithoutStreet() {
        val address = Address("Minsk", null, "220030")
        val uri = ContactUriBuilderImpl().buildVCardUri(24, "Jane", null, address)

        assertEquals(Uri.parse("$baseUri/24?firstName=Jane&address=Minsk%3B%3B220030"), uri)
        assertEquals(address, VCardUriData(uri).homeAddress)
    }
}
