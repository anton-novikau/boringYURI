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

private const val DELTA = 0f

@RunWith(Enclosed::class)
class FloatTestUriDataImplTest {
    @RunWith(ParameterizedRobolectricTestRunner::class)
    class FloatTestUriDataNonNullTest(
        private val input: Any,
        private val expected: Float,
    ) {

        private lateinit var baseUri: String

        @Before
        fun setUp() {
            baseUri = "https://example.com/path/segment"
        }

        @Test
        fun getFloatSegment() {
            val testUri = "$baseUri/$input"
            val uriData = FloatTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getFloatSegment(),
                DELTA,
            )
        }

        @Test
        fun getFloatWithDefaultSegment() {
            val testUri = "$baseUri/0/$input"
            val uriData: FloatTestUriData = FloatTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getFloatWithDefaultSegment(),
                DELTA,
            )
        }

        @Test
        fun getNullableParam() {
            val testUri = "$baseUri?nullableParam=$input"
            val uriData: FloatTestUriData = FloatTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getNullableParam()
            )
        }

        @Test
        fun getNonNullParam() {
            val testUri = "$baseUri?nonNullParam=$input"
            val uriData: FloatTestUriData = FloatTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getNonNullParam(),
                DELTA,
            )
        }

        @Test
        fun getNonNullWithDefaultParam() {
            val testUri = "$baseUri?nonNullWithDefaultParam=$input"
            val uriData: FloatTestUriData = FloatTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getNonNullWithDefaultParam(),
                DELTA,
            )
        }

        @Test
        fun getNullableWithDefaultParam() {
            val testUri = "$baseUri?nullableWithDefaultParam=$input"
            val uriData: FloatTestUriData = FloatTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getNullableWithDefaultParam()
            )
        }

        @Test
        fun getNullableArrayParam_OneParam() {
            val testUri = "$baseUri?nullableArrayParam=$input"
            val uriData: FloatTestUriData = FloatTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                arrayOf(expected),
                uriData.getNullableArrayParam(),
            )
        }

        @Test
        fun getNonNullArrayParam_OneParam() {
            val testUri = "$baseUri?nonNullArrayParam=$input"
            val uriData: FloatTestUriData = FloatTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                floatArrayOf(expected),
                uriData.getNonNullArrayParam(),
                DELTA,
            )
        }

        @Test
        fun getNonNullWithDefaultArrayParam_OneParam() {
            val testUri = "$baseUri?nonNullWithDefaultArrayParam=$input"
            val uriData: FloatTestUriData = FloatTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                floatArrayOf(expected),
                uriData.getNonNullWithDefaultArrayParam(),
                DELTA,
            )
        }

        @Test
        fun getNullableWithDefaultArrayParam_OneParam() {
            val testUri = "$baseUri?nullableWithDefaultArrayParam=$input"
            val uriData: FloatTestUriData = FloatTestUriDataImpl(Uri.parse(testUri))

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
                arrayOf(1.5, 1.5f),
                arrayOf(-1.5, -1.5f),
                arrayOf(0, 0f),
                arrayOf(100, 100f),
                arrayOf("1e3", 1000f),
                arrayOf(Float.MAX_VALUE, Float.MAX_VALUE),
                arrayOf(Float.MIN_VALUE, Float.MIN_VALUE),
            )
        }
    }

    @RunWith(ParameterizedRobolectricTestRunner::class)
    class FloatTestUriDataNonNullArrayTest(
        private val input: List<Any>,
        private val expectedArray: List<Float>,
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
            val uriData: FloatTestUriData = FloatTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                expectedArray.toFloatArray(),
                uriData.getNonNullArrayParam(),
                DELTA,
            )
        }

        @Test
        fun getNullableArrayParam_TwoParams() {
            val (expectedParam1, expectedParam2) = input
            val testUri = "$baseUri?nullableArrayParam=$expectedParam1&nullableArrayParam=$expectedParam2"
            val uriData: FloatTestUriData = FloatTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                expectedArray.toTypedArray(),
                uriData.getNullableArrayParam(),
            )
        }

        @Test
        fun getNonNullWithDefaultArrayParam_TwoParams() {
            val (expectedParam1, expectedParam2) = input
            val testUri = "$baseUri?nonNullWithDefaultArrayParam=$expectedParam1&nonNullWithDefaultArrayParam=$expectedParam2"
            val uriData: FloatTestUriData = FloatTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                expectedArray.toFloatArray(),
                uriData.getNonNullWithDefaultArrayParam(),
                DELTA,
            )
        }

        @Test
        fun getNullableWithDefaultArrayParam_TwoParams() {
            val (expectedParam1, expectedParam2) = input
            val testUri = "$baseUri?nullableWithDefaultArrayParam=$expectedParam1&nullableWithDefaultArrayParam=$expectedParam2"
            val uriData: FloatTestUriData = FloatTestUriDataImpl(Uri.parse(testUri))

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
                arrayOf(listOf(1.5, -100), listOf(1.5f, -100f)),
                arrayOf(listOf(-100, 1.5), listOf(-100f, 1.5f)),
                arrayOf(listOf(Float.MAX_VALUE, Float.MIN_VALUE), listOf(Float.MAX_VALUE, Float.MIN_VALUE)),
            )
        }
    }

    /**
     * Values that can't be parsed to [Float] fall back to `0` for non-null getters, to `null`
     * for nullable getters and to the [boringyuri.api.DefaultValue] when it is specified.
     */
    @RunWith(ParameterizedRobolectricTestRunner::class)
    class FloatTestUriDataInvalidValueTest(
        private val input: Any,
    ) {
        private lateinit var baseUri: String

        @Before
        fun setUp() {
            baseUri = "https://example.com/path/segment"
        }

        @Test
        fun getFloatSegment_InvalidValue_ReturnsZero() {
            val testUri = "$baseUri/$input"
            val uriData: FloatTestUriData = FloatTestUriDataImpl(Uri.parse(testUri))

            assertEquals(0f, uriData.getFloatSegment(), DELTA)
        }

        @Test
        fun getFloatWithDefaultSegment_InvalidValue_ReturnsDefault() {
            val testUri = "$baseUri/0/$input"
            val uriData: FloatTestUriData = FloatTestUriDataImpl(Uri.parse(testUri))

            assertEquals(1.2f, uriData.getFloatWithDefaultSegment(), DELTA)
        }

        @Test
        fun getNullableParam_InvalidValue_ReturnsNull() {
            val testUri = "$baseUri?nullableParam=$input"
            val uriData: FloatTestUriData = FloatTestUriDataImpl(Uri.parse(testUri))

            assertNull(uriData.getNullableParam())
        }

        @Test
        fun getNonNullParam_InvalidValue_ReturnsZero() {
            val testUri = "$baseUri?nonNullParam=$input"
            val uriData: FloatTestUriData = FloatTestUriDataImpl(Uri.parse(testUri))

            assertEquals(0f, uriData.getNonNullParam(), DELTA)
        }

        @Test
        fun getNonNullWithDefaultParam_InvalidValue_ReturnsDefault() {
            val testUri = "$baseUri?nonNullWithDefaultParam=$input"
            val uriData: FloatTestUriData = FloatTestUriDataImpl(Uri.parse(testUri))

            assertEquals(1.2f, uriData.getNonNullWithDefaultParam(), DELTA)
        }

        @Test
        fun getNullableWithDefaultParam_InvalidValue_ReturnsDefault() {
            val testUri = "$baseUri?nullableWithDefaultParam=$input"
            val uriData: FloatTestUriData = FloatTestUriDataImpl(Uri.parse(testUri))

            assertEquals(1.2f, uriData.getNullableWithDefaultParam())
        }

        @Test
        fun getNullableArrayParam_ValidAndInvalidValues_InvalidElementIsNull() {
            val testUri = "$baseUri?nullableArrayParam=42&nullableArrayParam=$input"
            val uriData: FloatTestUriData = FloatTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                arrayOf(42f, null),
                uriData.getNullableArrayParam(),
            )
        }

        @Test
        fun getNonNullArrayParam_ValidAndInvalidValues_InvalidElementIsZero() {
            val testUri = "$baseUri?nonNullArrayParam=42&nonNullArrayParam=$input"
            val uriData: FloatTestUriData = FloatTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                floatArrayOf(42f, 0f),
                uriData.getNonNullArrayParam(),
                DELTA,
            )
        }

        @Test
        fun getNonNullWithDefaultArrayParam_ValidAndInvalidValues_InvalidElementIsDefault() {
            val testUri = "$baseUri?nonNullWithDefaultArrayParam=42&nonNullWithDefaultArrayParam=$input"
            val uriData: FloatTestUriData = FloatTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                floatArrayOf(42f, 1.2f),
                uriData.getNonNullWithDefaultArrayParam(),
                DELTA,
            )
        }

        @Test
        fun getNullableWithDefaultArrayParam_ValidAndInvalidValues_InvalidElementIsDefault() {
            val testUri = "$baseUri?nullableWithDefaultArrayParam=42&nullableWithDefaultArrayParam=$input"
            val uriData: FloatTestUriData = FloatTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                arrayOf(42f, 1.2f),
                uriData.getNullableWithDefaultArrayParam(),
            )
        }

        companion object {
            // the testParams() provider is used by test runner to provide the parameters for the tests
            @Suppress("unused")
            @JvmStatic
            @ParameterizedRobolectricTestRunner.Parameters(name = "input = {0}")
            fun testParams(): List<Array<Any>> = listOf(
                // arrayOf(input),
                arrayOf("invalid"),
                arrayOf("1.2.3"),
                arrayOf("1,5"),
            )
        }
    }

    @RunWith(RobolectricTestRunner::class)
    class FloatTestUriDataNullableOrDefaultTest {
        private lateinit var baseUri: String

        @Before
        fun setUp() {
            baseUri = "https://example.com/path/segment"
        }

        @Test(expected = NullPointerException::class)
        fun getFloatSegment_SegmentIsMissing_ThrowException() {
            val testUri = baseUri
            val uriData: FloatTestUriData = FloatTestUriDataImpl(Uri.parse(testUri))

            uriData.getFloatSegment()
        }

        @Test(expected = NullPointerException::class)
        fun getFloatWithDefaultSegment_SegmentIsMissing_ThrowException() {
            val testUri = "$baseUri/0"
            val uriData: FloatTestUriData = FloatTestUriDataImpl(Uri.parse(testUri))

            uriData.getFloatWithDefaultSegment()
        }

        @Test(expected = NullPointerException::class)
        fun getNonNullParam_ParamIsMissing_ThrowException() {
            val testUri = baseUri
            val uriData: FloatTestUriData = FloatTestUriDataImpl(Uri.parse(testUri))

            uriData.getNonNullParam()
        }

        @Test(expected = NullPointerException::class)
        fun getNonNullArrayParam_ParamIsMissing_ThrowException() {
            val testUri = baseUri
            val uriData: FloatTestUriData = FloatTestUriDataImpl(Uri.parse(testUri))

            uriData.getNonNullArrayParam()
        }

        @Test
        fun getNullableParam() {
            val testUri = baseUri
            val uriData: FloatTestUriData = FloatTestUriDataImpl(Uri.parse(testUri))

            assertNull(uriData.getNullableParam())
        }

        @Test
        fun getNonNullWithDefaultParam() {
            val testUri = baseUri
            val uriData: FloatTestUriData = FloatTestUriDataImpl(Uri.parse(testUri))

            assertEquals(1.2f, uriData.getNonNullWithDefaultParam(), DELTA)
        }

        @Test
        fun getNullableWithDefaultParam() {
            val testUri = baseUri
            val uriData: FloatTestUriData = FloatTestUriDataImpl(Uri.parse(testUri))

            assertEquals(1.2f, uriData.getNullableWithDefaultParam())
        }

        @Test
        fun getNullableArrayParam() {
            val testUri = baseUri
            val uriData: FloatTestUriData = FloatTestUriDataImpl(Uri.parse(testUri))

            assertNull(uriData.getNullableArrayParam())
        }

        @Test
        fun getNonNullWithDefaultArrayParam() {
            val testUri = baseUri
            val uriData: FloatTestUriData = FloatTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                floatArrayOf(1.2f),
                uriData.getNonNullWithDefaultArrayParam(),
                DELTA,
            )
        }

        @Test
        fun getNonNullWithDefaultArrayParam_OneInvalidParam() {
            val testUri = "$baseUri?nonNullWithDefaultArrayParam=invalid"
            val uriData: FloatTestUriData = FloatTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                floatArrayOf(1.2f),
                uriData.getNonNullWithDefaultArrayParam(),
                DELTA,
            )
        }

        @Test
        fun getNonNullWithDefaultArrayParam_TwoInvalidParams() {
            val testUri = "$baseUri?nonNullWithDefaultArrayParam=invalid&nonNullWithDefaultArrayParam=invalid"
            val uriData: FloatTestUriData = FloatTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                floatArrayOf(1.2f, 1.2f),
                uriData.getNonNullWithDefaultArrayParam(),
                DELTA,
            )
        }

        @Test
        fun getNullableWithDefaultArrayParam() {
            val testUri = baseUri
            val uriData: FloatTestUriData = FloatTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                arrayOf(1.2f),
                uriData.getNullableWithDefaultArrayParam(),
            )
        }

        @Test
        fun getNullableWithDefaultArrayParam_OneInvalidParam() {
            val testUri = "$baseUri?nullableWithDefaultArrayParam=invalid"
            val uriData: FloatTestUriData = FloatTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                arrayOf(1.2f),
                uriData.getNullableWithDefaultArrayParam(),
            )
        }

        @Test
        fun getNullableWithDefaultArrayParam_TwoInvalidParams() {
            val testUri = "$baseUri?nullableWithDefaultArrayParam=invalid&nullableWithDefaultArrayParam=invalid"
            val uriData: FloatTestUriData = FloatTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                arrayOf(1.2f, 1.2f),
                uriData.getNullableWithDefaultArrayParam(),
            )
        }

        @Test
        fun toString_ReturnsCorrectUri() {
            val testUri = "$baseUri/0.5/1.5?nonNullParam=2.5&nullableParam=3.5"
            val uriData: FloatTestUriData = FloatTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                testUri,
                uriData.toString()
            )
        }
    }
}
