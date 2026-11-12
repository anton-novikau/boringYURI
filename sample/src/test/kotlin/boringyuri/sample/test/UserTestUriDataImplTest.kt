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
import boringyuri.sample.data.User
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.RobolectricTestRunner

@RunWith(Enclosed::class)
class UserTestUriDataImplTest {
    @RunWith(ParameterizedRobolectricTestRunner::class)
    class UserTestUriDataNonNullTest(
        private val input: String,
        private val expected: Pair<Int, String>,
    ) {

        private lateinit var baseUri: String

        @Before
        fun setUp() {
            baseUri = "https://example.com/path/segment"
        }

        @Test
        fun getUserSegment() {
            val testUri = "$baseUri/$input"
            val uriData = UserTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getUserSegment().toPair()
            )
        }

        @Test
        fun getNullableParam() {
            val testUri = "$baseUri?nullableParam=$input"
            val uriData: UserTestUriData = UserTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getNullableParam()?.toPair()
            )
        }

        @Test
        fun getNonNullParam() {
            val testUri = "$baseUri?nonNullParam=$input"
            val uriData: UserTestUriData = UserTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getNonNullParam().toPair()
            )
        }

        @Test
        fun getNonNullWithDefaultParam() {
            val testUri = "$baseUri?nonNullWithDefaultParam=$input"
            val uriData: UserTestUriData = UserTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expected,
                uriData.getNonNullWithDefaultParam().toPair()
            )
        }

        @Test
        fun getNullableArrayParam_OneParam() {
            val testUri = "$baseUri?nullableArrayParam=$input"
            val uriData: UserTestUriData = UserTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                listOf(expected),
                uriData.getNullableArrayParam()?.map { it.toPair() },
            )
        }

        @Test
        fun getNonNullArrayParam_OneParam() {
            val testUri = "$baseUri?nonNullArrayParam=$input"
            val uriData: UserTestUriData = UserTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                listOf(expected),
                uriData.getNonNullArrayParam().map { it.toPair() },
            )
        }

        @Test
        fun getNonNullWithDefaultArrayParam_OneParam() {
            val testUri = "$baseUri?nonNullWithDefaultArrayParam=$input"
            val uriData: UserTestUriData = UserTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                listOf(expected),
                uriData.getNonNullWithDefaultArrayParam().map { it.toPair() },
            )
        }

        companion object {
            // the testParams() provider is used by test runner to provide the parameters for the tests
            @Suppress("unused")
            @JvmStatic
            @ParameterizedRobolectricTestRunner.Parameters(name = "input = {0}, expected = {1}")
            fun testParams(): List<Array<Any>> = listOf(
                // arrayOf(input, expected),
                arrayOf("42%3BJohn%20Doe", 42 to "John Doe"),
                arrayOf("-1%3BJane", -1 to "Jane"),
                arrayOf("0%3BA%26B", 0 to "A&B"),
            )
        }
    }

    @RunWith(ParameterizedRobolectricTestRunner::class)
    class UserTestUriDataNonNullArrayTest(
        private val input: List<String>,
        private val expectedArray: List<Pair<Int, String>>,
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
            val uriData: UserTestUriData = UserTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expectedArray,
                uriData.getNonNullArrayParam().map { it.toPair() },
            )
        }

        @Test
        fun getNullableArrayParam_TwoParams() {
            val (expectedParam1, expectedParam2) = input
            val testUri = "$baseUri?nullableArrayParam=$expectedParam1&nullableArrayParam=$expectedParam2"
            val uriData: UserTestUriData = UserTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expectedArray,
                uriData.getNullableArrayParam()?.map { it.toPair() },
            )
        }

        @Test
        fun getNonNullWithDefaultArrayParam_TwoParams() {
            val (expectedParam1, expectedParam2) = input
            val testUri = "$baseUri?nonNullWithDefaultArrayParam=$expectedParam1&nonNullWithDefaultArrayParam=$expectedParam2"
            val uriData: UserTestUriData = UserTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                expectedArray,
                uriData.getNonNullWithDefaultArrayParam().map { it.toPair() },
            )
        }

        companion object {
            // the testParams() provider is used by test runner to provide the parameters for the tests
            @Suppress("unused")
            @JvmStatic
            @ParameterizedRobolectricTestRunner.Parameters(name = "input = {0}, expectedArray = {1}")
            fun testParams() = listOf(
                // arrayOf(input, expectedArray)
                arrayOf(listOf("1%3BJohn", "2%3BJane"), listOf(1 to "John", 2 to "Jane")),
                arrayOf(listOf("2%3BJane", "1%3BJohn"), listOf(2 to "Jane", 1 to "John")),
            )
        }
    }

    @RunWith(RobolectricTestRunner::class)
    class UserTestUriDataNullableOrDefaultTest {
        private lateinit var baseUri: String

        @Before
        fun setUp() {
            baseUri = "https://example.com/path/segment"
        }

        @Test(expected = NullPointerException::class)
        fun getUserSegment_SegmentIsMissing_ThrowException() {
            val testUri = baseUri
            val uriData: UserTestUriData = UserTestUriDataImpl(Uri.parse(testUri))

            uriData.getUserSegment()
        }

        @Test(expected = NullPointerException::class)
        fun getNonNullParam_ParamIsMissing_ThrowException() {
            val testUri = baseUri
            val uriData: UserTestUriData = UserTestUriDataImpl(Uri.parse(testUri))

            uriData.getNonNullParam()
        }

        @Test(expected = NullPointerException::class)
        fun getNonNullArrayParam_ParamIsMissing_ThrowException() {
            val testUri = baseUri
            val uriData: UserTestUriData = UserTestUriDataImpl(Uri.parse(testUri))

            uriData.getNonNullArrayParam()
        }

        @Test
        fun getNullableParam() {
            val testUri = baseUri
            val uriData: UserTestUriData = UserTestUriDataImpl(Uri.parse(testUri))

            assertNull(uriData.getNullableParam())
        }

        @Test
        fun getNonNullWithDefaultParam() {
            val testUri = baseUri
            val uriData: UserTestUriData = UserTestUriDataImpl(Uri.parse(testUri))

            assertEquals(42 to "John Doe", uriData.getNonNullWithDefaultParam().toPair())
        }

        @Test
        fun getNullableArrayParam() {
            val testUri = baseUri
            val uriData: UserTestUriData = UserTestUriDataImpl(Uri.parse(testUri))

            assertNull(uriData.getNullableArrayParam())
        }

        @Test
        fun getNonNullWithDefaultArrayParam() {
            val testUri = baseUri
            val uriData: UserTestUriData = UserTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                listOf(42 to "John Doe"),
                uriData.getNonNullWithDefaultArrayParam().map { it.toPair() },
            )
        }

        @Test
        fun toString_ReturnsCorrectUri() {
            val testUri = "$baseUri/1%3BJohn?nonNullParam=2%3BJane"
            val uriData: UserTestUriData = UserTestUriDataImpl(Uri.parse(testUri))

            assertEquals(
                testUri,
                uriData.toString()
            )
        }
    }
}

/**
 * [User] doesn't implement `equals()`, so it is compared
 * by its properties.
 */
private fun User.toPair(): Pair<Int, String> = id to name
