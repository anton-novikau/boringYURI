package boringyuri.sample.uri

import android.net.Uri
import boringyuri.sample.data.Address
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LocationUriBuilderImplTest {

    private val baseUri = "https://maps.example.com/maps/api"

    private val sut = LocationUriBuilderImpl()

    @Test
    fun buildStaticMapUri() {
        val lat = 0.01
        val lng = 0.02
        val expected = Uri.parse(
            "$baseUri/staticmap?lat=$lat&lng=$lng&sensor=true&zoom=2.5"
        )
        val actual = sut.buildStaticMapUri(lat, lng)

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun buildAddressUri() {
        val address = Address("Minsk", "Nemiga")
        val expected = Uri.parse("$baseUri/geocode?address=Minsk%3BNemiga%3Bnull&sensor=true")
        val actual = sut.buildAddressUri(address)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun buildGeocodeUri() {
        val lat = 1L
        val lng = 2L
        val expected = Uri.parse(
            "$baseUri/geocode?latlng=$lat%2C$lng&address=Minsk%3BNemiga%3Bnull&sensor=true"
        )
        val address = Address("Minsk", "Nemiga")
        val actual = sut.buildGeocodeUri(lat to lng, address)

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun buildShowPinsUri() {
        val latLng = arrayOf(1L to 2L, 3L to 4L)
        val latLngParams = latLng.joinToString(separator = "&") { (lat, lng) ->
            "latlng=$lat%2C$lng"
        }

        val expected = Uri.parse("$baseUri/pins?$latLngParams&zoom=4.5")
        val actual = sut.buildShowPinsUri(latLng)

        Assert.assertEquals(expected, actual)
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
        val actual = sut.buildShowPinsByCoordinatesUri(coordinates)

        Assert.assertEquals(expected, actual)
    }
}