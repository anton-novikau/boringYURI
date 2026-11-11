package boringyuri.sample.test

import android.net.Uri
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.RobolectricTestRunner

@RunWith(Enclosed::class)
class UriTestUriDataImplTest {
    @RunWith(ParameterizedRobolectricTestRunner::class)
    class UriTestUriDataNonNullTest(
        private val input: String,
        private val expected: String,
    ) {

        private lateinit var baseUri: String

        @Before
        fun setUp() {
            baseUri = "https://example.com/path/segment"
        }

        @Test
        fun getUriSegment() {
            val testUri = "$baseUri/$input"
            val uriData = UriTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getUriSegment().toString()
            )
        }

        @Test
        fun getNullableParam() {
            val testUri = "$baseUri?nullableParam=$input"
            val uriData: UriTestUriData = UriTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getNullableParam()?.toString()
            )
        }

        @Test
        fun getNonNullParam() {
            val testUri = "$baseUri?nonNullParam=$input"
            val uriData: UriTestUriData = UriTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getNonNullParam().toString()
            )
        }

        @Test
        fun getNonNullWithDefaultParam() {
            val testUri = "$baseUri?nonNullWithDefaultParam=$input"
            val uriData: UriTestUriData = UriTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getNonNullWithDefaultParam().toString()
            )
        }

        @Test
        fun getNullableArrayParam_OneParam() {
            val testUri = "$baseUri?nullableArrayParam=$input"
            val uriData: UriTestUriData = UriTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                listOf(expected),
                uriData.getNullableArrayParam()?.map { it.toString() },
            )
        }

        @Test
        fun getNonNullArrayParam_OneParam() {
            val testUri = "$baseUri?nonNullArrayParam=$input"
            val uriData: UriTestUriData = UriTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                listOf(expected),
                uriData.getNonNullArrayParam().map { it.toString() },
            )
        }

        @Test
        fun getNonNullWithDefaultArrayParam_OneParam() {
            val testUri = "$baseUri?nonNullWithDefaultArrayParam=$input"
            val uriData: UriTestUriData = UriTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                listOf(expected),
                uriData.getNonNullWithDefaultArrayParam().map { it.toString() },
            )
        }

        companion object {
            // the testParams() provider is used by test runner to provide the parameters for the tests
            @Suppress("unused")
            @JvmStatic
            @ParameterizedRobolectricTestRunner.Parameters(name = "input = {0}, expected = {1}")
            fun testParams(): List<Array<Any>> = listOf(
                // arrayOf(input, expected),
                arrayOf("https%3A%2F%2Fexample.com%2Fpath%3Fq%3D1", "https://example.com/path?q=1"),
                arrayOf("content%3A%2F%2Fauthority%2Fitem%2F42", "content://authority/item/42"),
                arrayOf("mailto%3Ajohn%40example.com", "mailto:john@example.com"),
                arrayOf("relative%2Fpath", "relative/path"),
            )
        }
    }

    @RunWith(ParameterizedRobolectricTestRunner::class)
    class UriTestUriDataNonNullArrayTest(
        private val input: List<String>,
        private val expectedArray: List<String>,
    ) {
        private lateinit var baseUri: String

        @Before
        fun setUp() {
            baseUri = "https://example.com/path/segment"
        }

        @Test
        fun getNonNullArrayParam_TwoParams() {
            val (expectedParam1, expectedParam2) = input
            val testUri = "$baseUri?nonNullArrayParam=$expectedParam1&nonNullArrayParam=$expectedParam2"
            val uriData: UriTestUriData = UriTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expectedArray,
                uriData.getNonNullArrayParam().map { it.toString() },
            )
        }

        @Test
        fun getNullableArrayParam_TwoParams() {
            val (expectedParam1, expectedParam2) = input
            val testUri = "$baseUri?nullableArrayParam=$expectedParam1&nullableArrayParam=$expectedParam2"
            val uriData: UriTestUriData = UriTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expectedArray,
                uriData.getNullableArrayParam()?.map { it.toString() },
            )
        }

        @Test
        fun getNonNullWithDefaultArrayParam_TwoParams() {
            val (expectedParam1, expectedParam2) = input
            val testUri = "$baseUri?nonNullWithDefaultArrayParam=$expectedParam1&nonNullWithDefaultArrayParam=$expectedParam2"
            val uriData: UriTestUriData = UriTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expectedArray,
                uriData.getNonNullWithDefaultArrayParam().map { it.toString() },
            )
        }

        companion object {
            // the testParams() provider is used by test runner to provide the parameters for the tests
            @Suppress("unused")
            @JvmStatic
            @ParameterizedRobolectricTestRunner.Parameters(name = "input = {0}, expectedArray = {1}")
            fun testParams() = listOf(
                // arrayOf(input, expectedArray)
                arrayOf(listOf("https%3A%2F%2Fexample.com%2Fa", "content%3A%2F%2Fauthority%2Fb"), listOf("https://example.com/a", "content://authority/b")),
                arrayOf(listOf("content%3A%2F%2Fauthority%2Fb", "https%3A%2F%2Fexample.com%2Fa"), listOf("content://authority/b", "https://example.com/a")),
            )
        }
    }

    @RunWith(RobolectricTestRunner::class)
    class UriTestUriDataNullableOrDefaultTest {
        private lateinit var baseUri: String

        @Before
        fun setUp() {
            baseUri = "https://example.com/path/segment"
        }

        @Test(expected = NullPointerException::class)
        fun getUriSegment_SegmentIsMissing_ThrowException() {
            val testUri = baseUri
            val uriData: UriTestUriData = UriTestUriDataImpl(Uri.parse(testUri))

            uriData.getUriSegment()
        }

        @Test(expected = NullPointerException::class)
        fun getNonNullParam_ParamIsMissing_ThrowException() {
            val testUri = baseUri
            val uriData: UriTestUriData = UriTestUriDataImpl(Uri.parse(testUri))

            uriData.getNonNullParam()
        }

        @Test(expected = NullPointerException::class)
        fun getNonNullArrayParam_ParamIsMissing_ThrowException() {
            val testUri = baseUri
            val uriData: UriTestUriData = UriTestUriDataImpl(Uri.parse(testUri))

            uriData.getNonNullArrayParam()
        }

        @Test
        fun getNullableParam() {
            val testUri = baseUri
            val uriData: UriTestUriData = UriTestUriDataImpl(Uri.parse(testUri))

            assertNull(uriData.getNullableParam())
        }

        @Test
        fun getNonNullWithDefaultParam() {
            val testUri = baseUri
            val uriData: UriTestUriData = UriTestUriDataImpl(Uri.parse(testUri))

            assertEquals("https://example.com/uri", uriData.getNonNullWithDefaultParam().toString())
        }

        @Test
        fun getNullableArrayParam() {
            val testUri = baseUri
            val uriData: UriTestUriData = UriTestUriDataImpl(Uri.parse(testUri))

            assertNull(uriData.getNullableArrayParam())
        }

        @Test
        fun getNonNullWithDefaultArrayParam() {
            val testUri = baseUri
            val uriData: UriTestUriData = UriTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                listOf("https://example.com/uri"),
                uriData.getNonNullWithDefaultArrayParam().map { it.toString() },
            )
        }

        @Test
        fun toString_ReturnsCorrectUri() {
            val testUri = "$baseUri/https%3A%2F%2Fexample.com?nonNullParam=content%3A%2F%2Fauthority"
            val uriData: UriTestUriData = UriTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                testUri,
                uriData.toString()
            )
        }
    }
}
