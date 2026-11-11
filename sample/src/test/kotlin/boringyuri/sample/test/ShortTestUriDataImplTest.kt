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
class ShortTestUriDataImplTest {
    @RunWith(ParameterizedRobolectricTestRunner::class)
    class ShortTestUriDataNonNullTest(
        private val input: Any,
        private val expected: Short,
    ) {

        private lateinit var baseUri: String

        @Before
        fun setUp() {
            baseUri = "https://example.com/path/segment"
        }

        @Test
        fun getShortSegment() {
            val testUri = "$baseUri/$input"
            val uriData = ShortTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getShortSegment()
            )
        }

        @Test
        fun getShortWithDefaultSegment() {
            val testUri = "$baseUri/0/$input"
            val uriData: ShortTestUriData = ShortTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getShortWithDefaultSegment()
            )
        }

        @Test
        fun getNullableParam() {
            val testUri = "$baseUri?nullableParam=$input"
            val uriData: ShortTestUriData = ShortTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getNullableParam()
            )
        }

        @Test
        fun getNonNullParam() {
            val testUri = "$baseUri?nonNullParam=$input"
            val uriData: ShortTestUriData = ShortTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getNonNullParam()
            )
        }

        @Test
        fun getNonNullWithDefaultParam() {
            val testUri = "$baseUri?nonNullWithDefaultParam=$input"
            val uriData: ShortTestUriData = ShortTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getNonNullWithDefaultParam()
            )
        }

        @Test
        fun getNullableWithDefaultParam() {
            val testUri = "$baseUri?nullableWithDefaultParam=$input"
            val uriData: ShortTestUriData = ShortTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getNullableWithDefaultParam()
            )
        }

        @Test
        fun getNullableArrayParam_OneParam() {
            val testUri = "$baseUri?nullableArrayParam=$input"
            val uriData: ShortTestUriData = ShortTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                arrayOf(expected),
                uriData.getNullableArrayParam(),
            )
        }

        @Test
        fun getNonNullArrayParam_OneParam() {
            val testUri = "$baseUri?nonNullArrayParam=$input"
            val uriData: ShortTestUriData = ShortTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                shortArrayOf(expected),
                uriData.getNonNullArrayParam(),
            )
        }

        @Test
        fun getNonNullWithDefaultArrayParam_OneParam() {
            val testUri = "$baseUri?nonNullWithDefaultArrayParam=$input"
            val uriData: ShortTestUriData = ShortTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                shortArrayOf(expected),
                uriData.getNonNullWithDefaultArrayParam(),
            )
        }

        @Test
        fun getNullableWithDefaultArrayParam_OneParam() {
            val testUri = "$baseUri?nullableWithDefaultArrayParam=$input"
            val uriData: ShortTestUriData = ShortTestUriDataImpl(Uri.parse(testUri))

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
                arrayOf(100, 100.toShort()),
                arrayOf(-100, (-100).toShort()),
                arrayOf(0, 0.toShort()),
                arrayOf(Short.MAX_VALUE, Short.MAX_VALUE),
                arrayOf(Short.MIN_VALUE, Short.MIN_VALUE),
            )
        }
    }

    @RunWith(ParameterizedRobolectricTestRunner::class)
    class ShortTestUriDataNonNullArrayTest(
        private val input: List<Any>,
        private val expectedArray: List<Short>,
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
            val uriData: ShortTestUriData = ShortTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                expectedArray.toShortArray(),
                uriData.getNonNullArrayParam(),
            )
        }

        @Test
        fun getNullableArrayParam_TwoParams() {
            val (expectedParam1, expectedParam2) = input
            val testUri = "$baseUri?nullableArrayParam=$expectedParam1&nullableArrayParam=$expectedParam2"
            val uriData: ShortTestUriData = ShortTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                expectedArray.toTypedArray(),
                uriData.getNullableArrayParam(),
            )
        }

        @Test
        fun getNonNullWithDefaultArrayParam_TwoParams() {
            val (expectedParam1, expectedParam2) = input
            val testUri = "$baseUri?nonNullWithDefaultArrayParam=$expectedParam1&nonNullWithDefaultArrayParam=$expectedParam2"
            val uriData: ShortTestUriData = ShortTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                expectedArray.toShortArray(),
                uriData.getNonNullWithDefaultArrayParam(),
            )
        }

        @Test
        fun getNullableWithDefaultArrayParam_TwoParams() {
            val (expectedParam1, expectedParam2) = input
            val testUri = "$baseUri?nullableWithDefaultArrayParam=$expectedParam1&nullableWithDefaultArrayParam=$expectedParam2"
            val uriData: ShortTestUriData = ShortTestUriDataImpl(Uri.parse(testUri))

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
                arrayOf(listOf(1, -100), listOf<Short>(1, -100)),
                arrayOf(listOf(-100, 1), listOf<Short>(-100, 1)),
                arrayOf(listOf(Short.MAX_VALUE, Short.MIN_VALUE), listOf(Short.MAX_VALUE, Short.MIN_VALUE)),
            )
        }
    }

    /**
     * Values that can't be parsed to [Short] fall back to `0` for non-null getters, to `null`
     * for nullable getters and to the [boringyuri.api.DefaultValue] when it is specified.
     */
    @RunWith(ParameterizedRobolectricTestRunner::class)
    class ShortTestUriDataInvalidValueTest(
        private val input: Any,
    ) {
        private lateinit var baseUri: String

        @Before
        fun setUp() {
            baseUri = "https://example.com/path/segment"
        }

        @Test
        fun getShortSegment_InvalidValue_ReturnsZero() {
            val testUri = "$baseUri/$input"
            val uriData: ShortTestUriData = ShortTestUriDataImpl(Uri.parse(testUri))

            assertEquals(0.toShort(), uriData.getShortSegment())
        }

        @Test
        fun getShortWithDefaultSegment_InvalidValue_ReturnsDefault() {
            val testUri = "$baseUri/0/$input"
            val uriData: ShortTestUriData = ShortTestUriDataImpl(Uri.parse(testUri))

            assertEquals(1.toShort(), uriData.getShortWithDefaultSegment())
        }

        @Test
        fun getNullableParam_InvalidValue_ReturnsNull() {
            val testUri = "$baseUri?nullableParam=$input"
            val uriData: ShortTestUriData = ShortTestUriDataImpl(Uri.parse(testUri))

            assertNull(uriData.getNullableParam())
        }

        @Test
        fun getNonNullParam_InvalidValue_ReturnsZero() {
            val testUri = "$baseUri?nonNullParam=$input"
            val uriData: ShortTestUriData = ShortTestUriDataImpl(Uri.parse(testUri))

            assertEquals(0.toShort(), uriData.getNonNullParam())
        }

        @Test
        fun getNonNullWithDefaultParam_InvalidValue_ReturnsDefault() {
            val testUri = "$baseUri?nonNullWithDefaultParam=$input"
            val uriData: ShortTestUriData = ShortTestUriDataImpl(Uri.parse(testUri))

            assertEquals(1.toShort(), uriData.getNonNullWithDefaultParam())
        }

        @Test
        fun getNullableWithDefaultParam_InvalidValue_ReturnsDefault() {
            val testUri = "$baseUri?nullableWithDefaultParam=$input"
            val uriData: ShortTestUriData = ShortTestUriDataImpl(Uri.parse(testUri))

            assertEquals(1.toShort(), uriData.getNullableWithDefaultParam())
        }

        @Test
        fun getNullableArrayParam_ValidAndInvalidValues_InvalidElementIsNull() {
            val testUri = "$baseUri?nullableArrayParam=42&nullableArrayParam=$input"
            val uriData: ShortTestUriData = ShortTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                arrayOf(42.toShort(), null),
                uriData.getNullableArrayParam(),
            )
        }

        @Test
        fun getNonNullArrayParam_ValidAndInvalidValues_InvalidElementIsZero() {
            val testUri = "$baseUri?nonNullArrayParam=42&nonNullArrayParam=$input"
            val uriData: ShortTestUriData = ShortTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                shortArrayOf(42, 0),
                uriData.getNonNullArrayParam(),
            )
        }

        @Test
        fun getNonNullWithDefaultArrayParam_ValidAndInvalidValues_InvalidElementIsDefault() {
            val testUri = "$baseUri?nonNullWithDefaultArrayParam=42&nonNullWithDefaultArrayParam=$input"
            val uriData: ShortTestUriData = ShortTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                shortArrayOf(42, 1),
                uriData.getNonNullWithDefaultArrayParam(),
            )
        }

        @Test
        fun getNullableWithDefaultArrayParam_ValidAndInvalidValues_InvalidElementIsDefault() {
            val testUri = "$baseUri?nullableWithDefaultArrayParam=42&nullableWithDefaultArrayParam=$input"
            val uriData: ShortTestUriData = ShortTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                arrayOf(42.toShort(), 1.toShort()),
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
                arrayOf("1.5"),
                arrayOf(Short.MAX_VALUE + 1),
                arrayOf(Short.MIN_VALUE - 1),
            )
        }
    }

    @RunWith(RobolectricTestRunner::class)
    class ShortTestUriDataNullableOrDefaultTest {
        private lateinit var baseUri: String

        @Before
        fun setUp() {
            baseUri = "https://example.com/path/segment"
        }

        @Test(expected = NullPointerException::class)
        fun getShortSegment_SegmentIsMissing_ThrowException() {
            val testUri = baseUri
            val uriData: ShortTestUriData = ShortTestUriDataImpl(Uri.parse(testUri))

            uriData.getShortSegment()
        }

        @Test(expected = NullPointerException::class)
        fun getShortWithDefaultSegment_SegmentIsMissing_ThrowException() {
            val testUri = "$baseUri/0"
            val uriData: ShortTestUriData = ShortTestUriDataImpl(Uri.parse(testUri))

            uriData.getShortWithDefaultSegment()
        }

        @Test(expected = NullPointerException::class)
        fun getNonNullParam_ParamIsMissing_ThrowException() {
            val testUri = baseUri
            val uriData: ShortTestUriData = ShortTestUriDataImpl(Uri.parse(testUri))

            uriData.getNonNullParam()
        }

        @Test(expected = NullPointerException::class)
        fun getNonNullArrayParam_ParamIsMissing_ThrowException() {
            val testUri = baseUri
            val uriData: ShortTestUriData = ShortTestUriDataImpl(Uri.parse(testUri))

            uriData.getNonNullArrayParam()
        }

        @Test
        fun getNullableParam() {
            val testUri = baseUri
            val uriData: ShortTestUriData = ShortTestUriDataImpl(Uri.parse(testUri))

            assertNull(uriData.getNullableParam())
        }

        @Test
        fun getNonNullWithDefaultParam() {
            val testUri = baseUri
            val uriData: ShortTestUriData = ShortTestUriDataImpl(Uri.parse(testUri))

            assertEquals(1.toShort(), uriData.getNonNullWithDefaultParam())
        }

        @Test
        fun getNullableWithDefaultParam() {
            val testUri = baseUri
            val uriData: ShortTestUriData = ShortTestUriDataImpl(Uri.parse(testUri))

            assertEquals(1.toShort(), uriData.getNullableWithDefaultParam())
        }

        @Test
        fun getNullableArrayParam() {
            val testUri = baseUri
            val uriData: ShortTestUriData = ShortTestUriDataImpl(Uri.parse(testUri))

            assertNull(uriData.getNullableArrayParam())
        }

        @Test
        fun getNonNullWithDefaultArrayParam() {
            val testUri = baseUri
            val uriData: ShortTestUriData = ShortTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                shortArrayOf(1),
                uriData.getNonNullWithDefaultArrayParam(),
            )
        }

        @Test
        fun getNonNullWithDefaultArrayParam_OneInvalidParam() {
            val testUri = "$baseUri?nonNullWithDefaultArrayParam=invalid"
            val uriData: ShortTestUriData = ShortTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                shortArrayOf(1),
                uriData.getNonNullWithDefaultArrayParam(),
            )
        }

        @Test
        fun getNonNullWithDefaultArrayParam_TwoInvalidParams() {
            val testUri = "$baseUri?nonNullWithDefaultArrayParam=invalid&nonNullWithDefaultArrayParam=invalid"
            val uriData: ShortTestUriData = ShortTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                shortArrayOf(1, 1),
                uriData.getNonNullWithDefaultArrayParam(),
            )
        }

        @Test
        fun getNullableWithDefaultArrayParam() {
            val testUri = baseUri
            val uriData: ShortTestUriData = ShortTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                arrayOf(1.toShort()),
                uriData.getNullableWithDefaultArrayParam(),
            )
        }

        @Test
        fun getNullableWithDefaultArrayParam_OneInvalidParam() {
            val testUri = "$baseUri?nullableWithDefaultArrayParam=invalid"
            val uriData: ShortTestUriData = ShortTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                arrayOf(1.toShort()),
                uriData.getNullableWithDefaultArrayParam(),
            )
        }

        @Test
        fun getNullableWithDefaultArrayParam_TwoInvalidParams() {
            val testUri = "$baseUri?nullableWithDefaultArrayParam=invalid&nullableWithDefaultArrayParam=invalid"
            val uriData: ShortTestUriData = ShortTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                arrayOf(1.toShort(), 1.toShort()),
                uriData.getNullableWithDefaultArrayParam(),
            )
        }

        @Test
        fun toString_ReturnsCorrectUri() {
            val testUri = "$baseUri/0/1?nonNullParam=2&nullableParam=3"
            val uriData: ShortTestUriData = ShortTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                testUri,
                uriData.toString()
            )
        }
    }
}
