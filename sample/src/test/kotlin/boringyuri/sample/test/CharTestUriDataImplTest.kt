package boringyuri.sample.test

import android.net.Uri
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.RobolectricTestRunner

@RunWith(Enclosed::class)
class CharTestUriDataImplTest {
    @RunWith(ParameterizedRobolectricTestRunner::class)
    class CharTestUriDataNonNullTest(
        private val input: String,
        private val expected: Char,
    ) {

        private lateinit var baseUri: String

        @Before
        fun setUp() {
            baseUri = "https://example.com/path/segment"
        }

        @Test
        fun getCharSegment() {
            val testUri = "$baseUri/$input"
            val uriData = CharTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getCharSegment()
            )
        }

        @Test
        fun getCharWithDefaultSegment() {
            val testUri = "$baseUri/x/$input"
            val uriData: CharTestUriData = CharTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getCharWithDefaultSegment()
            )
        }

        @Test
        fun getNullableParam() {
            val testUri = "$baseUri?nullableParam=$input"
            val uriData: CharTestUriData = CharTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getNullableParam()
            )
        }

        @Test
        fun getNonNullParam() {
            val testUri = "$baseUri?nonNullParam=$input"
            val uriData: CharTestUriData = CharTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getNonNullParam()
            )
        }

        @Test
        fun getNonNullWithDefaultParam() {
            val testUri = "$baseUri?nonNullWithDefaultParam=$input"
            val uriData: CharTestUriData = CharTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getNonNullWithDefaultParam()
            )
        }

        @Test
        fun getNullableWithDefaultParam() {
            val testUri = "$baseUri?nullableWithDefaultParam=$input"
            val uriData: CharTestUriData = CharTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getNullableWithDefaultParam()
            )
        }

        @Test
        fun getNullableArrayParam_OneParam() {
            val testUri = "$baseUri?nullableArrayParam=$input"
            val uriData: CharTestUriData = CharTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                arrayOf(expected),
                uriData.getNullableArrayParam(),
            )
        }

        @Test
        fun getNonNullArrayParam_OneParam() {
            val testUri = "$baseUri?nonNullArrayParam=$input"
            val uriData: CharTestUriData = CharTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                charArrayOf(expected),
                uriData.getNonNullArrayParam(),
            )
        }

        @Test
        fun getNonNullWithDefaultArrayParam_OneParam() {
            val testUri = "$baseUri?nonNullWithDefaultArrayParam=$input"
            val uriData: CharTestUriData = CharTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                charArrayOf(expected),
                uriData.getNonNullWithDefaultArrayParam(),
            )
        }

        @Test
        fun getNullableWithDefaultArrayParam_OneParam() {
            val testUri = "$baseUri?nullableWithDefaultArrayParam=$input"
            val uriData: CharTestUriData = CharTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                arrayOf(expected),
                uriData.getNullableWithDefaultArrayParam(),
            )
        }

        companion object {
            // the testParams() provider is used by test runner to provide the parameters for the tests
            @Suppress("unused")
            @JvmStatic
            @ParameterizedRobolectricTestRunner.Parameters(name = "input = {0}, expected = {1}")
            fun testParams(): List<Array<Any>> = listOf(
                // arrayOf(input, expected),
                arrayOf("a", 'a'),
                arrayOf("Z", 'Z'),
                arrayOf("1", '1'),
                // only the first character of a longer value is taken
                arrayOf("abc", 'a'),
                // url encoded characters
                arrayOf("%20", ' '),
                arrayOf("%26", '&'),
                arrayOf("%C3%A9", 'é'),
            )
        }
    }

    @RunWith(ParameterizedRobolectricTestRunner::class)
    class CharTestUriDataNonNullArrayTest(
        private val input: List<String>,
        private val expectedArray: List<Char>,
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
            val uriData: CharTestUriData = CharTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                expectedArray.toCharArray(),
                uriData.getNonNullArrayParam(),
            )
        }

        @Test
        fun getNullableArrayParam_TwoParams() {
            val (expectedParam1, expectedParam2) = input
            val testUri = "$baseUri?nullableArrayParam=$expectedParam1&nullableArrayParam=$expectedParam2"
            val uriData: CharTestUriData = CharTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                expectedArray.toTypedArray(),
                uriData.getNullableArrayParam(),
            )
        }

        @Test
        fun getNonNullWithDefaultArrayParam_TwoParams() {
            val (expectedParam1, expectedParam2) = input
            val testUri = "$baseUri?nonNullWithDefaultArrayParam=$expectedParam1&nonNullWithDefaultArrayParam=$expectedParam2"
            val uriData: CharTestUriData = CharTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                expectedArray.toCharArray(),
                uriData.getNonNullWithDefaultArrayParam(),
            )
        }

        @Test
        fun getNullableWithDefaultArrayParam_TwoParams() {
            val (expectedParam1, expectedParam2) = input
            val testUri = "$baseUri?nullableWithDefaultArrayParam=$expectedParam1&nullableWithDefaultArrayParam=$expectedParam2"
            val uriData: CharTestUriData = CharTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                expectedArray.toTypedArray(),
                uriData.getNullableWithDefaultArrayParam(),
            )
        }

        companion object {
            // the testParams() provider is used by test runner to provide the parameters for the tests
            @Suppress("unused")
            @JvmStatic
            @ParameterizedRobolectricTestRunner.Parameters(name = "input = {0}, expectedArray = {1}")
            fun testParams() = listOf(
                // arrayOf(input, expectedArray)
                arrayOf(listOf("a", "b"), listOf('a', 'b')),
                arrayOf(listOf("b", "a"), listOf('b', 'a')),
                arrayOf(listOf("xyz", "%26"), listOf('x', '&')),
            )
        }
    }

    /**
     * An empty value can't be converted to [Char], so it falls back to `'0'` for non-null getters,
     * to `null` for nullable getters and to the [boringyuri.api.DefaultValue] when it is specified.
     */
    @RunWith(RobolectricTestRunner::class)
    class CharTestUriDataEmptyValueTest {
        private lateinit var baseUri: String

        @Before
        fun setUp() {
            baseUri = "https://example.com/path/segment"
        }

        @Test
        fun getNullableParam_EmptyValue_ReturnsNull() {
            val testUri = "$baseUri?nullableParam="
            val uriData: CharTestUriData = CharTestUriDataImpl(Uri.parse(testUri))

            assertNull(uriData.getNullableParam())
        }

        @Test
        fun getNonNullParam_EmptyValue_ReturnsZeroChar() {
            val testUri = "$baseUri?nonNullParam="
            val uriData: CharTestUriData = CharTestUriDataImpl(Uri.parse(testUri))

            assertEquals('0', uriData.getNonNullParam())
        }

        @Test
        fun getNonNullWithDefaultParam_EmptyValue_ReturnsDefault() {
            val testUri = "$baseUri?nonNullWithDefaultParam="
            val uriData: CharTestUriData = CharTestUriDataImpl(Uri.parse(testUri))

            assertEquals('c', uriData.getNonNullWithDefaultParam())
        }

        @Test
        fun getNullableWithDefaultParam_EmptyValue_ReturnsDefault() {
            val testUri = "$baseUri?nullableWithDefaultParam="
            val uriData: CharTestUriData = CharTestUriDataImpl(Uri.parse(testUri))

            assertEquals('c', uriData.getNullableWithDefaultParam())
        }

        @Test
        fun getNullableArrayParam_ValidAndEmptyValues_EmptyElementIsNull() {
            val testUri = "$baseUri?nullableArrayParam=a&nullableArrayParam="
            val uriData: CharTestUriData = CharTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                arrayOf('a', null),
                uriData.getNullableArrayParam(),
            )
        }

        @Test
        fun getNonNullArrayParam_ValidAndEmptyValues_EmptyElementIsZeroChar() {
            val testUri = "$baseUri?nonNullArrayParam=a&nonNullArrayParam="
            val uriData: CharTestUriData = CharTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                charArrayOf('a', '0'),
                uriData.getNonNullArrayParam(),
            )
        }

        @Test
        fun getNonNullWithDefaultArrayParam_ValidAndEmptyValues_EmptyElementIsDefault() {
            val testUri = "$baseUri?nonNullWithDefaultArrayParam=a&nonNullWithDefaultArrayParam="
            val uriData: CharTestUriData = CharTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                charArrayOf('a', 'c'),
                uriData.getNonNullWithDefaultArrayParam(),
            )
        }

        @Test
        fun getNullableWithDefaultArrayParam_ValidAndEmptyValues_EmptyElementIsDefault() {
            val testUri = "$baseUri?nullableWithDefaultArrayParam=a&nullableWithDefaultArrayParam="
            val uriData: CharTestUriData = CharTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                arrayOf('a', 'c'),
                uriData.getNullableWithDefaultArrayParam(),
            )
        }
    }

    @RunWith(RobolectricTestRunner::class)
    class CharTestUriDataNullableOrDefaultTest {
        private lateinit var baseUri: String

        @Before
        fun setUp() {
            baseUri = "https://example.com/path/segment"
        }

        @Test(expected = NullPointerException::class)
        fun getCharSegment_SegmentIsMissing_ThrowException() {
            val testUri = baseUri
            val uriData: CharTestUriData = CharTestUriDataImpl(Uri.parse(testUri))

            uriData.getCharSegment()
        }

        @Test(expected = NullPointerException::class)
        fun getCharWithDefaultSegment_SegmentIsMissing_ThrowException() {
            val testUri = "$baseUri/x"
            val uriData: CharTestUriData = CharTestUriDataImpl(Uri.parse(testUri))

            uriData.getCharWithDefaultSegment()
        }

        @Test(expected = NullPointerException::class)
        fun getNonNullParam_ParamIsMissing_ThrowException() {
            val testUri = baseUri
            val uriData: CharTestUriData = CharTestUriDataImpl(Uri.parse(testUri))

            uriData.getNonNullParam()
        }

        @Test(expected = NullPointerException::class)
        fun getNonNullArrayParam_ParamIsMissing_ThrowException() {
            val testUri = baseUri
            val uriData: CharTestUriData = CharTestUriDataImpl(Uri.parse(testUri))

            uriData.getNonNullArrayParam()
        }

        @Test
        fun getNullableParam() {
            val testUri = baseUri
            val uriData: CharTestUriData = CharTestUriDataImpl(Uri.parse(testUri))

            assertNull(uriData.getNullableParam())
        }

        @Test
        fun getNonNullWithDefaultParam() {
            val testUri = baseUri
            val uriData: CharTestUriData = CharTestUriDataImpl(Uri.parse(testUri))

            assertEquals('c', uriData.getNonNullWithDefaultParam())
        }

        @Test
        fun getNullableWithDefaultParam() {
            val testUri = baseUri
            val uriData: CharTestUriData = CharTestUriDataImpl(Uri.parse(testUri))

            assertEquals('c', uriData.getNullableWithDefaultParam())
        }

        @Test
        fun getNullableArrayParam() {
            val testUri = baseUri
            val uriData: CharTestUriData = CharTestUriDataImpl(Uri.parse(testUri))

            assertNull(uriData.getNullableArrayParam())
        }

        @Test
        fun getNonNullWithDefaultArrayParam() {
            val testUri = baseUri
            val uriData: CharTestUriData = CharTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                charArrayOf('c'),
                uriData.getNonNullWithDefaultArrayParam(),
            )
        }

        @Test
        fun getNullableWithDefaultArrayParam() {
            val testUri = baseUri
            val uriData: CharTestUriData = CharTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                arrayOf('c'),
                uriData.getNullableWithDefaultArrayParam(),
            )
        }

        @Test
        fun toString_ReturnsCorrectUri() {
            val testUri = "$baseUri/a/b?nonNullParam=c&nullableParam=d"
            val uriData: CharTestUriData = CharTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                testUri,
                uriData.toString()
            )
        }
    }
}
