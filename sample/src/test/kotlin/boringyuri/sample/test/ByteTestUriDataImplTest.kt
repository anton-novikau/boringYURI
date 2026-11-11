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
class ByteTestUriDataImplTest {
    @RunWith(ParameterizedRobolectricTestRunner::class)
    class ByteTestUriDataNonNullTest(
        private val input: Any,
        private val expected: Byte,
    ) {

        private lateinit var baseUri: String

        @Before
        fun setUp() {
            baseUri = "https://example.com/path/segment"
        }

        @Test
        fun getByteSegment() {
            val testUri = "$baseUri/$input"
            val uriData = ByteTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getByteSegment()
            )
        }

        @Test
        fun getByteWithDefaultSegment() {
            val testUri = "$baseUri/0/$input"
            val uriData: ByteTestUriData = ByteTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getByteWithDefaultSegment()
            )
        }

        @Test
        fun getNullableParam() {
            val testUri = "$baseUri?nullableParam=$input"
            val uriData: ByteTestUriData = ByteTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getNullableParam()
            )
        }

        @Test
        fun getNonNullParam() {
            val testUri = "$baseUri?nonNullParam=$input"
            val uriData: ByteTestUriData = ByteTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getNonNullParam()
            )
        }

        @Test
        fun getNonNullWithDefaultParam() {
            val testUri = "$baseUri?nonNullWithDefaultParam=$input"
            val uriData: ByteTestUriData = ByteTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getNonNullWithDefaultParam()
            )
        }

        @Test
        fun getNullableWithDefaultParam() {
            val testUri = "$baseUri?nullableWithDefaultParam=$input"
            val uriData: ByteTestUriData = ByteTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getNullableWithDefaultParam()
            )
        }

        @Test
        fun getNullableArrayParam_OneParam() {
            val testUri = "$baseUri?nullableArrayParam=$input"
            val uriData: ByteTestUriData = ByteTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                arrayOf(expected),
                uriData.getNullableArrayParam(),
            )
        }

        @Test
        fun getNonNullArrayParam_OneParam() {
            val testUri = "$baseUri?nonNullArrayParam=$input"
            val uriData: ByteTestUriData = ByteTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                byteArrayOf(expected),
                uriData.getNonNullArrayParam(),
            )
        }

        @Test
        fun getNonNullWithDefaultArrayParam_OneParam() {
            val testUri = "$baseUri?nonNullWithDefaultArrayParam=$input"
            val uriData: ByteTestUriData = ByteTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                byteArrayOf(expected),
                uriData.getNonNullWithDefaultArrayParam(),
            )
        }

        @Test
        fun getNullableWithDefaultArrayParam_OneParam() {
            val testUri = "$baseUri?nullableWithDefaultArrayParam=$input"
            val uriData: ByteTestUriData = ByteTestUriDataImpl(Uri.parse(testUri))

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
                arrayOf(100, 100.toByte()),
                arrayOf(-100, (-100).toByte()),
                arrayOf(0, 0.toByte()),
                arrayOf(Byte.MAX_VALUE, Byte.MAX_VALUE),
                arrayOf(Byte.MIN_VALUE, Byte.MIN_VALUE),
            )
        }
    }

    @RunWith(ParameterizedRobolectricTestRunner::class)
    class ByteTestUriDataNonNullArrayTest(
        private val input: List<Any>,
        private val expectedArray: List<Byte>,
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
            val uriData: ByteTestUriData = ByteTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                expectedArray.toByteArray(),
                uriData.getNonNullArrayParam(),
            )
        }

        @Test
        fun getNullableArrayParam_TwoParams() {
            val (expectedParam1, expectedParam2) = input
            val testUri = "$baseUri?nullableArrayParam=$expectedParam1&nullableArrayParam=$expectedParam2"
            val uriData: ByteTestUriData = ByteTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                expectedArray.toTypedArray(),
                uriData.getNullableArrayParam(),
            )
        }

        @Test
        fun getNonNullWithDefaultArrayParam_TwoParams() {
            val (expectedParam1, expectedParam2) = input
            val testUri = "$baseUri?nonNullWithDefaultArrayParam=$expectedParam1&nonNullWithDefaultArrayParam=$expectedParam2"
            val uriData: ByteTestUriData = ByteTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                expectedArray.toByteArray(),
                uriData.getNonNullWithDefaultArrayParam(),
            )
        }

        @Test
        fun getNullableWithDefaultArrayParam_TwoParams() {
            val (expectedParam1, expectedParam2) = input
            val testUri = "$baseUri?nullableWithDefaultArrayParam=$expectedParam1&nullableWithDefaultArrayParam=$expectedParam2"
            val uriData: ByteTestUriData = ByteTestUriDataImpl(Uri.parse(testUri))

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
                arrayOf(listOf(1, -100), listOf<Byte>(1, -100)),
                arrayOf(listOf(-100, 1), listOf<Byte>(-100, 1)),
                arrayOf(listOf(Byte.MAX_VALUE, Byte.MIN_VALUE), listOf(Byte.MAX_VALUE, Byte.MIN_VALUE)),
            )
        }
    }

    /**
     * Values that can't be parsed to [Byte] fall back to `0` for non-null getters, to `null`
     * for nullable getters and to the [boringyuri.api.DefaultValue] when it is specified.
     */
    @RunWith(ParameterizedRobolectricTestRunner::class)
    class ByteTestUriDataInvalidValueTest(
        private val input: Any,
    ) {
        private lateinit var baseUri: String

        @Before
        fun setUp() {
            baseUri = "https://example.com/path/segment"
        }

        @Test
        fun getByteSegment_InvalidValue_ReturnsZero() {
            val testUri = "$baseUri/$input"
            val uriData: ByteTestUriData = ByteTestUriDataImpl(Uri.parse(testUri))

            assertEquals(0.toByte(), uriData.getByteSegment())
        }

        @Test
        fun getByteWithDefaultSegment_InvalidValue_ReturnsDefault() {
            val testUri = "$baseUri/0/$input"
            val uriData: ByteTestUriData = ByteTestUriDataImpl(Uri.parse(testUri))

            assertEquals(1.toByte(), uriData.getByteWithDefaultSegment())
        }

        @Test
        fun getNullableParam_InvalidValue_ReturnsNull() {
            val testUri = "$baseUri?nullableParam=$input"
            val uriData: ByteTestUriData = ByteTestUriDataImpl(Uri.parse(testUri))

            assertNull(uriData.getNullableParam())
        }

        @Test
        fun getNonNullParam_InvalidValue_ReturnsZero() {
            val testUri = "$baseUri?nonNullParam=$input"
            val uriData: ByteTestUriData = ByteTestUriDataImpl(Uri.parse(testUri))

            assertEquals(0.toByte(), uriData.getNonNullParam())
        }

        @Test
        fun getNonNullWithDefaultParam_InvalidValue_ReturnsDefault() {
            val testUri = "$baseUri?nonNullWithDefaultParam=$input"
            val uriData: ByteTestUriData = ByteTestUriDataImpl(Uri.parse(testUri))

            assertEquals(1.toByte(), uriData.getNonNullWithDefaultParam())
        }

        @Test
        fun getNullableWithDefaultParam_InvalidValue_ReturnsDefault() {
            val testUri = "$baseUri?nullableWithDefaultParam=$input"
            val uriData: ByteTestUriData = ByteTestUriDataImpl(Uri.parse(testUri))

            assertEquals(1.toByte(), uriData.getNullableWithDefaultParam())
        }

        @Test
        fun getNullableArrayParam_ValidAndInvalidValues_InvalidElementIsNull() {
            val testUri = "$baseUri?nullableArrayParam=42&nullableArrayParam=$input"
            val uriData: ByteTestUriData = ByteTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                arrayOf(42.toByte(), null),
                uriData.getNullableArrayParam(),
            )
        }

        @Test
        fun getNonNullArrayParam_ValidAndInvalidValues_InvalidElementIsZero() {
            val testUri = "$baseUri?nonNullArrayParam=42&nonNullArrayParam=$input"
            val uriData: ByteTestUriData = ByteTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                byteArrayOf(42, 0),
                uriData.getNonNullArrayParam(),
            )
        }

        @Test
        fun getNonNullWithDefaultArrayParam_ValidAndInvalidValues_InvalidElementIsDefault() {
            val testUri = "$baseUri?nonNullWithDefaultArrayParam=42&nonNullWithDefaultArrayParam=$input"
            val uriData: ByteTestUriData = ByteTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                byteArrayOf(42, 1),
                uriData.getNonNullWithDefaultArrayParam(),
            )
        }

        @Test
        fun getNullableWithDefaultArrayParam_ValidAndInvalidValues_InvalidElementIsDefault() {
            val testUri = "$baseUri?nullableWithDefaultArrayParam=42&nullableWithDefaultArrayParam=$input"
            val uriData: ByteTestUriData = ByteTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                arrayOf(42.toByte(), 1.toByte()),
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
                arrayOf(Byte.MAX_VALUE + 1),
                arrayOf(Byte.MIN_VALUE - 1),
            )
        }
    }

    @RunWith(RobolectricTestRunner::class)
    class ByteTestUriDataNullableOrDefaultTest {
        private lateinit var baseUri: String

        @Before
        fun setUp() {
            baseUri = "https://example.com/path/segment"
        }

        @Test(expected = NullPointerException::class)
        fun getByteSegment_SegmentIsMissing_ThrowException() {
            val testUri = baseUri
            val uriData: ByteTestUriData = ByteTestUriDataImpl(Uri.parse(testUri))

            uriData.getByteSegment()
        }

        @Test(expected = NullPointerException::class)
        fun getByteWithDefaultSegment_SegmentIsMissing_ThrowException() {
            val testUri = "$baseUri/0"
            val uriData: ByteTestUriData = ByteTestUriDataImpl(Uri.parse(testUri))

            uriData.getByteWithDefaultSegment()
        }

        @Test(expected = NullPointerException::class)
        fun getNonNullParam_ParamIsMissing_ThrowException() {
            val testUri = baseUri
            val uriData: ByteTestUriData = ByteTestUriDataImpl(Uri.parse(testUri))

            uriData.getNonNullParam()
        }

        @Test(expected = NullPointerException::class)
        fun getNonNullArrayParam_ParamIsMissing_ThrowException() {
            val testUri = baseUri
            val uriData: ByteTestUriData = ByteTestUriDataImpl(Uri.parse(testUri))

            uriData.getNonNullArrayParam()
        }

        @Test
        fun getNullableParam() {
            val testUri = baseUri
            val uriData: ByteTestUriData = ByteTestUriDataImpl(Uri.parse(testUri))

            assertNull(uriData.getNullableParam())
        }

        @Test
        fun getNonNullWithDefaultParam() {
            val testUri = baseUri
            val uriData: ByteTestUriData = ByteTestUriDataImpl(Uri.parse(testUri))

            assertEquals(1.toByte(), uriData.getNonNullWithDefaultParam())
        }

        @Test
        fun getNullableWithDefaultParam() {
            val testUri = baseUri
            val uriData: ByteTestUriData = ByteTestUriDataImpl(Uri.parse(testUri))

            assertEquals(1.toByte(), uriData.getNullableWithDefaultParam())
        }

        @Test
        fun getNullableArrayParam() {
            val testUri = baseUri
            val uriData: ByteTestUriData = ByteTestUriDataImpl(Uri.parse(testUri))

            assertNull(uriData.getNullableArrayParam())
        }

        @Test
        fun getNonNullWithDefaultArrayParam() {
            val testUri = baseUri
            val uriData: ByteTestUriData = ByteTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                byteArrayOf(1),
                uriData.getNonNullWithDefaultArrayParam(),
            )
        }

        @Test
        fun getNonNullWithDefaultArrayParam_OneInvalidParam() {
            val testUri = "$baseUri?nonNullWithDefaultArrayParam=invalid"
            val uriData: ByteTestUriData = ByteTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                byteArrayOf(1),
                uriData.getNonNullWithDefaultArrayParam(),
            )
        }

        @Test
        fun getNonNullWithDefaultArrayParam_TwoInvalidParams() {
            val testUri = "$baseUri?nonNullWithDefaultArrayParam=invalid&nonNullWithDefaultArrayParam=invalid"
            val uriData: ByteTestUriData = ByteTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                byteArrayOf(1, 1),
                uriData.getNonNullWithDefaultArrayParam(),
            )
        }

        @Test
        fun getNullableWithDefaultArrayParam() {
            val testUri = baseUri
            val uriData: ByteTestUriData = ByteTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                arrayOf(1.toByte()),
                uriData.getNullableWithDefaultArrayParam(),
            )
        }

        @Test
        fun getNullableWithDefaultArrayParam_OneInvalidParam() {
            val testUri = "$baseUri?nullableWithDefaultArrayParam=invalid"
            val uriData: ByteTestUriData = ByteTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                arrayOf(1.toByte()),
                uriData.getNullableWithDefaultArrayParam(),
            )
        }

        @Test
        fun getNullableWithDefaultArrayParam_TwoInvalidParams() {
            val testUri = "$baseUri?nullableWithDefaultArrayParam=invalid&nullableWithDefaultArrayParam=invalid"
            val uriData: ByteTestUriData = ByteTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                arrayOf(1.toByte(), 1.toByte()),
                uriData.getNullableWithDefaultArrayParam(),
            )
        }

        @Test
        fun toString_ReturnsCorrectUri() {
            val testUri = "$baseUri/0/1?nonNullParam=2&nullableParam=3"
            val uriData: ByteTestUriData = ByteTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                testUri,
                uriData.toString()
            )
        }
    }
}
