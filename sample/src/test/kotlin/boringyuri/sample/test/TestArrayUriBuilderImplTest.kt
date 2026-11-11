package boringyuri.sample.test

import android.net.Uri
import boringyuri.sample.data.User
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.RobolectricTestRunner

@RunWith(Enclosed::class)
class TestArrayUriBuilderImplTest {
    @RunWith(ParameterizedRobolectricTestRunner::class)
    class BuildStringArrayUriTest(
        private val nullableParam: Array<String?>?,
        private val nonNullParam: Array<String>,
        private val nullableWithDefaultParam: Array<String?>?,
        private val nonNullWithDefaultParam: Array<String>,
        private val expectedUri: String,
    ) {

        @Test
        fun buildStringArrayUri() {
            val uri = buildUri()

            assertEquals(expectedUri, uri.toString())
        }

        @Test
        fun buildStringArrayUri_ParsedWithStringArrayUriData() {
            val uriData = StringArrayUriData(buildUri())

            // `null` elements are not added to the Uri
            assertArrayEquals(
                nullableParam?.filterNotNull()?.toTypedArray(),
                uriData.getNullableParam(),
            )
            assertArrayEquals(
                nonNullParam,
                uriData.getNonNullParam(),
            )
            assertArrayEquals(
                nullableWithDefaultParam?.filterNotNull()?.toTypedArray() ?: arrayOf<String>("default"),
                uriData.getNullableWithDefaultParam(),
            )
            assertArrayEquals(
                nonNullWithDefaultParam,
                uriData.getNonNullWithDefaultParam(),
            )
        }

        private fun buildUri(): Uri = TestArrayUriBuilderImpl().buildStringArrayUri(
            nullableParam,
            nonNullParam,
            nullableWithDefaultParam,
            nonNullWithDefaultParam,
        )

        companion object {
            // the testParams() provider is used by test runner to provide the parameters for the tests
            @Suppress("unused")
            @JvmStatic
            @ParameterizedRobolectricTestRunner.Parameters(name = "{4}")
            fun testParams(): List<Array<Any?>> = listOf(
                // arrayOf(nullableParam, nonNullParam, nullableWithDefaultParam,
                //         nonNullWithDefaultParam, expectedUri),
                arrayOf(
                    arrayOf<String?>("a b", "c&d"),
                    arrayOf("e", "f/g"),
                    arrayOf<String?>("h=i", "j"),
                    arrayOf("k?l", "m"),
                    "content://com.example.provider.test/path/segment?nullableParam=a%20b&nullableParam=c%26d&nonNullParam=e&nonNullParam=f%2Fg&nullableWithDefaultParam=h%3Di&nullableWithDefaultParam=j&nonNullWithDefaultParam=k%3Fl&nonNullWithDefaultParam=m",
                ),
                // `null` arrays with @DefaultValue are replaced with the default value,
                // `null` arrays without @DefaultValue are omitted
                arrayOf(
                    null,
                    arrayOf("e"),
                    null,
                    arrayOf("k?l"),
                    "content://com.example.provider.test/path/segment?nonNullParam=e&nullableWithDefaultParam=default&nonNullWithDefaultParam=k%3Fl",
                ),
                // `null` elements of the arrays are omitted
                arrayOf(
                    arrayOf<String?>(null, "c&d"),
                    arrayOf("e"),
                    arrayOf<String?>("h=i", null),
                    arrayOf("k?l"),
                    "content://com.example.provider.test/path/segment?nullableParam=c%26d&nonNullParam=e&nullableWithDefaultParam=h%3Di&nonNullWithDefaultParam=k%3Fl",
                ),
            )
        }
    }

    @RunWith(ParameterizedRobolectricTestRunner::class)
    class BuildBooleanArrayUriTest(
        private val nullableParam: Array<Boolean?>?,
        private val nonNullParam: BooleanArray,
        private val nullableWithDefaultParam: Array<Boolean?>?,
        private val nonNullWithDefaultParam: BooleanArray,
        private val expectedUri: String,
    ) {

        @Test
        fun buildBooleanArrayUri() {
            val uri = buildUri()

            assertEquals(expectedUri, uri.toString())
        }

        @Test
        fun buildBooleanArrayUri_ParsedWithBooleanArrayUriData() {
            val uriData = BooleanArrayUriData(buildUri())

            // `null` elements are not added to the Uri
            assertArrayEquals(
                nullableParam?.filterNotNull()?.toTypedArray(),
                uriData.getNullableParam(),
            )
            assertArrayEquals(
                nonNullParam,
                uriData.getNonNullParam(),
            )
            assertArrayEquals(
                nullableWithDefaultParam?.filterNotNull()?.toTypedArray() ?: arrayOf<Boolean>(true),
                uriData.getNullableWithDefaultParam(),
            )
            assertArrayEquals(
                nonNullWithDefaultParam,
                uriData.getNonNullWithDefaultParam(),
            )
        }

        private fun buildUri(): Uri = TestArrayUriBuilderImpl().buildBooleanArrayUri(
            nullableParam,
            nonNullParam,
            nullableWithDefaultParam,
            nonNullWithDefaultParam,
        )

        companion object {
            // the testParams() provider is used by test runner to provide the parameters for the tests
            @Suppress("unused")
            @JvmStatic
            @ParameterizedRobolectricTestRunner.Parameters(name = "{4}")
            fun testParams(): List<Array<Any?>> = listOf(
                // arrayOf(nullableParam, nonNullParam, nullableWithDefaultParam,
                //         nonNullWithDefaultParam, expectedUri),
                arrayOf(
                    arrayOf<Boolean?>(true, false),
                    booleanArrayOf(false, true),
                    arrayOf<Boolean?>(true, false),
                    booleanArrayOf(false, true),
                    "content://com.example.provider.test/path/segment?nullableParam=true&nullableParam=false&nonNullParam=false&nonNullParam=true&nullableWithDefaultParam=true&nullableWithDefaultParam=false&nonNullWithDefaultParam=false&nonNullWithDefaultParam=true",
                ),
                // `null` arrays with @DefaultValue are replaced with the default value,
                // `null` arrays without @DefaultValue are omitted
                arrayOf(
                    null,
                    booleanArrayOf(false),
                    null,
                    booleanArrayOf(false),
                    "content://com.example.provider.test/path/segment?nonNullParam=false&nullableWithDefaultParam=true&nonNullWithDefaultParam=false",
                ),
                // `null` elements of the arrays are omitted
                arrayOf(
                    arrayOf<Boolean?>(null, false),
                    booleanArrayOf(false),
                    arrayOf<Boolean?>(true, null),
                    booleanArrayOf(false),
                    "content://com.example.provider.test/path/segment?nullableParam=false&nonNullParam=false&nullableWithDefaultParam=true&nonNullWithDefaultParam=false",
                ),
            )
        }
    }

    @RunWith(ParameterizedRobolectricTestRunner::class)
    class BuildCharArrayUriTest(
        private val nullableParam: Array<Char?>?,
        private val nonNullParam: CharArray,
        private val nullableWithDefaultParam: Array<Char?>?,
        private val nonNullWithDefaultParam: CharArray?,
        private val expectedUri: String,
    ) {

        @Test
        fun buildCharArrayUri() {
            val uri = buildUri()

            assertEquals(expectedUri, uri.toString())
        }

        @Test
        fun buildCharArrayUri_ParsedWithCharArrayUriData() {
            val uriData = CharArrayUriData(buildUri())

            // `null` elements are not added to the Uri
            assertArrayEquals(
                nullableParam?.filterNotNull()?.toTypedArray(),
                uriData.getNullableParam(),
            )
            assertArrayEquals(
                nonNullParam,
                uriData.getNonNullParam(),
            )
            assertArrayEquals(
                nullableWithDefaultParam?.filterNotNull()?.toTypedArray() ?: arrayOf<Char>('c'),
                uriData.getNullableWithDefaultParam(),
            )
            assertArrayEquals(
                nonNullWithDefaultParam ?: charArrayOf('c'),
                uriData.getNonNullWithDefaultParam(),
            )
        }

        private fun buildUri(): Uri = TestArrayUriBuilderImpl().buildCharArrayUri(
            nullableParam,
            nonNullParam,
            nullableWithDefaultParam,
            nonNullWithDefaultParam,
        )

        companion object {
            // the testParams() provider is used by test runner to provide the parameters for the tests
            @Suppress("unused")
            @JvmStatic
            @ParameterizedRobolectricTestRunner.Parameters(name = "{4}")
            fun testParams(): List<Array<Any?>> = listOf(
                // arrayOf(nullableParam, nonNullParam, nullableWithDefaultParam,
                //         nonNullWithDefaultParam, expectedUri),
                arrayOf(
                    arrayOf<Char?>('a', 'b'),
                    charArrayOf(' ', '&'),
                    arrayOf<Char?>('é', 'x'),
                    charArrayOf('y', '/'),
                    "content://com.example.provider.test/path/segment?nullableParam=a&nullableParam=b&nonNullParam=%20&nonNullParam=%26&nullableWithDefaultParam=%C3%A9&nullableWithDefaultParam=x&nonNullWithDefaultParam=y&nonNullWithDefaultParam=%2F",
                ),
                // `null` arrays with @DefaultValue are replaced with the default value,
                // `null` arrays without @DefaultValue are omitted
                arrayOf(
                    null,
                    charArrayOf(' '),
                    null,
                    charArrayOf('y'),
                    "content://com.example.provider.test/path/segment?nonNullParam=%20&nullableWithDefaultParam=c&nonNullWithDefaultParam=y",
                ),
                // `null` elements of the arrays are omitted
                arrayOf(
                    arrayOf<Char?>(null, 'b'),
                    charArrayOf(' '),
                    arrayOf<Char?>('é', null),
                    charArrayOf('y'),
                    "content://com.example.provider.test/path/segment?nullableParam=b&nonNullParam=%20&nullableWithDefaultParam=%C3%A9&nonNullWithDefaultParam=y",
                ),
                // `nonNullWithDefaultParam` isn't annotated with @NonNull in the builder
                // interface, so `null` is replaced with the default value
                arrayOf(
                    null,
                    charArrayOf(' '),
                    null,
                    null,
                    "content://com.example.provider.test/path/segment?nonNullParam=%20&nullableWithDefaultParam=c&nonNullWithDefaultParam=c",
                ),
            )
        }
    }

    @RunWith(ParameterizedRobolectricTestRunner::class)
    class BuildByteArrayUriTest(
        private val nullableParam: Array<Byte?>?,
        private val nonNullParam: ByteArray,
        private val nullableWithDefaultParam: Array<Byte?>?,
        private val nonNullWithDefaultParam: ByteArray,
        private val expectedUri: String,
    ) {

        @Test
        fun buildByteArrayUri() {
            val uri = buildUri()

            assertEquals(expectedUri, uri.toString())
        }

        @Test
        fun buildByteArrayUri_ParsedWithByteArrayUriData() {
            val uriData = ByteArrayUriData(buildUri())

            // `null` elements are not added to the Uri
            assertArrayEquals(
                nullableParam?.filterNotNull()?.toTypedArray(),
                uriData.getNullableParam(),
            )
            assertArrayEquals(
                nonNullParam,
                uriData.getNonNullParam(),
            )
            assertArrayEquals(
                nullableWithDefaultParam?.filterNotNull()?.toTypedArray() ?: arrayOf<Byte>(1),
                uriData.getNullableWithDefaultParam(),
            )
            assertArrayEquals(
                nonNullWithDefaultParam,
                uriData.getNonNullWithDefaultParam(),
            )
        }

        private fun buildUri(): Uri = TestArrayUriBuilderImpl().buildByteArrayUri(
            nullableParam,
            nonNullParam,
            nullableWithDefaultParam,
            nonNullWithDefaultParam,
        )

        companion object {
            // the testParams() provider is used by test runner to provide the parameters for the tests
            @Suppress("unused")
            @JvmStatic
            @ParameterizedRobolectricTestRunner.Parameters(name = "{4}")
            fun testParams(): List<Array<Any?>> = listOf(
                // arrayOf(nullableParam, nonNullParam, nullableWithDefaultParam,
                //         nonNullWithDefaultParam, expectedUri),
                arrayOf(
                    arrayOf<Byte?>(Byte.MAX_VALUE, Byte.MIN_VALUE),
                    byteArrayOf(3, -4),
                    arrayOf<Byte?>(5, -6),
                    byteArrayOf(7, -8),
                    "content://com.example.provider.test/path/segment?nullableParam=127&nullableParam=-128&nonNullParam=3&nonNullParam=-4&nullableWithDefaultParam=5&nullableWithDefaultParam=-6&nonNullWithDefaultParam=7&nonNullWithDefaultParam=-8",
                ),
                // `null` arrays with @DefaultValue are replaced with the default value,
                // `null` arrays without @DefaultValue are omitted
                arrayOf(
                    null,
                    byteArrayOf(3),
                    null,
                    byteArrayOf(7),
                    "content://com.example.provider.test/path/segment?nonNullParam=3&nullableWithDefaultParam=1&nonNullWithDefaultParam=7",
                ),
                // `null` elements of the arrays are omitted
                arrayOf(
                    arrayOf<Byte?>(null, Byte.MIN_VALUE),
                    byteArrayOf(3),
                    arrayOf<Byte?>(5, null),
                    byteArrayOf(7),
                    "content://com.example.provider.test/path/segment?nullableParam=-128&nonNullParam=3&nullableWithDefaultParam=5&nonNullWithDefaultParam=7",
                ),
            )
        }
    }

    @RunWith(ParameterizedRobolectricTestRunner::class)
    class BuildShortArrayUriTest(
        private val nullableParam: Array<Short?>?,
        private val nonNullParam: ShortArray,
        private val nullableWithDefaultParam: Array<Short?>?,
        private val nonNullWithDefaultParam: ShortArray,
        private val expectedUri: String,
    ) {

        @Test
        fun buildShortArrayUri() {
            val uri = buildUri()

            assertEquals(expectedUri, uri.toString())
        }

        @Test
        fun buildShortArrayUri_ParsedWithShortArrayUriData() {
            val uriData = ShortArrayUriData(buildUri())

            // `null` elements are not added to the Uri
            assertArrayEquals(
                nullableParam?.filterNotNull()?.toTypedArray(),
                uriData.getNullableParam(),
            )
            assertArrayEquals(
                nonNullParam,
                uriData.getNonNullParam(),
            )
            assertArrayEquals(
                nullableWithDefaultParam?.filterNotNull()?.toTypedArray() ?: arrayOf<Short>(1),
                uriData.getNullableWithDefaultParam(),
            )
            assertArrayEquals(
                nonNullWithDefaultParam,
                uriData.getNonNullWithDefaultParam(),
            )
        }

        private fun buildUri(): Uri = TestArrayUriBuilderImpl().buildShortArrayUri(
            nullableParam,
            nonNullParam,
            nullableWithDefaultParam,
            nonNullWithDefaultParam,
        )

        companion object {
            // the testParams() provider is used by test runner to provide the parameters for the tests
            @Suppress("unused")
            @JvmStatic
            @ParameterizedRobolectricTestRunner.Parameters(name = "{4}")
            fun testParams(): List<Array<Any?>> = listOf(
                // arrayOf(nullableParam, nonNullParam, nullableWithDefaultParam,
                //         nonNullWithDefaultParam, expectedUri),
                arrayOf(
                    arrayOf<Short?>(Short.MAX_VALUE, Short.MIN_VALUE),
                    shortArrayOf(3, -4),
                    arrayOf<Short?>(5, -6),
                    shortArrayOf(7, -8),
                    "content://com.example.provider.test/path/segment?nullableParam=32767&nullableParam=-32768&nonNullParam=3&nonNullParam=-4&nullableWithDefaultParam=5&nullableWithDefaultParam=-6&nonNullWithDefaultParam=7&nonNullWithDefaultParam=-8",
                ),
                // `null` arrays with @DefaultValue are replaced with the default value,
                // `null` arrays without @DefaultValue are omitted
                arrayOf(
                    null,
                    shortArrayOf(3),
                    null,
                    shortArrayOf(7),
                    "content://com.example.provider.test/path/segment?nonNullParam=3&nullableWithDefaultParam=1&nonNullWithDefaultParam=7",
                ),
                // `null` elements of the arrays are omitted
                arrayOf(
                    arrayOf<Short?>(null, Short.MIN_VALUE),
                    shortArrayOf(3),
                    arrayOf<Short?>(5, null),
                    shortArrayOf(7),
                    "content://com.example.provider.test/path/segment?nullableParam=-32768&nonNullParam=3&nullableWithDefaultParam=5&nonNullWithDefaultParam=7",
                ),
            )
        }
    }

    @RunWith(ParameterizedRobolectricTestRunner::class)
    class BuildIntArrayUriTest(
        private val nullableParam: Array<Int?>?,
        private val nonNullParam: IntArray,
        private val nullableWithDefaultParam: Array<Int?>?,
        private val nonNullWithDefaultParam: IntArray,
        private val expectedUri: String,
    ) {

        @Test
        fun buildIntArrayUri() {
            val uri = buildUri()

            assertEquals(expectedUri, uri.toString())
        }

        @Test
        fun buildIntArrayUri_ParsedWithIntArrayUriData() {
            val uriData = IntArrayUriData(buildUri())

            // `null` elements are not added to the Uri
            assertArrayEquals(
                nullableParam?.filterNotNull()?.toTypedArray(),
                uriData.getNullableParam(),
            )
            assertArrayEquals(
                nonNullParam,
                uriData.getNonNullParam(),
            )
            assertArrayEquals(
                nullableWithDefaultParam?.filterNotNull()?.toTypedArray() ?: arrayOf<Int>(1),
                uriData.getNullableWithDefaultParam(),
            )
            assertArrayEquals(
                nonNullWithDefaultParam,
                uriData.getNonNullWithDefaultParam(),
            )
        }

        private fun buildUri(): Uri = TestArrayUriBuilderImpl().buildIntArrayUri(
            nullableParam,
            nonNullParam,
            nullableWithDefaultParam,
            nonNullWithDefaultParam,
        )

        companion object {
            // the testParams() provider is used by test runner to provide the parameters for the tests
            @Suppress("unused")
            @JvmStatic
            @ParameterizedRobolectricTestRunner.Parameters(name = "{4}")
            fun testParams(): List<Array<Any?>> = listOf(
                // arrayOf(nullableParam, nonNullParam, nullableWithDefaultParam,
                //         nonNullWithDefaultParam, expectedUri),
                arrayOf(
                    arrayOf<Int?>(Int.MAX_VALUE, Int.MIN_VALUE),
                    intArrayOf(3, -4),
                    arrayOf<Int?>(5, -6),
                    intArrayOf(7, -8),
                    "content://com.example.provider.test/path/segment?nullableParam=2147483647&nullableParam=-2147483648&nonNullParam=3&nonNullParam=-4&nullableWithDefaultParam=5&nullableWithDefaultParam=-6&nonNullWithDefaultParam=7&nonNullWithDefaultParam=-8",
                ),
                // `null` arrays with @DefaultValue are replaced with the default value,
                // `null` arrays without @DefaultValue are omitted
                arrayOf(
                    null,
                    intArrayOf(3),
                    null,
                    intArrayOf(7),
                    "content://com.example.provider.test/path/segment?nonNullParam=3&nullableWithDefaultParam=1&nonNullWithDefaultParam=7",
                ),
                // `null` elements of the arrays are omitted
                arrayOf(
                    arrayOf<Int?>(null, Int.MIN_VALUE),
                    intArrayOf(3),
                    arrayOf<Int?>(5, null),
                    intArrayOf(7),
                    "content://com.example.provider.test/path/segment?nullableParam=-2147483648&nonNullParam=3&nullableWithDefaultParam=5&nonNullWithDefaultParam=7",
                ),
            )
        }
    }

    @RunWith(ParameterizedRobolectricTestRunner::class)
    class BuildLongArrayUriTest(
        private val nullableParam: Array<Long?>?,
        private val nonNullParam: LongArray,
        private val nullableWithDefaultParam: Array<Long?>?,
        private val nonNullWithDefaultParam: LongArray,
        private val expectedUri: String,
    ) {

        @Test
        fun buildLongArrayUri() {
            val uri = buildUri()

            assertEquals(expectedUri, uri.toString())
        }

        @Test
        fun buildLongArrayUri_ParsedWithLongArrayUriData() {
            val uriData = LongArrayUriData(buildUri())

            // `null` elements are not added to the Uri
            assertArrayEquals(
                nullableParam?.filterNotNull()?.toTypedArray(),
                uriData.getNullableParam(),
            )
            assertArrayEquals(
                nonNullParam,
                uriData.getNonNullParam(),
            )
            assertArrayEquals(
                nullableWithDefaultParam?.filterNotNull()?.toTypedArray() ?: arrayOf<Long>(1L),
                uriData.getNullableWithDefaultParam(),
            )
            assertArrayEquals(
                nonNullWithDefaultParam,
                uriData.getNonNullWithDefaultParam(),
            )
        }

        private fun buildUri(): Uri = TestArrayUriBuilderImpl().buildLongArrayUri(
            nullableParam,
            nonNullParam,
            nullableWithDefaultParam,
            nonNullWithDefaultParam,
        )

        companion object {
            // the testParams() provider is used by test runner to provide the parameters for the tests
            @Suppress("unused")
            @JvmStatic
            @ParameterizedRobolectricTestRunner.Parameters(name = "{4}")
            fun testParams(): List<Array<Any?>> = listOf(
                // arrayOf(nullableParam, nonNullParam, nullableWithDefaultParam,
                //         nonNullWithDefaultParam, expectedUri),
                arrayOf(
                    arrayOf<Long?>(Long.MAX_VALUE, Long.MIN_VALUE),
                    longArrayOf(3, -4),
                    arrayOf<Long?>(5, -6),
                    longArrayOf(7, -8),
                    "content://com.example.provider.test/path/segment?nullableParam=9223372036854775807&nullableParam=-9223372036854775808&nonNullParam=3&nonNullParam=-4&nullableWithDefaultParam=5&nullableWithDefaultParam=-6&nonNullWithDefaultParam=7&nonNullWithDefaultParam=-8",
                ),
                // `null` arrays with @DefaultValue are replaced with the default value,
                // `null` arrays without @DefaultValue are omitted
                arrayOf(
                    null,
                    longArrayOf(3),
                    null,
                    longArrayOf(7),
                    "content://com.example.provider.test/path/segment?nonNullParam=3&nullableWithDefaultParam=1&nonNullWithDefaultParam=7",
                ),
                // `null` elements of the arrays are omitted
                arrayOf(
                    arrayOf<Long?>(null, Long.MIN_VALUE),
                    longArrayOf(3),
                    arrayOf<Long?>(5, null),
                    longArrayOf(7),
                    "content://com.example.provider.test/path/segment?nullableParam=-9223372036854775808&nonNullParam=3&nullableWithDefaultParam=5&nonNullWithDefaultParam=7",
                ),
            )
        }
    }

    @RunWith(ParameterizedRobolectricTestRunner::class)
    class BuildFloatArrayUriTest(
        private val nullableParam: Array<Float?>?,
        private val nonNullParam: FloatArray,
        private val nullableWithDefaultParam: Array<Float?>?,
        private val nonNullWithDefaultParam: FloatArray,
        private val expectedUri: String,
    ) {

        @Test
        fun buildFloatArrayUri() {
            val uri = buildUri()

            assertEquals(expectedUri, uri.toString())
        }

        @Test
        fun buildFloatArrayUri_ParsedWithFloatArrayUriData() {
            val uriData = FloatArrayUriData(buildUri())

            // `null` elements are not added to the Uri
            assertArrayEquals(
                nullableParam?.filterNotNull()?.toTypedArray(),
                uriData.getNullableParam(),
            )
            assertArrayEquals(
                nonNullParam,
                uriData.getNonNullParam(), 0f,
            )
            assertArrayEquals(
                nullableWithDefaultParam?.filterNotNull()?.toTypedArray() ?: arrayOf<Float>(1.2f),
                uriData.getNullableWithDefaultParam(),
            )
            assertArrayEquals(
                nonNullWithDefaultParam,
                uriData.getNonNullWithDefaultParam(), 0f,
            )
        }

        private fun buildUri(): Uri = TestArrayUriBuilderImpl().buildFloatArrayUri(
            nullableParam,
            nonNullParam,
            nullableWithDefaultParam,
            nonNullWithDefaultParam,
        )

        companion object {
            // the testParams() provider is used by test runner to provide the parameters for the tests
            @Suppress("unused")
            @JvmStatic
            @ParameterizedRobolectricTestRunner.Parameters(name = "{4}")
            fun testParams(): List<Array<Any?>> = listOf(
                // arrayOf(nullableParam, nonNullParam, nullableWithDefaultParam,
                //         nonNullWithDefaultParam, expectedUri),
                arrayOf(
                    arrayOf<Float?>(1.5f, -2.5f),
                    floatArrayOf(3.5f, -4.5f),
                    arrayOf<Float?>(5.5f, -6.5f),
                    floatArrayOf(7.5f, -8.5f),
                    "content://com.example.provider.test/path/segment?nullableParam=1.5&nullableParam=-2.5&nonNullParam=3.5&nonNullParam=-4.5&nullableWithDefaultParam=5.5&nullableWithDefaultParam=-6.5&nonNullWithDefaultParam=7.5&nonNullWithDefaultParam=-8.5",
                ),
                // `null` arrays with @DefaultValue are replaced with the default value,
                // `null` arrays without @DefaultValue are omitted
                arrayOf(
                    null,
                    floatArrayOf(3.5f),
                    null,
                    floatArrayOf(7.5f),
                    "content://com.example.provider.test/path/segment?nonNullParam=3.5&nullableWithDefaultParam=1.2&nonNullWithDefaultParam=7.5",
                ),
                // `null` elements of the arrays are omitted
                arrayOf(
                    arrayOf<Float?>(null, -2.5f),
                    floatArrayOf(3.5f),
                    arrayOf<Float?>(5.5f, null),
                    floatArrayOf(7.5f),
                    "content://com.example.provider.test/path/segment?nullableParam=-2.5&nonNullParam=3.5&nullableWithDefaultParam=5.5&nonNullWithDefaultParam=7.5",
                ),
            )
        }
    }

    @RunWith(ParameterizedRobolectricTestRunner::class)
    class BuildDoubleArrayUriTest(
        private val nullableParam: Array<Double?>?,
        private val nonNullParam: DoubleArray,
        private val nullableWithDefaultParam: Array<Double?>?,
        private val nonNullWithDefaultParam: DoubleArray,
        private val expectedUri: String,
    ) {

        @Test
        fun buildDoubleArrayUri() {
            val uri = buildUri()

            assertEquals(expectedUri, uri.toString())
        }

        @Test
        fun buildDoubleArrayUri_ParsedWithDoubleArrayUriData() {
            val uriData = DoubleArrayUriData(buildUri())

            // `null` elements are not added to the Uri
            assertArrayEquals(
                nullableParam?.filterNotNull()?.toTypedArray(),
                uriData.getNullableParam(),
            )
            assertArrayEquals(
                nonNullParam,
                uriData.getNonNullParam(), 0.0,
            )
            assertArrayEquals(
                nullableWithDefaultParam?.filterNotNull()?.toTypedArray() ?: arrayOf<Double>(1.2),
                uriData.getNullableWithDefaultParam(),
            )
            assertArrayEquals(
                nonNullWithDefaultParam,
                uriData.getNonNullWithDefaultParam(), 0.0,
            )
        }

        private fun buildUri(): Uri = TestArrayUriBuilderImpl().buildDoubleArrayUri(
            nullableParam,
            nonNullParam,
            nullableWithDefaultParam,
            nonNullWithDefaultParam,
        )

        companion object {
            // the testParams() provider is used by test runner to provide the parameters for the tests
            @Suppress("unused")
            @JvmStatic
            @ParameterizedRobolectricTestRunner.Parameters(name = "{4}")
            fun testParams(): List<Array<Any?>> = listOf(
                // arrayOf(nullableParam, nonNullParam, nullableWithDefaultParam,
                //         nonNullWithDefaultParam, expectedUri),
                arrayOf(
                    arrayOf<Double?>(1.5, -2.5),
                    doubleArrayOf(3.5, -4.5),
                    arrayOf<Double?>(5.5, -6.5),
                    doubleArrayOf(7.5, -8.5),
                    "content://com.example.provider.test/path/segment?nullableParam=1.5&nullableParam=-2.5&nonNullParam=3.5&nonNullParam=-4.5&nullableWithDefaultParam=5.5&nullableWithDefaultParam=-6.5&nonNullWithDefaultParam=7.5&nonNullWithDefaultParam=-8.5",
                ),
                // `null` arrays with @DefaultValue are replaced with the default value,
                // `null` arrays without @DefaultValue are omitted
                arrayOf(
                    null,
                    doubleArrayOf(3.5),
                    null,
                    doubleArrayOf(7.5),
                    "content://com.example.provider.test/path/segment?nonNullParam=3.5&nullableWithDefaultParam=1.2&nonNullWithDefaultParam=7.5",
                ),
                // `null` elements of the arrays are omitted
                arrayOf(
                    arrayOf<Double?>(null, -2.5),
                    doubleArrayOf(3.5),
                    arrayOf<Double?>(5.5, null),
                    doubleArrayOf(7.5),
                    "content://com.example.provider.test/path/segment?nullableParam=-2.5&nonNullParam=3.5&nullableWithDefaultParam=5.5&nonNullWithDefaultParam=7.5",
                ),
            )
        }
    }

    @RunWith(RobolectricTestRunner::class)
    class BuildUriBasedArrayUriTest {

        @Test
        fun buildUriBasedArrayUri() {
            val uri = buildUri()

            assertEquals(
                "content://com.example.provider.test/path/segment" +
                    "?param=https%3A%2F%2Fexample.com%2Fa" +
                    "&param=content%3A%2F%2Fauthority%2Fb" +
                    "&paramWithDefault=https%3A%2F%2Fexample.com%2Fc%3Fd%3De",
                uri.toString(),
            )
        }

        @Test
        fun buildUriBasedArrayUri_ParsedWithUriBasedArrayUriData() {
            val uriData = UriBasedArrayUriData(buildUri())

            assertArrayEquals(
                arrayOf(Uri.parse("https://example.com/a"), Uri.parse("content://authority/b")),
                uriData.getParam(),
            )
            assertArrayEquals(
                arrayOf(Uri.parse("https://example.com/c?d=e")),
                uriData.getParamWithDefault(),
            )
        }

        private fun buildUri(): Uri = TestArrayUriBuilderImpl().buildUriBasedArrayUri(
            arrayOf(Uri.parse("https://example.com/a"), Uri.parse("content://authority/b")),
            arrayOf(Uri.parse("https://example.com/c?d=e")),
        )
    }

    @RunWith(RobolectricTestRunner::class)
    class BuildNullableUriBasedArrayUriTest {

        @Test
        fun buildNullableUriBasedArrayUri() {
            val uri = buildUri()

            assertEquals(
                "content://com.example.provider.test/path/segment" +
                    "?param=https%3A%2F%2Fexample.com%2Fa" +
                    "&paramWithDefault=content%3A%2F%2Fauthority%2Fb",
                uri.toString(),
            )
        }

        @Test
        fun buildNullableUriBasedArrayUri_NullValues() {
            val uri = TestArrayUriBuilderImpl().buildNullableUriBasedArrayUri(null, null)

            assertEquals(
                "content://com.example.provider.test/path/segment?paramWithDefault=https%3A%2F%2Fexample.com%2Furi",
                uri.toString(),
            )
        }

        @Test
        fun buildNullableUriBasedArrayUri_ParsedWithNullableUriBasedArrayUriData() {
            val uriData = NullableUriBasedArrayUriData(buildUri())

            assertArrayEquals(
                arrayOf(Uri.parse("https://example.com/a")),
                uriData.getParam(),
            )
            assertArrayEquals(
                arrayOf(Uri.parse("content://authority/b")),
                uriData.getParamWithDefault(),
            )
        }

        @Test
        fun buildNullableUriBasedArrayUri_NullValues_ParsedWithNullableUriBasedArrayUriData() {
            val uri = TestArrayUriBuilderImpl().buildNullableUriBasedArrayUri(null, null)
            val uriData = NullableUriBasedArrayUriData(uri)

            assertNull(uriData.getParam())
            assertArrayEquals(
                arrayOf(Uri.parse("https://example.com/uri")),
                uriData.getParamWithDefault(),
            )
        }

        // `null` elements of the arrays are omitted
        private fun buildUri(): Uri =
            TestArrayUriBuilderImpl().buildNullableUriBasedArrayUri(
                arrayOf(Uri.parse("https://example.com/a"), null),
                arrayOf(null, Uri.parse("content://authority/b")),
            )
    }

    @RunWith(RobolectricTestRunner::class)
    class BuildUserArrayUriTest {

        @Test
        fun buildUserArrayUri() {
            val uri = buildUri()

            assertEquals(
                "content://com.example.provider.test/path/segment?param=1%3BJohn&param=2%3BJane%20Doe&paramWithDefault=3%3BX",
                uri.toString(),
            )
        }

        @Test
        fun buildUserArrayUri_ParsedWithUserArrayUriData() {
            val uriData = UserArrayUriData(buildUri())

            assertEquals(
                listOf(1 to "John", 2 to "Jane Doe"),
                uriData.getParam().map { it.toPair() },
            )
            assertEquals(
                listOf(3 to "X"),
                uriData.getParamWithDefault().map { it.toPair() },
            )
        }

        private fun buildUri(): Uri = TestArrayUriBuilderImpl().buildUserArrayUri(
            arrayOf(User(1, "John"), User(2, "Jane Doe")),
            arrayOf(User(3, "X")),
        )
    }

    @RunWith(RobolectricTestRunner::class)
    class BuildNullableUserArrayUriTest {

        @Test
        fun buildNullableUserArrayUri() {
            val uri = buildUri()

            assertEquals(
                "content://com.example.provider.test/path/segment?param=1%3BJohn&paramWithDefault=3%3BX",
                uri.toString(),
            )
        }

        @Test
        fun buildNullableUserArrayUri_NullValues() {
            val uri = TestArrayUriBuilderImpl().buildNullableUserArrayUri(null, null)

            assertEquals(
                "content://com.example.provider.test/path/segment?paramWithDefault=42%3BJohn%20Doe",
                uri.toString(),
            )
        }

        @Test
        fun buildNullableUserArrayUri_ParsedWithNullableUserArrayUriData() {
            val uriData = NullableUserArrayUriData(buildUri())

            assertEquals(
                listOf(1 to "John"),
                uriData.getParam()?.map { it.toPair() },
            )
            assertEquals(
                listOf(3 to "X"),
                uriData.getParamWithDefault().map { it.toPair() },
            )
        }

        @Test
        fun buildNullableUserArrayUri_NullValues_ParsedWithNullableUserArrayUriData() {
            val uri = TestArrayUriBuilderImpl().buildNullableUserArrayUri(null, null)
            val uriData = NullableUserArrayUriData(uri)

            assertNull(uriData.getParam())
            assertEquals(
                listOf(42 to "John Doe"),
                uriData.getParamWithDefault().map { it.toPair() },
            )
        }

        // `null` elements of the arrays are omitted
        private fun buildUri(): Uri =
            TestArrayUriBuilderImpl().buildNullableUserArrayUri(
                arrayOf(User(1, "John"), null),
                arrayOf(null, User(3, "X")),
            )
    }

    @RunWith(RobolectricTestRunner::class)
    class BuildDoubleDoubleArrayUriTest {

        @Test
        fun buildDoubleDoubleArrayUri() {
            val uri = buildUri()

            assertEquals(
                "content://com.example.provider.test/path/segment" +
                    "?nullableParam=1.5%3B-2.5" +
                    "&nonNullParam=3.5" +
                    "&nonNullParam=4.5%3B5.5" +
                    "&nullableWithDefaultParam=6.5" +
                    "&nonNullWithDefaultParam=7.5%3B8.5",
                uri.toString(),
            )
        }

        @Test
        fun buildDoubleDoubleArrayUri_NullValues() {
            val uri = TestArrayUriBuilderImpl().buildDoubleDoubleArrayUri(
                null,
                arrayOf(doubleArrayOf(3.5)),
                null,
                arrayOf(doubleArrayOf(7.5)),
            )

            assertEquals(
                "content://com.example.provider.test/path/segment" +
                    "?nonNullParam=3.5" +
                    "&nullableWithDefaultParam=1.2%3B2.3" +
                    "&nonNullWithDefaultParam=7.5",
                uri.toString(),
            )
        }

        @Test
        fun buildDoubleDoubleArrayUri_ParsedWithDoubleDoubleArrayUriData() {
            val uriData = DoubleDoubleArrayUriData(buildUri())

            assertArrayEquals(
                arrayOf(doubleArrayOf(1.5, -2.5)),
                uriData.getNullableParam(),
            )
            assertArrayEquals(
                arrayOf(doubleArrayOf(3.5), doubleArrayOf(4.5, 5.5)),
                uriData.getNonNullParam(),
            )
            assertArrayEquals(
                arrayOf(doubleArrayOf(6.5)),
                uriData.getNullableWithDefaultParam(),
            )
            assertArrayEquals(
                arrayOf(doubleArrayOf(7.5, 8.5)),
                uriData.getNonNullWithDefaultParam(),
            )
        }

        @Test
        fun buildDoubleDoubleArrayUri_NullValues_ParsedWithDoubleDoubleArrayUriData() {
            val uri = TestArrayUriBuilderImpl().buildDoubleDoubleArrayUri(
                null,
                arrayOf(doubleArrayOf(3.5)),
                null,
                arrayOf(doubleArrayOf(7.5)),
            )
            val uriData = DoubleDoubleArrayUriData(uri)

            assertNull(uriData.getNullableParam())
            assertArrayEquals(
                arrayOf(doubleArrayOf(1.2, 2.3)),
                uriData.getNullableWithDefaultParam(),
            )
        }

        private fun buildUri(): Uri = TestArrayUriBuilderImpl().buildDoubleDoubleArrayUri(
            arrayOf(doubleArrayOf(1.5, -2.5)),
            arrayOf(doubleArrayOf(3.5), doubleArrayOf(4.5, 5.5)),
            arrayOf(doubleArrayOf(6.5)),
            arrayOf(doubleArrayOf(7.5, 8.5)),
        )
    }
}

/**
 * [User] doesn't implement `equals()`, so it is compared by its properties.
 */
private fun User.toPair(): Pair<Int, String> = id to name
