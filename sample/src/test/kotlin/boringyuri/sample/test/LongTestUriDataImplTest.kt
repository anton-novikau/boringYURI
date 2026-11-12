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

@RunWith(Enclosed::class)
class LongTestUriDataImplTest {
    @RunWith(ParameterizedRobolectricTestRunner::class)
    class LongTestUriDataNonNullTest(
        private val input: Any,
        private val expected: Long,
    ) {

        private lateinit var baseUri: String

        @Before
        fun setUp() {
            baseUri = "https://example.com/path/segment"
        }

        @Test
        fun getLongSegment() {
            val testUri = "$baseUri/$input"
            val uriData = LongTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getLongSegment()
            )
        }

        @Test
        fun getLongWithDefaultSegment() {
            val testUri = "$baseUri/0/$input"
            val uriData: LongTestUriData = LongTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getLongWithDefaultSegment()
            )
        }

        @Test
        fun getNullableParam() {
            val testUri = "$baseUri?nullableParam=$input"
            val uriData: LongTestUriData = LongTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getNullableParam()
            )
        }

        @Test
        fun getNonNullParam() {
            val testUri = "$baseUri?nonNullParam=$input"
            val uriData: LongTestUriData = LongTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getNonNullParam()
            )
        }

        @Test
        fun getNonNullWithDefaultParam() {
            val testUri = "$baseUri?nonNullWithDefaultParam=$input"
            val uriData: LongTestUriData = LongTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getNonNullWithDefaultParam()
            )
        }

        @Test
        fun getNullableWithDefaultParam() {
            val testUri = "$baseUri?nullableWithDefaultParam=$input"
            val uriData: LongTestUriData = LongTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getNullableWithDefaultParam()
            )
        }

        @Test
        fun getNullableArrayParam_OneParam() {
            val testUri = "$baseUri?nullableArrayParam=$input"
            val uriData: LongTestUriData = LongTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                arrayOf(expected),
                uriData.getNullableArrayParam(),
            )
        }

        @Test
        fun getNonNullArrayParam_OneParam() {
            val testUri = "$baseUri?nonNullArrayParam=$input"
            val uriData: LongTestUriData = LongTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                longArrayOf(expected),
                uriData.getNonNullArrayParam(),
            )
        }

        @Test
        fun getNonNullWithDefaultArrayParam_OneParam() {
            val testUri = "$baseUri?nonNullWithDefaultArrayParam=$input"
            val uriData: LongTestUriData = LongTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                longArrayOf(expected),
                uriData.getNonNullWithDefaultArrayParam(),
            )
        }

        @Test
        fun getNullableWithDefaultArrayParam_OneParam() {
            val testUri = "$baseUri?nullableWithDefaultArrayParam=$input"
            val uriData: LongTestUriData = LongTestUriDataImpl(Uri.parse(testUri))

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
                arrayOf(100, 100L),
                arrayOf(-100, -100L),
                arrayOf(0, 0L),
                arrayOf(Long.MAX_VALUE, Long.MAX_VALUE),
                arrayOf(Long.MIN_VALUE, Long.MIN_VALUE),
            )
        }
    }

    @RunWith(ParameterizedRobolectricTestRunner::class)
    class LongTestUriDataNonNullArrayTest(
        private val input: List<Any>,
        private val expectedArray: List<Long>,
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
            val uriData: LongTestUriData = LongTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                expectedArray.toLongArray(),
                uriData.getNonNullArrayParam(),
            )
        }

        @Test
        fun getNullableArrayParam_TwoParams() {
            val (expectedParam1, expectedParam2) = input
            val testUri = "$baseUri?nullableArrayParam=$expectedParam1&nullableArrayParam=$expectedParam2"
            val uriData: LongTestUriData = LongTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                expectedArray.toTypedArray(),
                uriData.getNullableArrayParam(),
            )
        }

        @Test
        fun getNonNullWithDefaultArrayParam_TwoParams() {
            val (expectedParam1, expectedParam2) = input
            val testUri = "$baseUri?nonNullWithDefaultArrayParam=$expectedParam1&nonNullWithDefaultArrayParam=$expectedParam2"
            val uriData: LongTestUriData = LongTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                expectedArray.toLongArray(),
                uriData.getNonNullWithDefaultArrayParam(),
            )
        }

        @Test
        fun getNullableWithDefaultArrayParam_TwoParams() {
            val (expectedParam1, expectedParam2) = input
            val testUri = "$baseUri?nullableWithDefaultArrayParam=$expectedParam1&nullableWithDefaultArrayParam=$expectedParam2"
            val uriData: LongTestUriData = LongTestUriDataImpl(Uri.parse(testUri))

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
                arrayOf(listOf(1, -100), listOf<Long>(1, -100)),
                arrayOf(listOf(-100, 1), listOf<Long>(-100, 1)),
                arrayOf(listOf(Long.MAX_VALUE, Long.MIN_VALUE), listOf(Long.MAX_VALUE, Long.MIN_VALUE)),
            )
        }
    }

    /**
     * Values that can't be parsed to [Long] fall back to `0` for non-null getters, to `null`
     * for nullable getters and to the [boringyuri.api.DefaultValue] when it is specified.
     */
    @RunWith(ParameterizedRobolectricTestRunner::class)
    class LongTestUriDataInvalidValueTest(
        private val input: Any,
    ) {
        private lateinit var baseUri: String

        @Before
        fun setUp() {
            baseUri = "https://example.com/path/segment"
        }

        @Test
        fun getLongSegment_InvalidValue_ReturnsZero() {
            val testUri = "$baseUri/$input"
            val uriData: LongTestUriData = LongTestUriDataImpl(Uri.parse(testUri))

            assertEquals(0L, uriData.getLongSegment())
        }

        @Test
        fun getLongWithDefaultSegment_InvalidValue_ReturnsDefault() {
            val testUri = "$baseUri/0/$input"
            val uriData: LongTestUriData = LongTestUriDataImpl(Uri.parse(testUri))

            assertEquals(1L, uriData.getLongWithDefaultSegment())
        }

        @Test
        fun getNullableParam_InvalidValue_ReturnsNull() {
            val testUri = "$baseUri?nullableParam=$input"
            val uriData: LongTestUriData = LongTestUriDataImpl(Uri.parse(testUri))

            assertNull(uriData.getNullableParam())
        }

        @Test
        fun getNonNullParam_InvalidValue_ReturnsZero() {
            val testUri = "$baseUri?nonNullParam=$input"
            val uriData: LongTestUriData = LongTestUriDataImpl(Uri.parse(testUri))

            assertEquals(0L, uriData.getNonNullParam())
        }

        @Test
        fun getNonNullWithDefaultParam_InvalidValue_ReturnsDefault() {
            val testUri = "$baseUri?nonNullWithDefaultParam=$input"
            val uriData: LongTestUriData = LongTestUriDataImpl(Uri.parse(testUri))

            assertEquals(1L, uriData.getNonNullWithDefaultParam())
        }

        @Test
        fun getNullableWithDefaultParam_InvalidValue_ReturnsDefault() {
            val testUri = "$baseUri?nullableWithDefaultParam=$input"
            val uriData: LongTestUriData = LongTestUriDataImpl(Uri.parse(testUri))

            assertEquals(1L, uriData.getNullableWithDefaultParam())
        }

        @Test
        fun getNullableArrayParam_ValidAndInvalidValues_InvalidElementIsNull() {
            val testUri = "$baseUri?nullableArrayParam=42&nullableArrayParam=$input"
            val uriData: LongTestUriData = LongTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                arrayOf(42L, null),
                uriData.getNullableArrayParam(),
            )
        }

        @Test
        fun getNonNullArrayParam_ValidAndInvalidValues_InvalidElementIsZero() {
            val testUri = "$baseUri?nonNullArrayParam=42&nonNullArrayParam=$input"
            val uriData: LongTestUriData = LongTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                longArrayOf(42, 0),
                uriData.getNonNullArrayParam(),
            )
        }

        @Test
        fun getNonNullWithDefaultArrayParam_ValidAndInvalidValues_InvalidElementIsDefault() {
            val testUri = "$baseUri?nonNullWithDefaultArrayParam=42&nonNullWithDefaultArrayParam=$input"
            val uriData: LongTestUriData = LongTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                longArrayOf(42, 1),
                uriData.getNonNullWithDefaultArrayParam(),
            )
        }

        @Test
        fun getNullableWithDefaultArrayParam_ValidAndInvalidValues_InvalidElementIsDefault() {
            val testUri = "$baseUri?nullableWithDefaultArrayParam=42&nullableWithDefaultArrayParam=$input"
            val uriData: LongTestUriData = LongTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                arrayOf(42L, 1L),
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
                arrayOf("9223372036854775808"),
                arrayOf("-9223372036854775809"),
            )
        }
    }

    @RunWith(RobolectricTestRunner::class)
    class LongTestUriDataNullableOrDefaultTest {
        private lateinit var baseUri: String

        @Before
        fun setUp() {
            baseUri = "https://example.com/path/segment"
        }

        @Test(expected = NullPointerException::class)
        fun getLongSegment_SegmentIsMissing_ThrowException() {
            val testUri = baseUri
            val uriData: LongTestUriData = LongTestUriDataImpl(Uri.parse(testUri))

            uriData.getLongSegment()
        }

        @Test(expected = NullPointerException::class)
        fun getLongWithDefaultSegment_SegmentIsMissing_ThrowException() {
            val testUri = "$baseUri/0"
            val uriData: LongTestUriData = LongTestUriDataImpl(Uri.parse(testUri))

            uriData.getLongWithDefaultSegment()
        }

        @Test(expected = NullPointerException::class)
        fun getNonNullParam_ParamIsMissing_ThrowException() {
            val testUri = baseUri
            val uriData: LongTestUriData = LongTestUriDataImpl(Uri.parse(testUri))

            uriData.getNonNullParam()
        }

        @Test(expected = NullPointerException::class)
        fun getNonNullArrayParam_ParamIsMissing_ThrowException() {
            val testUri = baseUri
            val uriData: LongTestUriData = LongTestUriDataImpl(Uri.parse(testUri))

            uriData.getNonNullArrayParam()
        }

        @Test
        fun getNullableParam() {
            val testUri = baseUri
            val uriData: LongTestUriData = LongTestUriDataImpl(Uri.parse(testUri))

            assertNull(uriData.getNullableParam())
        }

        @Test
        fun getNonNullWithDefaultParam() {
            val testUri = baseUri
            val uriData: LongTestUriData = LongTestUriDataImpl(Uri.parse(testUri))

            assertEquals(1L, uriData.getNonNullWithDefaultParam())
        }

        @Test
        fun getNullableWithDefaultParam() {
            val testUri = baseUri
            val uriData: LongTestUriData = LongTestUriDataImpl(Uri.parse(testUri))

            assertEquals(1L, uriData.getNullableWithDefaultParam())
        }

        @Test
        fun getNullableArrayParam() {
            val testUri = baseUri
            val uriData: LongTestUriData = LongTestUriDataImpl(Uri.parse(testUri))

            assertNull(uriData.getNullableArrayParam())
        }

        @Test
        fun getNonNullWithDefaultArrayParam() {
            val testUri = baseUri
            val uriData: LongTestUriData = LongTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                longArrayOf(1),
                uriData.getNonNullWithDefaultArrayParam(),
            )
        }

        @Test
        fun getNonNullWithDefaultArrayParam_OneInvalidParam() {
            val testUri = "$baseUri?nonNullWithDefaultArrayParam=invalid"
            val uriData: LongTestUriData = LongTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                longArrayOf(1),
                uriData.getNonNullWithDefaultArrayParam(),
            )
        }

        @Test
        fun getNonNullWithDefaultArrayParam_TwoInvalidParams() {
            val testUri = "$baseUri?nonNullWithDefaultArrayParam=invalid&nonNullWithDefaultArrayParam=invalid"
            val uriData: LongTestUriData = LongTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                longArrayOf(1, 1),
                uriData.getNonNullWithDefaultArrayParam(),
            )
        }

        @Test
        fun getNullableWithDefaultArrayParam() {
            val testUri = baseUri
            val uriData: LongTestUriData = LongTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                arrayOf(1L),
                uriData.getNullableWithDefaultArrayParam(),
            )
        }

        @Test
        fun getNullableWithDefaultArrayParam_OneInvalidParam() {
            val testUri = "$baseUri?nullableWithDefaultArrayParam=invalid"
            val uriData: LongTestUriData = LongTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                arrayOf(1L),
                uriData.getNullableWithDefaultArrayParam(),
            )
        }

        @Test
        fun getNullableWithDefaultArrayParam_TwoInvalidParams() {
            val testUri = "$baseUri?nullableWithDefaultArrayParam=invalid&nullableWithDefaultArrayParam=invalid"
            val uriData: LongTestUriData = LongTestUriDataImpl(Uri.parse(testUri))

            assertArrayEquals(
                arrayOf(1L, 1L),
                uriData.getNullableWithDefaultArrayParam(),
            )
        }

        @Test
        fun toString_ReturnsCorrectUri() {
            val testUri = "$baseUri/0/1?nonNullParam=2&nullableParam=3"
            val uriData: LongTestUriData = LongTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                testUri,
                uriData.toString()
            )
        }
    }
}
