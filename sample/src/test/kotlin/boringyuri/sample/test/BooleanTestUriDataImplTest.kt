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
class BooleanTestUriDataImplTest {
    @RunWith(ParameterizedRobolectricTestRunner::class)
    class BooleanTestUriDataNonNullTest(
        private val input: Any,
        private val expected: Any,
    ) {

        private lateinit var baseUri: String

        @Before
        fun setUp() {
            baseUri = "https://example.com/path/segment"
        }

        @Test
        fun getBoolSegment() {
            val testUri = "$baseUri/$input"
            val uriData = BooleanTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getBoolSegment()
            )
        }

        @Test
        fun getBoolWithDefaultSegment() {
            val testUri = "$baseUri/false/$input"
            val uriData: BooleanTestUriData = BooleanTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getBoolWithDefaultSegment()
            )
        }

        @Test
        fun getNullableParam() {
            val testUri = "$baseUri?nullableParam=$input"
            val uriData: BooleanTestUriData = BooleanTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getNullableParam()
            )
        }

        @Test
        fun getNonNullParam() {
            val testUri = "$baseUri?nonNullParam=$input"
            val uriData: BooleanTestUriData = BooleanTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getNonNullParam()
            )
        }

        @Test
        fun getNonNullWithDefaultParam() {
            val testUri = "$baseUri?nonNullWithDefaultParam=$input"
            val uriData: BooleanTestUriData = BooleanTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getNonNullWithDefaultParam()
            )
        }

        @Test
        fun getNullableWithDefaultParam() {
            val testUri = "$baseUri?nullableWithDefaultParam=$input"
            val uriData: BooleanTestUriData = BooleanTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getNullableWithDefaultParam()
            )
        }

        @Test
        fun getNullableArrayParam_OneParam() {
            val testUri = "$baseUri?nullableArrayParam=$input"
            val uriData: BooleanTestUriData = BooleanTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                arrayOf(expected),
                uriData.getNullableArrayParam(),

            )
        }

        @Test
        fun getNonNullArrayParam_OneParam() {
            val testUri = "$baseUri?nonNullArrayParam=$input"
            val uriData: BooleanTestUriData = BooleanTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                booleanArrayOf(expected as Boolean),
                uriData.getNonNullArrayParam(),
            )
        }

        @Test
        fun getNonNullWithDefaultArrayParam_OneParam() {
            val testUri = "$baseUri?nonNullWithDefaultArrayParam=$input"
            val uriData: BooleanTestUriData = BooleanTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                booleanArrayOf(expected as Boolean),
                uriData.getNonNullWithDefaultArrayParam(),
            )
        }

        @Test
        fun getNullableWithDefaultArrayParam_OneParam() {
            val testUri = "$baseUri?nullableWithDefaultArrayParam=$input"
            val uriData: BooleanTestUriData = BooleanTestUriDataImpl(Uri.parse(testUri))

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
                arrayOf(true, true),
                arrayOf(false, false),
                arrayOf("TRUE", true),
                arrayOf("False", false),
                arrayOf(1, true),
                arrayOf(0, false),
                arrayOf(-1, false),
                arrayOf(2, false),
                // any value which is neither "true" (case-insensitive) nor "1" is parsed as false,
                // so getters with @DefaultValue("true") return false as well instead of the default
                arrayOf("invalid", false),
            )
        }
    }

    @RunWith(ParameterizedRobolectricTestRunner::class)
    class BooleanTestUriDataNonNullArrayTest(
        private val input: List<Any>,
        private val expectedArray: List<Boolean>,
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
            val uriData: BooleanTestUriData = BooleanTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                expectedArray.toBooleanArray(),
                uriData.getNonNullArrayParam(),
                )
        }

        @Test
        fun getNullableArrayParam_TwoParams() {
            val (expectedParam1, expectedParam2) = input
            val testUri = "$baseUri?nullableArrayParam=$expectedParam1&nullableArrayParam=$expectedParam2"
            val uriData: BooleanTestUriData = BooleanTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                expectedArray.toTypedArray(),
                uriData.getNullableArrayParam(),
            )
        }

        @Test
        fun getNonNullWithDefaultArrayParam_TwoParams() {
            val (expectedParam1, expectedParam2) = input
            val testUri = "$baseUri?nonNullWithDefaultArrayParam=$expectedParam1&nonNullWithDefaultArrayParam=$expectedParam2"
            val uriData: BooleanTestUriData = BooleanTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                expectedArray.toBooleanArray(),
                uriData.getNonNullWithDefaultArrayParam(),
            )
        }

        @Test
        fun getNullableWithDefaultArrayParam_TwoParams() {
            val (expectedParam1, expectedParam2) = input
            val testUri = "$baseUri?nullableWithDefaultArrayParam=$expectedParam1&nullableWithDefaultArrayParam=$expectedParam2"
            val uriData: BooleanTestUriData = BooleanTestUriDataImpl(Uri.parse(testUri))

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
                arrayOf(listOf(true, true), listOf(true, true)),
                arrayOf(listOf(false, false), listOf(false, false)),
                arrayOf(listOf(true, false), listOf(true, false)),
                arrayOf(listOf(false, true), listOf(false, true)),
                arrayOf(listOf(1, 1), listOf(true, true)),
                arrayOf(listOf(0, 0), listOf(false, false)),
                arrayOf(listOf(1, 0), listOf(true, false)),
                arrayOf(listOf(0, 1), listOf(false, true)),
                arrayOf(listOf(2, -1), listOf(false, false)),
                arrayOf(listOf("invalid", 1), listOf(false, true)),
                arrayOf(listOf(1, "invalid"), listOf(true, false)),
            )
        }
    }

    @RunWith(RobolectricTestRunner::class)
    class BooleanTestUriDataNullableOrDefaultTest {
        private lateinit var baseUri: String

        @Before
        fun setUp() {
            baseUri = "https://example.com/path/segment"
        }

        @Test(expected = NullPointerException::class)
        fun getBoolSegment_SegmentIsMissing_ThrowException() {
            val testUri = baseUri
            val uriData: BooleanTestUriData = BooleanTestUriDataImpl(Uri.parse(testUri))

            uriData.getBoolSegment()
        }

        @Test(expected = NullPointerException::class)
        fun getBoolWithDefaultSegment_SegmentIsMissing_ThrowException() {
            val testUri = "$baseUri/false"
            val uriData: BooleanTestUriData = BooleanTestUriDataImpl(Uri.parse(testUri))

            uriData.getBoolWithDefaultSegment()
        }

        @Test(expected = NullPointerException::class)
        fun getNonNullParam_ParamIsMissing_ThrowException() {
            val testUri = baseUri
            val uriData: BooleanTestUriData = BooleanTestUriDataImpl(Uri.parse(testUri))

            uriData.getNonNullParam()
        }

        @Test(expected = NullPointerException::class)
        fun getNonNullArrayParam_ParamIsMissing_ThrowException() {
            val testUri = baseUri
            val uriData: BooleanTestUriData = BooleanTestUriDataImpl(Uri.parse(testUri))

            uriData.getNonNullArrayParam()
        }

        @Test
        fun getNullableParam() {
            val testUri = baseUri
            val uriData: BooleanTestUriData = BooleanTestUriDataImpl(Uri.parse(testUri))

            assertNull(uriData.getNullableParam())
        }

        @Test
        fun getNonNullWithDefaultParam() {
            val testUri = baseUri
            val uriData: BooleanTestUriData = BooleanTestUriDataImpl(Uri.parse(testUri))

            assertEquals(true, uriData.getNonNullWithDefaultParam())
        }

        @Test
        fun getNullableWithDefaultParam() {
            val testUri = baseUri
            val uriData: BooleanTestUriData = BooleanTestUriDataImpl(Uri.parse(testUri))

            assertEquals(true, uriData.getNullableWithDefaultParam())
        }

        @Test
        fun getNullableArrayParam() {
            val testUri = baseUri
            val uriData: BooleanTestUriData = BooleanTestUriDataImpl(Uri.parse(testUri))

            assertNull(uriData.getNullableArrayParam())
        }

        @Test
        fun getNonNullWithDefaultArrayParam() {
            val testUri = baseUri
            val uriData: BooleanTestUriData = BooleanTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                booleanArrayOf(true),
                uriData.getNonNullWithDefaultArrayParam(),
            )
        }

        @Test
        fun getNullableWithDefaultArrayParam() {
            val testUri = baseUri
            val uriData: BooleanTestUriData = BooleanTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                arrayOf(true),
                uriData.getNullableWithDefaultArrayParam(),
            )
        }

        @Test
        fun toString_ReturnsCorrectUri() {
            val testUri = "$baseUri/false/true?nonNullParam=false&nullableParam=true"
            val uriData: BooleanTestUriData = BooleanTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                testUri,
                uriData.toString()
            )
        }
    }
}
