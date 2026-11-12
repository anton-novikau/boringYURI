package boringyuri.sample.uri

import android.graphics.Rect
import android.net.Uri
import boringyuri.sample.data.Address
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ContactUriBuilderImplTest {

    private lateinit var baseUri: String
    private lateinit var uriBuilder: ContactUriBuilder

    @Before
    fun setUp() {
        baseUri = "content://boringyuri.sample.provider"
        uriBuilder = ContactUriBuilderImpl()
    }

    @Test
    fun buildContactDataUri() {
        val expected = Uri.parse("$baseUri/data/42")
        val actual = uriBuilder.buildContactDataUri(42)

        assertEquals(expected, actual)
    }

    @Test
    fun buildContactPhotoUri() {
        // RectTypeAdapter serializes Rect with Rect.flattenToString(): "left top right bottom"
        val expected = Uri.parse(
            "$baseUri/file/photo/best%20friends/42?desired_dimensions=0%200%20100%20200"
        )
        val actual = uriBuilder.buildContactPhotoUri("best friends", 42, Rect(0, 0, 100, 200))

        assertEquals(expected, actual)
    }

    @Test
    fun buildContactPhotoUri_NullDesiredDimens_ParamIsOmitted() {
        val expected = Uri.parse("$baseUri/file/photo/friends/42")
        val actual = uriBuilder.buildContactPhotoUri("friends", 42, null)

        assertEquals(expected, actual)
    }

    @Test
    fun buildVCardUri() {
        val expected = Uri.parse(
            "$baseUri/file/vcard/42?firstName=John&lastName=Doe&address=Minsk%3BNemiga%3B220030"
        )
        val actual = uriBuilder.buildVCardUri(42, "John", "Doe", Address("Minsk", "Nemiga", "220030"))

        assertEquals(expected, actual)
    }

    @Test
    fun buildVCardUri_NullValues_ParamsAreOmitted() {
        val expected = Uri.parse("$baseUri/file/vcard/42?firstName=John")
        val actual = uriBuilder.buildVCardUri(42, "John", null, null)

        assertEquals(expected, actual)
    }

    @Test
    fun buildNoPlayServicesVCard() {
        val expected = Uri.parse("$baseUri/file/huawei/42")
        val actual = uriBuilder.buildNoPlayServicesVCard(42)

        assertEquals(expected, actual)
    }

    @Test
    fun create_ReturnsGeneratedImplementation() {
        assertEquals(ContactUriBuilderImpl::class.java, ContactUriBuilder.create().javaClass)
    }
}
