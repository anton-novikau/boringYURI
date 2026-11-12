package boringyuri.sample.uri

import android.net.Uri
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

private const val DELTA = 0.0
private const val DEFAULT_LATITUDE = 53.893009
private const val DEFAULT_LONGITUDE = 27.567444

/**
 * Tests the data class generated for [LocationUriBuilder.buildStaticMapUri] with a custom name
 * in `@WithUriData("StaticMapData")`.
 */
@RunWith(RobolectricTestRunner::class)
class StaticMapDataTest {
    private lateinit var baseUri: String
    private lateinit var testUriString: String

    private lateinit var staticMapData: StaticMapData

    @Before
    fun setUp() {
        baseUri = "https://maps.example.com/maps/api/staticmap"
        testUriString = "$baseUri?lat=0.01&lng=-0.02&sensor=true&zoom=2.5"

        staticMapData = StaticMapData(Uri.parse(testUriString))
    }

    @Test
    fun testLatitudeCorrect() {
        assertEquals(0.01, staticMapData.latitude, DELTA)
    }

    @Test
    fun testLongitudeCorrect() {
        assertEquals(-0.02, staticMapData.longitude, DELTA)
    }

    @Test
    fun testSensorCorrect() {
        assertTrue(staticMapData.isSensor)
    }

    @Test
    fun testZoomCorrect() {
        assertEquals(2.5, staticMapData.zoom, DELTA)
    }

    @Test
    fun testToStringCorrect() {
        assertEquals(testUriString, staticMapData.toString())
    }

    @Test
    fun testCoordinatesAreMissing_ReturnDefaultValues() {
        val data = StaticMapData(Uri.parse(baseUri))

        assertEquals(DEFAULT_LATITUDE, data.latitude, DELTA)
        assertEquals(DEFAULT_LONGITUDE, data.longitude, DELTA)
    }

    @Test
    fun testCoordinatesAreInvalid_ReturnDefaultValues() {
        val data = StaticMapData(Uri.parse("$baseUri?lat=north&lng=east"))

        assertEquals(DEFAULT_LATITUDE, data.latitude, DELTA)
        assertEquals(DEFAULT_LONGITUDE, data.longitude, DELTA)
    }

    @Test
    fun testConstantParams_AreNotReadFromUri() {
        // constant query parameters are defined in the builder method annotations,
        // so the getters always return the constant values
        val data = StaticMapData(Uri.parse("$baseUri?sensor=false&zoom=10"))

        assertTrue(data.isSensor)
        assertEquals(2.5, data.zoom, DELTA)
    }

    @Test
    fun testParseUriBuiltWithLocationUriBuilder() {
        val uri = LocationUriBuilderImpl().buildStaticMapUri(37.773972, -122.431297)
        val data = StaticMapData(uri)

        assertEquals(37.773972, data.latitude, DELTA)
        assertEquals(-122.431297, data.longitude, DELTA)
        assertTrue(data.isSensor)
        assertEquals(2.5, data.zoom, DELTA)
    }
}
