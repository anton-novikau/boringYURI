package boringyuri.sample.uri

import android.net.Uri
import boringyuri.sample.data.Address
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LocationUriBuilderImplTest {

    private lateinit var baseUri: String
    private lateinit var uriBuilder: LocationUriBuilder

    @Before
    fun setUp() {
        baseUri = "https://maps.example.com/maps/api"
        uriBuilder = LocationUriBuilderImpl()
    }

    @Test
    fun buildStaticMapUri() {
        val lat = 0.01
        val lng = 0.02
        val expected = Uri.parse(
            "$baseUri/staticmap?lat=$lat&lng=$lng&sensor=true&zoom=2.5"
        )
        val actual = uriBuilder.buildStaticMapUri(lat, lng)

        assertEquals(expected, actual)
    }

    @Test
    fun buildAddressUri() {
        val address = Address("Minsk", "Nemiga")
        val expected = Uri.parse("$baseUri/geocode?address=Minsk%3BNemiga%3Bnull&sensor=true")
        val actual = uriBuilder.buildAddressUri(address)
        assertEquals(expected, actual)
    }

    @Test
    fun buildGeocodeUri() {
        val lat = 1L
        val lng = 2L
        val expected = Uri.parse(
            "$baseUri/geocode?latlng=$lat%2C$lng&address=Minsk%3BNemiga%3Bnull&sensor=true"
        )
        val address = Address("Minsk", "Nemiga")
        val actual = uriBuilder.buildGeocodeUri(lat to lng, address)

        assertEquals(expected, actual)
    }

    @Test
    fun buildShowPinsUri() {
        val latLng = arrayOf(1L to 2L, 3L to 4L)
        val latLngParams = latLng.joinToString(separator = "&") { (lat, lng) ->
            "latlng=$lat%2C$lng"
        }

        val expected = Uri.parse("$baseUri/pins?$latLngParams&zoom=4.5")
        val actual = uriBuilder.buildShowPinsUri(latLng)

        assertEquals(expected, actual)
    }

    @Test
    fun buildShowPinsByCoordinatesUri() {
        val coordinates = arrayOf(
            doubleArrayOf(0.1, 0.2),
            doubleArrayOf(0.3, 0.4),
            doubleArrayOf(0.5, 0.6),
        )

        val pinParams = coordinates.joinToString(separator = "&") { "pin=${it[0]}%3B${it[1]}" }

        val expected = Uri.parse(
            "$baseUri/pins?$pinParams&zoom=4.5"
        )
        val actual = uriBuilder.buildShowPinsByCoordinatesUri(coordinates)

        assertEquals(expected, actual)
    }
}