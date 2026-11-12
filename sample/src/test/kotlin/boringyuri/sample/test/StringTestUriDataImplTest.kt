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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.RobolectricTestRunner

@RunWith(Enclosed::class)
class StringTestUriDataImplTest {
    @RunWith(ParameterizedRobolectricTestRunner::class)
    class StringTestUriDataNonNullTest(
        private val input: String,
        private val expected: String,
    ) {

        private lateinit var baseUri: String

        @Before
        fun setUp() {
            baseUri = "https://example.com/path/segment"
        }

        @Test
        fun getStringSegment() {
            val testUri = "$baseUri/$input"
            val uriData = StringTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getStringSegment()
            )
        }

        @Test
        fun getNullableParam() {
            val testUri = "$baseUri?nullableParam=$input"
            val uriData: StringTestUriData = StringTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getNullableParam()
            )
        }

        @Test
        fun getNonNullParam() {
            val testUri = "$baseUri?nonNullParam=$input"
            val uriData: StringTestUriData = StringTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getNonNullParam()
            )
        }

        @Test
        fun getNonNullWithDefaultParam() {
            val testUri = "$baseUri?nonNullWithDefaultParam=$input"
            val uriData: StringTestUriData = StringTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getNonNullWithDefaultParam()
            )
        }

        @Test
        fun getNullableArrayParam_OneParam() {
            val testUri = "$baseUri?nullableArrayParam=$input"
            val uriData: StringTestUriData = StringTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                listOf(expected),
                uriData.getNullableArrayParam()?.toList(),
            )
        }

        @Test
        fun getNonNullArrayParam_OneParam() {
            val testUri = "$baseUri?nonNullArrayParam=$input"
            val uriData: StringTestUriData = StringTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                listOf(expected),
                uriData.getNonNullArrayParam().toList(),
            )
        }

        @Test
        fun getNonNullWithDefaultArrayParam_OneParam() {
            val testUri = "$baseUri?nonNullWithDefaultArrayParam=$input"
            val uriData: StringTestUriData = StringTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                listOf(expected),
                uriData.getNonNullWithDefaultArrayParam().toList(),
            )
        }

        companion object {
            // the testParams() provider is used by test runner to provide the parameters for the tests
            @Suppress("unused")
            @JvmStatic
            @ParameterizedRobolectricTestRunner.Parameters(name = "input = {0}, expected = {1}")
            fun testParams(): List<Array<Any>> = listOf(
                // arrayOf(input, expected),
                arrayOf("value", "value"),
                arrayOf("42", "42"),
                arrayOf("hello%20world", "hello world"),
                arrayOf("a%26b%3Dc", "a&b=c"),
                arrayOf("%C3%A9t%C3%A9", "été"),
            )
        }
    }

    @RunWith(ParameterizedRobolectricTestRunner::class)
    class StringTestUriDataNonNullArrayTest(
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
            val uriData: StringTestUriData = StringTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expectedArray,
                uriData.getNonNullArrayParam().toList(),
            )
        }

        @Test
        fun getNullableArrayParam_TwoParams() {
            val (expectedParam1, expectedParam2) = input
            val testUri = "$baseUri?nullableArrayParam=$expectedParam1&nullableArrayParam=$expectedParam2"
            val uriData: StringTestUriData = StringTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expectedArray,
                uriData.getNullableArrayParam()?.toList(),
            )
        }

        @Test
        fun getNonNullWithDefaultArrayParam_TwoParams() {
            val (expectedParam1, expectedParam2) = input
            val testUri = "$baseUri?nonNullWithDefaultArrayParam=$expectedParam1&nonNullWithDefaultArrayParam=$expectedParam2"
            val uriData: StringTestUriData = StringTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expectedArray,
                uriData.getNonNullWithDefaultArrayParam().toList(),
            )
        }

        companion object {
            // the testParams() provider is used by test runner to provide the parameters for the tests
            @Suppress("unused")
            @JvmStatic
            @ParameterizedRobolectricTestRunner.Parameters(name = "input = {0}, expectedArray = {1}")
            fun testParams() = listOf(
                // arrayOf(input, expectedArray)
                arrayOf(listOf("a", "b"), listOf("a", "b")),
                arrayOf(listOf("b", "a"), listOf("b", "a")),
                arrayOf(listOf("hello%20world", "%26"), listOf("hello world", "&")),
            )
        }
    }

    /**
     * An empty value is a valid [String], so it is returned as is even if the getter
     * has a [boringyuri.api.DefaultValue] specified.
     */
    @RunWith(RobolectricTestRunner::class)
    class StringTestUriDataEmptyValueTest {
        private lateinit var baseUri: String

        @Before
        fun setUp() {
            baseUri = "https://example.com/path/segment"
        }

        @Test
        fun getNullableParam_EmptyValue_ReturnsEmptyString() {
            val testUri = "$baseUri?nullableParam="
            val uriData: StringTestUriData = StringTestUriDataImpl(Uri.parse(testUri))

            assertEquals("", uriData.getNullableParam())
        }

        @Test
        fun getNonNullParam_EmptyValue_ReturnsEmptyString() {
            val testUri = "$baseUri?nonNullParam="
            val uriData: StringTestUriData = StringTestUriDataImpl(Uri.parse(testUri))

            assertEquals("", uriData.getNonNullParam())
        }

        @Test
        fun getNonNullWithDefaultParam_EmptyValue_ReturnsEmptyString() {
            val testUri = "$baseUri?nonNullWithDefaultParam="
            val uriData: StringTestUriData = StringTestUriDataImpl(Uri.parse(testUri))

            assertEquals("", uriData.getNonNullWithDefaultParam())
        }

        @Test
        fun getNonNullWithDefaultArrayParam_ValidAndEmptyValues_EmptyElementIsKept() {
            val testUri = "$baseUri?nonNullWithDefaultArrayParam=a&nonNullWithDefaultArrayParam="
            val uriData: StringTestUriData = StringTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                listOf("a", ""),
                uriData.getNonNullWithDefaultArrayParam().toList(),
            )
        }
    }

    @RunWith(RobolectricTestRunner::class)
    class StringTestUriDataNullableOrDefaultTest {
        private lateinit var baseUri: String

        @Before
        fun setUp() {
            baseUri = "https://example.com/path/segment"
        }

        @Test(expected = NullPointerException::class)
        fun getStringSegment_SegmentIsMissing_ThrowException() {
            val testUri = baseUri
            val uriData: StringTestUriData = StringTestUriDataImpl(Uri.parse(testUri))

            uriData.getStringSegment()
        }

        @Test(expected = NullPointerException::class)
        fun getNonNullParam_ParamIsMissing_ThrowException() {
            val testUri = baseUri
            val uriData: StringTestUriData = StringTestUriDataImpl(Uri.parse(testUri))

            uriData.getNonNullParam()
        }

        @Test(expected = NullPointerException::class)
        fun getNonNullArrayParam_ParamIsMissing_ThrowException() {
            val testUri = baseUri
            val uriData: StringTestUriData = StringTestUriDataImpl(Uri.parse(testUri))

            uriData.getNonNullArrayParam()
        }

        @Test
        fun getNullableParam() {
            val testUri = baseUri
            val uriData: StringTestUriData = StringTestUriDataImpl(Uri.parse(testUri))

            assertNull(uriData.getNullableParam())
        }

        @Test
        fun getNonNullWithDefaultParam() {
            val testUri = baseUri
            val uriData: StringTestUriData = StringTestUriDataImpl(Uri.parse(testUri))

            assertEquals("default", uriData.getNonNullWithDefaultParam())
        }

        @Test
        fun getNullableArrayParam() {
            val testUri = baseUri
            val uriData: StringTestUriData = StringTestUriDataImpl(Uri.parse(testUri))

            assertNull(uriData.getNullableArrayParam())
        }

        @Test
        fun getNonNullWithDefaultArrayParam() {
            val testUri = baseUri
            val uriData: StringTestUriData = StringTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                listOf("default"),
                uriData.getNonNullWithDefaultArrayParam().toList(),
            )
        }

        @Test
        fun toString_ReturnsCorrectUri() {
            val testUri = "$baseUri/value?nonNullParam=a&nullableParam=b"
            val uriData: StringTestUriData = StringTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                testUri,
                uriData.toString()
            )
        }
    }
}
