/*
 * Copyright 2026 Anton Novikau
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

private const val DELTA = 0.0

@RunWith(Enclosed::class)
class DoubleTestUriDataImplTest {
    @RunWith(ParameterizedRobolectricTestRunner::class)
    class DoubleTestUriDataNonNullTest(
        private val input: Any,
        private val expected: Double,
    ) {

        private lateinit var baseUri: String

        @Before
        fun setUp() {
            baseUri = "https://example.com/path/segment"
        }

        @Test
        fun getDoubleSegment() {
            val testUri = "$baseUri/$input"
            val uriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getDoubleSegment(),
                DELTA,
            )
        }

        @Test
        fun getDoubleWithDefaultSegment() {
            val testUri = "$baseUri/0/$input"
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getDoubleWithDefaultSegment(),
                DELTA,
            )
        }

        @Test
        fun getNullableParam() {
            val testUri = "$baseUri?nullableParam=$input"
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getNullableParam()
            )
        }

        @Test
        fun getNonNullParam() {
            val testUri = "$baseUri?nonNullParam=$input"
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getNonNullParam(),
                DELTA,
            )
        }

        @Test
        fun getNonNullWithDefaultParam() {
            val testUri = "$baseUri?nonNullWithDefaultParam=$input"
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getNonNullWithDefaultParam(),
                DELTA,
            )
        }

        @Test
        fun getNullableWithDefaultParam() {
            val testUri = "$baseUri?nullableWithDefaultParam=$input"
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getNullableWithDefaultParam()
            )
        }

        @Test
        fun getNullableArrayParam_OneParam() {
            val testUri = "$baseUri?nullableArrayParam=$input"
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                arrayOf(expected),
                uriData.getNullableArrayParam(),
            )
        }

        @Test
        fun getNonNullArrayParam_OneParam() {
            val testUri = "$baseUri?nonNullArrayParam=$input"
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                doubleArrayOf(expected),
                uriData.getNonNullArrayParam(),
                DELTA,
            )
        }

        @Test
        fun getNonNullWithDefaultArrayParam_OneParam() {
            val testUri = "$baseUri?nonNullWithDefaultArrayParam=$input"
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                doubleArrayOf(expected),
                uriData.getNonNullWithDefaultArrayParam(),
                DELTA,
            )
        }

        @Test
        fun getNullableWithDefaultArrayParam_OneParam() {
            val testUri = "$baseUri?nullableWithDefaultArrayParam=$input"
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

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
                arrayOf(1.5, 1.5),
                arrayOf(-1.5, -1.5),
                arrayOf(0, 0.0),
                arrayOf(100, 100.0),
                arrayOf("1e3", 1000.0),
                arrayOf(Double.MAX_VALUE, Double.MAX_VALUE),
                arrayOf(Double.MIN_VALUE, Double.MIN_VALUE),
            )
        }
    }

    @RunWith(ParameterizedRobolectricTestRunner::class)
    class DoubleTestUriDataNonNullArrayTest(
        private val input: List<Any>,
        private val expectedArray: List<Double>,
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
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                expectedArray.toDoubleArray(),
                uriData.getNonNullArrayParam(),
                DELTA,
            )
        }

        @Test
        fun getNullableArrayParam_TwoParams() {
            val (expectedParam1, expectedParam2) = input
            val testUri = "$baseUri?nullableArrayParam=$expectedParam1&nullableArrayParam=$expectedParam2"
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                expectedArray.toTypedArray(),
                uriData.getNullableArrayParam(),
            )
        }

        @Test
        fun getNonNullWithDefaultArrayParam_TwoParams() {
            val (expectedParam1, expectedParam2) = input
            val testUri = "$baseUri?nonNullWithDefaultArrayParam=$expectedParam1&nonNullWithDefaultArrayParam=$expectedParam2"
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                expectedArray.toDoubleArray(),
                uriData.getNonNullWithDefaultArrayParam(),
                DELTA,
            )
        }

        @Test
        fun getNullableWithDefaultArrayParam_TwoParams() {
            val (expectedParam1, expectedParam2) = input
            val testUri = "$baseUri?nullableWithDefaultArrayParam=$expectedParam1&nullableWithDefaultArrayParam=$expectedParam2"
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

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
                arrayOf(listOf(1.5, -100), listOf(1.5, -100.0)),
                arrayOf(listOf(-100, 1.5), listOf(-100.0, 1.5)),
                arrayOf(listOf(Double.MAX_VALUE, Double.MIN_VALUE), listOf(Double.MAX_VALUE, Double.MIN_VALUE)),
            )
        }
    }

    /**
     * Values that can't be parsed to [Double] fall back to `0` for non-null getters, to `null`
     * for nullable getters and to the [boringyuri.api.DefaultValue] when it is specified.
     */
    @RunWith(ParameterizedRobolectricTestRunner::class)
    class DoubleTestUriDataInvalidValueTest(
        private val input: Any,
    ) {
        private lateinit var baseUri: String

        @Before
        fun setUp() {
            baseUri = "https://example.com/path/segment"
        }

        @Test
        fun getDoubleSegment_InvalidValue_ReturnsZero() {
            val testUri = "$baseUri/$input"
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            assertEquals(0.0, uriData.getDoubleSegment(), DELTA)
        }

        @Test
        fun getDoubleWithDefaultSegment_InvalidValue_ReturnsDefault() {
            val testUri = "$baseUri/0/$input"
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            assertEquals(1.2, uriData.getDoubleWithDefaultSegment(), DELTA)
        }

        @Test
        fun getNullableParam_InvalidValue_ReturnsNull() {
            val testUri = "$baseUri?nullableParam=$input"
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            assertNull(uriData.getNullableParam())
        }

        @Test
        fun getNonNullParam_InvalidValue_ReturnsZero() {
            val testUri = "$baseUri?nonNullParam=$input"
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            assertEquals(0.0, uriData.getNonNullParam(), DELTA)
        }

        @Test
        fun getNonNullWithDefaultParam_InvalidValue_ReturnsDefault() {
            val testUri = "$baseUri?nonNullWithDefaultParam=$input"
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            assertEquals(1.2, uriData.getNonNullWithDefaultParam(), DELTA)
        }

        @Test
        fun getNullableWithDefaultParam_InvalidValue_ReturnsDefault() {
            val testUri = "$baseUri?nullableWithDefaultParam=$input"
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            assertEquals(1.2, uriData.getNullableWithDefaultParam())
        }

        @Test
        fun getNullableArrayParam_ValidAndInvalidValues_InvalidElementIsNull() {
            val testUri = "$baseUri?nullableArrayParam=42&nullableArrayParam=$input"
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                arrayOf(42.0, null),
                uriData.getNullableArrayParam(),
            )
        }

        @Test
        fun getNonNullArrayParam_ValidAndInvalidValues_InvalidElementIsZero() {
            val testUri = "$baseUri?nonNullArrayParam=42&nonNullArrayParam=$input"
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                doubleArrayOf(42.0, 0.0),
                uriData.getNonNullArrayParam(),
                DELTA,
            )
        }

        @Test
        fun getNonNullWithDefaultArrayParam_ValidAndInvalidValues_InvalidElementIsDefault() {
            val testUri = "$baseUri?nonNullWithDefaultArrayParam=42&nonNullWithDefaultArrayParam=$input"
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                doubleArrayOf(42.0, 1.2),
                uriData.getNonNullWithDefaultArrayParam(),
                DELTA,
            )
        }

        @Test
        fun getNullableWithDefaultArrayParam_ValidAndInvalidValues_InvalidElementIsDefault() {
            val testUri = "$baseUri?nullableWithDefaultArrayParam=42&nullableWithDefaultArrayParam=$input"
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                arrayOf(42.0, 1.2),
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
    class DoubleTestUriDataNullableOrDefaultTest {
        private lateinit var baseUri: String

        @Before
        fun setUp() {
            baseUri = "https://example.com/path/segment"
        }

        @Test(expected = NullPointerException::class)
        fun getDoubleSegment_SegmentIsMissing_ThrowException() {
            val testUri = baseUri
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            uriData.getDoubleSegment()
        }

        @Test(expected = NullPointerException::class)
        fun getDoubleWithDefaultSegment_SegmentIsMissing_ThrowException() {
            val testUri = "$baseUri/0"
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            uriData.getDoubleWithDefaultSegment()
        }

        @Test(expected = NullPointerException::class)
        fun getNonNullParam_ParamIsMissing_ThrowException() {
            val testUri = baseUri
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            uriData.getNonNullParam()
        }

        @Test(expected = NullPointerException::class)
        fun getNonNullArrayParam_ParamIsMissing_ThrowException() {
            val testUri = baseUri
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            uriData.getNonNullArrayParam()
        }

        @Test
        fun getNullableParam() {
            val testUri = baseUri
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            assertNull(uriData.getNullableParam())
        }

        @Test
        fun getNonNullWithDefaultParam() {
            val testUri = baseUri
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            assertEquals(1.2, uriData.getNonNullWithDefaultParam(), DELTA)
        }

        @Test
        fun getNullableWithDefaultParam() {
            val testUri = baseUri
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            assertEquals(1.2, uriData.getNullableWithDefaultParam())
        }

        @Test
        fun getNullableArrayParam() {
            val testUri = baseUri
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            assertNull(uriData.getNullableArrayParam())
        }

        @Test
        fun getNonNullWithDefaultArrayParam() {
            val testUri = baseUri
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                doubleArrayOf(1.2),
                uriData.getNonNullWithDefaultArrayParam(),
                DELTA,
            )
        }

        @Test
        fun getNonNullWithDefaultArrayParam_OneInvalidParam() {
            val testUri = "$baseUri?nonNullWithDefaultArrayParam=invalid"
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                doubleArrayOf(1.2),
                uriData.getNonNullWithDefaultArrayParam(),
                DELTA,
            )
        }

        @Test
        fun getNonNullWithDefaultArrayParam_TwoInvalidParams() {
            val testUri = "$baseUri?nonNullWithDefaultArrayParam=invalid&nonNullWithDefaultArrayParam=invalid"
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                doubleArrayOf(1.2, 1.2),
                uriData.getNonNullWithDefaultArrayParam(),
                DELTA,
            )
        }

        @Test
        fun getNullableWithDefaultArrayParam() {
            val testUri = baseUri
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                arrayOf(1.2),
                uriData.getNullableWithDefaultArrayParam(),
            )
        }

        @Test
        fun getNullableWithDefaultArrayParam_OneInvalidParam() {
            val testUri = "$baseUri?nullableWithDefaultArrayParam=invalid"
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                arrayOf(1.2),
                uriData.getNullableWithDefaultArrayParam(),
            )
        }

        @Test
        fun getNullableWithDefaultArrayParam_TwoInvalidParams() {
            val testUri = "$baseUri?nullableWithDefaultArrayParam=invalid&nullableWithDefaultArrayParam=invalid"
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                arrayOf(1.2, 1.2),
                uriData.getNullableWithDefaultArrayParam(),
            )
        }

        @Test
        fun toString_ReturnsCorrectUri() {
            val testUri = "$baseUri/0.5/1.5?nonNullParam=2.5&nullableParam=3.5"
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                testUri,
                uriData.toString()
            )
        }
    }

    @RunWith(RobolectricTestRunner::class)
    class DoubleTestUriDataTypeAdapterTest {
        private lateinit var baseUri: String

        @Before
        fun setUp() {
            baseUri = "https://example.com/path/segment"
        }

        @Test
        fun getNullableDoubleArrayParam_OneParam() {
            val testUri = "$baseUri?nullableDoubleArrayParam=1.5%3B-2.5"
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                arrayOf(doubleArrayOf(1.5, -2.5)),
                uriData.getNullableDoubleArrayParam(),
            )
        }

        @Test
        fun getNullableDoubleArrayParam_TwoParams() {
            val testUri = "$baseUri?nullableDoubleArrayParam=1.5%3B-2.5&nullableDoubleArrayParam=3.5"
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                arrayOf(doubleArrayOf(1.5, -2.5), doubleArrayOf(3.5)),
                uriData.getNullableDoubleArrayParam(),
            )
        }

        @Test
        fun getNullableDoubleArrayParam_ParamIsMissing_ReturnsNull() {
            val testUri = baseUri
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            assertNull(uriData.getNullableDoubleArrayParam())
        }

        @Test
        fun getNonNullDoubleArrayParam_OneParam() {
            val testUri = "$baseUri?nonNullDoubleArrayParam=1.5%3B-2.5"
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                arrayOf(doubleArrayOf(1.5, -2.5)),
                uriData.getNonNullDoubleArrayParam(),
            )
        }

        @Test
        fun getNonNullDoubleArrayParam_TwoParams() {
            val testUri = "$baseUri?nonNullDoubleArrayParam=1.5%3B-2.5&nonNullDoubleArrayParam=3.5"
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                arrayOf(doubleArrayOf(1.5, -2.5), doubleArrayOf(3.5)),
                uriData.getNonNullDoubleArrayParam(),
            )
        }

        @Test(expected = NullPointerException::class)
        fun getNonNullDoubleArrayParam_ParamIsMissing_ThrowException() {
            val testUri = baseUri
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            uriData.getNonNullDoubleArrayParam()
        }

        @Test
        fun getNonNullWithDefaultDoubleArrayParam_OneParam() {
            val testUri = "$baseUri?nonNullWithDefaultDoubleArrayParam=1.5%3B-2.5"
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                arrayOf(doubleArrayOf(1.5, -2.5)),
                uriData.getNonNullWithDefaultDoubleArrayParam(),
            )
        }

        @Test
        fun getNonNullWithDefaultDoubleArrayParam_ParamIsMissing_ReturnsDefault() {
            val testUri = baseUri
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                arrayOf(doubleArrayOf(1.2, 2.3)),
                uriData.getNonNullWithDefaultDoubleArrayParam(),
            )
        }

        @Test
        fun getNullableWithDefaultDoubleArrayParam_OneParam() {
            val testUri = "$baseUri?nullableWithDefaultDoubleArrayParam=1.5%3B-2.5"
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                arrayOf(doubleArrayOf(1.5, -2.5)),
                uriData.getNullableWithDefaultDoubleArrayParam(),
            )
        }

        @Test
        fun getNullableWithDefaultDoubleArrayParam_ParamIsMissing_ReturnsDefault() {
            val testUri = baseUri
            val uriData: DoubleTestUriData = DoubleTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                arrayOf(doubleArrayOf(1.2, 2.3)),
                uriData.getNullableWithDefaultDoubleArrayParam(),
            )
        }
    }
}
