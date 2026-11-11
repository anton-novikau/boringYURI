package boringyuri.sample.test

import android.net.Uri
import boringyuri.sample.data.User
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.RobolectricTestRunner

@RunWith(Enclosed::class)
class TestUriBuilderImplTest {
    @RunWith(ParameterizedRobolectricTestRunner::class)
    class BuildBooleanUriTest(
        private val nullableSegment: Boolean?,
        private val nonNullSegment: Boolean,
        private val nullableParam: Boolean?,
        private val nonNullParam: Boolean,
        private val nullableWithDefaultParam: Boolean?,
        private val nonNullWithDefaultParam: Boolean,
        private val expectedUri: String,
    ) {

        @Test
        fun buildBooleanUri() {
            val uri = buildUri()

            assertEquals(expectedUri, uri.toString())
        }

        @Test
        fun buildBooleanUri_ParsedWithBooleanUriData() {
            val uriData = BooleanUriData(buildUri())

            assertEquals(nullableSegment ?: true, uriData.isNullableSegment())
            assertEquals(nonNullSegment, uriData.isNonNullSegment())
            assertEquals(nullableParam, uriData.isNullableParam())
            assertEquals(nonNullParam, uriData.isNonNullParam())
            assertEquals(nullableWithDefaultParam ?: true, uriData.isNullableWithDefaultParam())
            assertEquals(nonNullWithDefaultParam, uriData.isNonNullWithDefaultParam())
        }

        private fun buildUri(): Uri = TestUriBuilderImpl().buildBooleanUri(
            nullableSegment,
            nonNullSegment,
            nullableParam,
            nonNullParam,
            nullableWithDefaultParam,
            nonNullWithDefaultParam,
        )

        companion object {
            // the testParams() provider is used by test runner to provide the parameters for the tests
            @Suppress("unused")
            @JvmStatic
            @ParameterizedRobolectricTestRunner.Parameters(name = "{6}")
            fun testParams(): List<Array<Any?>> = listOf(
                // arrayOf(nullableSegment, nonNullSegment, nullableParam, nonNullParam,
                //         nullableWithDefaultParam, nonNullWithDefaultParam, expectedUri),
                arrayOf(
                    false,
                    true,
                    false,
                    true,
                    false,
                    true,
                    "content://com.example.provider.test/path/segment/false/true?nullableParam=false&nonNullParam=true&nullableWithDefaultParam=false&nonNullWithDefaultParam=true",
                ),
                // `null` values of nullable segments and params with @DefaultValue are replaced
                // with the default value, `null` params without @DefaultValue are omitted.
                arrayOf(
                    null,
                    false,
                    null,
                    false,
                    null,
                    false,
                    "content://com.example.provider.test/path/segment/true/false?nonNullParam=false&nullableWithDefaultParam=true&nonNullWithDefaultParam=false",
                ),
            )
        }
    }

    @RunWith(ParameterizedRobolectricTestRunner::class)
    class BuildCharUriTest(
        private val nullableSegment: Char?,
        private val nonNullSegment: Char,
        private val nullableParam: Char?,
        private val nonNullParam: Char,
        private val nullableWithDefaultParam: Char?,
        private val nonNullWithDefaultParam: Char,
        private val expectedUri: String,
    ) {

        @Test
        fun buildCharUri() {
            val uri = buildUri()

            assertEquals(expectedUri, uri.toString())
        }

        @Test
        fun buildCharUri_ParsedWithCharUriData() {
            val uriData = CharUriData(buildUri())

            assertEquals(nullableSegment ?: 'c', uriData.getNullableSegment())
            assertEquals(nonNullSegment, uriData.getNonNullSegment())
            assertEquals(nullableParam, uriData.getNullableParam())
            assertEquals(nonNullParam, uriData.getNonNullParam())
            assertEquals(nullableWithDefaultParam ?: 'c', uriData.getNullableWithDefaultParam())
            assertEquals(nonNullWithDefaultParam, uriData.getNonNullWithDefaultParam())
        }

        private fun buildUri(): Uri = TestUriBuilderImpl().buildCharUri(
            nullableSegment,
            nonNullSegment,
            nullableParam,
            nonNullParam,
            nullableWithDefaultParam,
            nonNullWithDefaultParam,
        )

        companion object {
            // the testParams() provider is used by test runner to provide the parameters for the tests
            @Suppress("unused")
            @JvmStatic
            @ParameterizedRobolectricTestRunner.Parameters(name = "{6}")
            fun testParams(): List<Array<Any?>> = listOf(
                // arrayOf(nullableSegment, nonNullSegment, nullableParam, nonNullParam,
                //         nullableWithDefaultParam, nonNullWithDefaultParam, expectedUri),
                arrayOf(
                    'a',
                    '/',
                    ' ',
                    '&',
                    'z',
                    'é',
                    "content://com.example.provider.test/path/segment/a/%2F?nullableParam=%20&nonNullParam=%26&nullableWithDefaultParam=z&nonNullWithDefaultParam=%C3%A9",
                ),
                // `null` values of nullable segments and params with @DefaultValue are replaced
                // with the default value, `null` params without @DefaultValue are omitted.
                arrayOf(
                    null,
                    'b',
                    null,
                    'd',
                    null,
                    'f',
                    "content://com.example.provider.test/path/segment/c/b?nonNullParam=d&nullableWithDefaultParam=c&nonNullWithDefaultParam=f",
                ),
            )
        }
    }

    @RunWith(ParameterizedRobolectricTestRunner::class)
    class BuildByteUriTest(
        private val nullableSegment: Byte?,
        private val nonNullSegment: Byte,
        private val nullableParam: Byte?,
        private val nonNullParam: Byte,
        private val nullableWithDefaultParam: Byte?,
        private val nonNullWithDefaultParam: Byte,
        private val expectedUri: String,
    ) {

        @Test
        fun buildByteUri() {
            val uri = buildUri()

            assertEquals(expectedUri, uri.toString())
        }

        @Test
        fun buildByteUri_ParsedWithByteUriData() {
            val uriData = ByteUriData(buildUri())

            assertEquals(nullableSegment ?: 1.toByte(), uriData.getNullableSegment())
            assertEquals(nonNullSegment, uriData.getNonNullSegment())
            assertEquals(nullableParam, uriData.getNullableParam())
            assertEquals(nonNullParam, uriData.getNonNullParam())
            assertEquals(nullableWithDefaultParam ?: 1.toByte(), uriData.getNullableWithDefaultParam())
            assertEquals(nonNullWithDefaultParam, uriData.getNonNullWithDefaultParam())
        }

        private fun buildUri(): Uri = TestUriBuilderImpl().buildByteUri(
            nullableSegment,
            nonNullSegment,
            nullableParam,
            nonNullParam,
            nullableWithDefaultParam,
            nonNullWithDefaultParam,
        )

        companion object {
            // the testParams() provider is used by test runner to provide the parameters for the tests
            @Suppress("unused")
            @JvmStatic
            @ParameterizedRobolectricTestRunner.Parameters(name = "{6}")
            fun testParams(): List<Array<Any?>> = listOf(
                // arrayOf(nullableSegment, nonNullSegment, nullableParam, nonNullParam,
                //         nullableWithDefaultParam, nonNullWithDefaultParam, expectedUri),
                arrayOf(
                    Byte.MAX_VALUE,
                    Byte.MIN_VALUE,
                    3.toByte(),
                    4.toByte(),
                    5.toByte(),
                    6.toByte(),
                    "content://com.example.provider.test/path/segment/127/-128?nullableParam=3&nonNullParam=4&nullableWithDefaultParam=5&nonNullWithDefaultParam=6",
                ),
                // `null` values of nullable segments and params with @DefaultValue are replaced
                // with the default value, `null` params without @DefaultValue are omitted.
                arrayOf(
                    null,
                    (-2).toByte(),
                    null,
                    (-4).toByte(),
                    null,
                    (-6).toByte(),
                    "content://com.example.provider.test/path/segment/1/-2?nonNullParam=-4&nullableWithDefaultParam=1&nonNullWithDefaultParam=-6",
                ),
            )
        }
    }

    @RunWith(ParameterizedRobolectricTestRunner::class)
    class BuildShortUriTest(
        private val nullableSegment: Short?,
        private val nonNullSegment: Short,
        private val nullableParam: Short?,
        private val nonNullParam: Short,
        private val nullableWithDefaultParam: Short?,
        private val nonNullWithDefaultParam: Short,
        private val expectedUri: String,
    ) {

        @Test
        fun buildShortUri() {
            val uri = buildUri()

            assertEquals(expectedUri, uri.toString())
        }

        @Test
        fun buildShortUri_ParsedWithShortUriData() {
            val uriData = ShortUriData(buildUri())

            assertEquals(nullableSegment ?: 1.toShort(), uriData.getNullableSegment())
            assertEquals(nonNullSegment, uriData.getNonNullSegment())
            assertEquals(nullableParam, uriData.getNullableParam())
            assertEquals(nonNullParam, uriData.getNonNullParam())
            assertEquals(nullableWithDefaultParam ?: 1.toShort(), uriData.getNullableWithDefaultParam())
            assertEquals(nonNullWithDefaultParam, uriData.getNonNullWithDefaultParam())
        }

        private fun buildUri(): Uri = TestUriBuilderImpl().buildShortUri(
            nullableSegment,
            nonNullSegment,
            nullableParam,
            nonNullParam,
            nullableWithDefaultParam,
            nonNullWithDefaultParam,
        )

        companion object {
            // the testParams() provider is used by test runner to provide the parameters for the tests
            @Suppress("unused")
            @JvmStatic
            @ParameterizedRobolectricTestRunner.Parameters(name = "{6}")
            fun testParams(): List<Array<Any?>> = listOf(
                // arrayOf(nullableSegment, nonNullSegment, nullableParam, nonNullParam,
                //         nullableWithDefaultParam, nonNullWithDefaultParam, expectedUri),
                arrayOf(
                    Short.MAX_VALUE,
                    Short.MIN_VALUE,
                    3.toShort(),
                    4.toShort(),
                    5.toShort(),
                    6.toShort(),
                    "content://com.example.provider.test/path/segment/32767/-32768?nullableParam=3&nonNullParam=4&nullableWithDefaultParam=5&nonNullWithDefaultParam=6",
                ),
                // `null` values of nullable segments and params with @DefaultValue are replaced
                // with the default value, `null` params without @DefaultValue are omitted.
                arrayOf(
                    null,
                    (-2).toShort(),
                    null,
                    (-4).toShort(),
                    null,
                    (-6).toShort(),
                    "content://com.example.provider.test/path/segment/1/-2?nonNullParam=-4&nullableWithDefaultParam=1&nonNullWithDefaultParam=-6",
                ),
            )
        }
    }

    @RunWith(ParameterizedRobolectricTestRunner::class)
    class BuildIntUriTest(
        private val nullableSegment: Int?,
        private val nonNullSegment: Int,
        private val nullableParam: Int?,
        private val nonNullParam: Int,
        private val nullableWithDefaultParam: Int?,
        private val nonNullWithDefaultParam: Int,
        private val expectedUri: String,
    ) {

        @Test
        fun buildIntUri() {
            val uri = buildUri()

            assertEquals(expectedUri, uri.toString())
        }

        @Test
        fun buildIntUri_ParsedWithIntUriData() {
            val uriData = IntUriData(buildUri())

            assertEquals(nullableSegment ?: 1, uriData.getNullableSegment())
            assertEquals(nonNullSegment, uriData.getNonNullSegment())
            assertEquals(nullableParam, uriData.getNullableParam())
            assertEquals(nonNullParam, uriData.getNonNullParam())
            assertEquals(nullableWithDefaultParam ?: 1, uriData.getNullableWithDefaultParam())
            assertEquals(nonNullWithDefaultParam, uriData.getNonNullWithDefaultParam())
        }

        private fun buildUri(): Uri = TestUriBuilderImpl().buildIntUri(
            nullableSegment,
            nonNullSegment,
            nullableParam,
            nonNullParam,
            nullableWithDefaultParam,
            nonNullWithDefaultParam,
        )

        companion object {
            // the testParams() provider is used by test runner to provide the parameters for the tests
            @Suppress("unused")
            @JvmStatic
            @ParameterizedRobolectricTestRunner.Parameters(name = "{6}")
            fun testParams(): List<Array<Any?>> = listOf(
                // arrayOf(nullableSegment, nonNullSegment, nullableParam, nonNullParam,
                //         nullableWithDefaultParam, nonNullWithDefaultParam, expectedUri),
                arrayOf(
                    Int.MAX_VALUE,
                    Int.MIN_VALUE,
                    3,
                    4,
                    5,
                    6,
                    "content://com.example.provider.test/path/segment/2147483647/-2147483648?nullableParam=3&nonNullParam=4&nullableWithDefaultParam=5&nonNullWithDefaultParam=6",
                ),
                // `null` values of nullable segments and params with @DefaultValue are replaced
                // with the default value, `null` params without @DefaultValue are omitted.
                arrayOf(
                    null,
                    -2,
                    null,
                    -4,
                    null,
                    -6,
                    "content://com.example.provider.test/path/segment/1/-2?nonNullParam=-4&nullableWithDefaultParam=1&nonNullWithDefaultParam=-6",
                ),
            )
        }
    }

    @RunWith(ParameterizedRobolectricTestRunner::class)
    class BuildLongUriTest(
        private val nullableSegment: Long?,
        private val nonNullSegment: Long,
        private val nullableParam: Long?,
        private val nonNullParam: Long,
        private val nullableWithDefaultParam: Long?,
        private val nonNullWithDefaultParam: Long,
        private val expectedUri: String,
    ) {

        @Test
        fun buildLongUri() {
            val uri = buildUri()

            assertEquals(expectedUri, uri.toString())
        }

        @Test
        fun buildLongUri_ParsedWithLongUriData() {
            val uriData = LongUriData(buildUri())

            assertEquals(nullableSegment ?: 1L, uriData.getNullableSegment())
            assertEquals(nonNullSegment, uriData.getNonNullSegment())
            assertEquals(nullableParam, uriData.getNullableParam())
            assertEquals(nonNullParam, uriData.getNonNullParam())
            assertEquals(nullableWithDefaultParam ?: 1L, uriData.getNullableWithDefaultParam())
            assertEquals(nonNullWithDefaultParam, uriData.getNonNullWithDefaultParam())
        }

        private fun buildUri(): Uri = TestUriBuilderImpl().buildLongUri(
            nullableSegment,
            nonNullSegment,
            nullableParam,
            nonNullParam,
            nullableWithDefaultParam,
            nonNullWithDefaultParam,
        )

        companion object {
            // the testParams() provider is used by test runner to provide the parameters for the tests
            @Suppress("unused")
            @JvmStatic
            @ParameterizedRobolectricTestRunner.Parameters(name = "{6}")
            fun testParams(): List<Array<Any?>> = listOf(
                // arrayOf(nullableSegment, nonNullSegment, nullableParam, nonNullParam,
                //         nullableWithDefaultParam, nonNullWithDefaultParam, expectedUri),
                arrayOf(
                    Long.MAX_VALUE,
                    Long.MIN_VALUE,
                    3L,
                    4L,
                    5L,
                    6L,
                    "content://com.example.provider.test/path/segment/9223372036854775807/-9223372036854775808?nullableParam=3&nonNullParam=4&nullableWithDefaultParam=5&nonNullWithDefaultParam=6",
                ),
                // `null` values of nullable segments and params with @DefaultValue are replaced
                // with the default value, `null` params without @DefaultValue are omitted.
                arrayOf(
                    null,
                    -2L,
                    null,
                    -4L,
                    null,
                    -6L,
                    "content://com.example.provider.test/path/segment/1/-2?nonNullParam=-4&nullableWithDefaultParam=1&nonNullWithDefaultParam=-6",
                ),
            )
        }
    }

    @RunWith(ParameterizedRobolectricTestRunner::class)
    class BuildFloatUriTest(
        private val nullableSegment: Float?,
        private val nonNullSegment: Float,
        private val nullableParam: Float?,
        private val nonNullParam: Float,
        private val nullableWithDefaultParam: Float?,
        private val nonNullWithDefaultParam: Float,
        private val expectedUri: String,
    ) {

        @Test
        fun buildFloatUri() {
            val uri = buildUri()

            assertEquals(expectedUri, uri.toString())
        }

        @Test
        fun buildFloatUri_ParsedWithFloatUriData() {
            val uriData = FloatUriData(buildUri())

            assertEquals(nullableSegment ?: 1.2f, uriData.getNullableSegment(), 0f)
            assertEquals(nonNullSegment, uriData.getNonNullSegment(), 0f)
            assertEquals(nullableParam, uriData.getNullableParam())
            assertEquals(nonNullParam, uriData.getNonNullParam(), 0f)
            assertEquals(nullableWithDefaultParam ?: 1.2f, uriData.getNullableWithDefaultParam(), 0f)
            assertEquals(nonNullWithDefaultParam, uriData.getNonNullWithDefaultParam(), 0f)
        }

        private fun buildUri(): Uri = TestUriBuilderImpl().buildFloatUri(
            nullableSegment,
            nonNullSegment,
            nullableParam,
            nonNullParam,
            nullableWithDefaultParam,
            nonNullWithDefaultParam,
        )

        companion object {
            // the testParams() provider is used by test runner to provide the parameters for the tests
            @Suppress("unused")
            @JvmStatic
            @ParameterizedRobolectricTestRunner.Parameters(name = "{6}")
            fun testParams(): List<Array<Any?>> = listOf(
                // arrayOf(nullableSegment, nonNullSegment, nullableParam, nonNullParam,
                //         nullableWithDefaultParam, nonNullWithDefaultParam, expectedUri),
                arrayOf(
                    1.5f,
                    -2.5f,
                    3.5f,
                    4.5f,
                    5.5f,
                    6.5f,
                    "content://com.example.provider.test/path/segment/1.5/-2.5?nullableParam=3.5&nonNullParam=4.5&nullableWithDefaultParam=5.5&nonNullWithDefaultParam=6.5",
                ),
                // `null` values of nullable segments and params with @DefaultValue are replaced
                // with the default value, `null` params without @DefaultValue are omitted.
                arrayOf(
                    null,
                    -2.5f,
                    null,
                    -4.5f,
                    null,
                    -6.5f,
                    "content://com.example.provider.test/path/segment/1.2/-2.5?nonNullParam=-4.5&nullableWithDefaultParam=1.2&nonNullWithDefaultParam=-6.5",
                ),
            )
        }
    }

    @RunWith(ParameterizedRobolectricTestRunner::class)
    class BuildDoubleUriTest(
        private val nullableSegment: Double?,
        private val nonNullSegment: Double,
        private val nullableParam: Double?,
        private val nonNullParam: Double,
        private val nullableWithDefaultParam: Double?,
        private val nonNullWithDefaultParam: Double,
        private val expectedUri: String,
    ) {

        @Test
        fun buildDoubleUri() {
            val uri = buildUri()

            assertEquals(expectedUri, uri.toString())
        }

        @Test
        fun buildDoubleUri_ParsedWithDoubleUriData() {
            val uriData = DoubleUriData(buildUri())

            assertEquals(nullableSegment ?: 1.2, uriData.getNullableSegment(), 0.0)
            assertEquals(nonNullSegment, uriData.getNonNullSegment(), 0.0)
            assertEquals(nullableParam, uriData.getNullableParam())
            assertEquals(nonNullParam, uriData.getNonNullParam(), 0.0)
            assertEquals(nullableWithDefaultParam ?: 1.2, uriData.getNullableWithDefaultParam(), 0.0)
            assertEquals(nonNullWithDefaultParam, uriData.getNonNullWithDefaultParam(), 0.0)
        }

        private fun buildUri(): Uri = TestUriBuilderImpl().buildDoubleUri(
            nullableSegment,
            nonNullSegment,
            nullableParam,
            nonNullParam,
            nullableWithDefaultParam,
            nonNullWithDefaultParam,
        )

        companion object {
            // the testParams() provider is used by test runner to provide the parameters for the tests
            @Suppress("unused")
            @JvmStatic
            @ParameterizedRobolectricTestRunner.Parameters(name = "{6}")
            fun testParams(): List<Array<Any?>> = listOf(
                // arrayOf(nullableSegment, nonNullSegment, nullableParam, nonNullParam,
                //         nullableWithDefaultParam, nonNullWithDefaultParam, expectedUri),
                arrayOf(
                    1.5,
                    -2.5,
                    3.5,
                    4.5,
                    5.5,
                    6.5,
                    "content://com.example.provider.test/path/segment/1.5/-2.5?nullableParam=3.5&nonNullParam=4.5&nullableWithDefaultParam=5.5&nonNullWithDefaultParam=6.5",
                ),
                // `null` values of nullable segments and params with @DefaultValue are replaced
                // with the default value, `null` params without @DefaultValue are omitted.
                arrayOf(
                    null,
                    -2.5,
                    null,
                    -4.5,
                    null,
                    -6.5,
                    "content://com.example.provider.test/path/segment/1.2/-2.5?nonNullParam=-4.5&nullableWithDefaultParam=1.2&nonNullWithDefaultParam=-6.5",
                ),
            )
        }
    }

    @RunWith(ParameterizedRobolectricTestRunner::class)
    class BuildStringUriTest(
        private val nullableSegment: String?,
        private val nonNullSegment: String,
        private val nullableParam: String?,
        private val nonNullParam: String,
        private val nullableWithDefaultParam: String?,
        private val nonNullWithDefaultParam: String,
        private val expectedUri: String,
    ) {

        @Test
        fun buildStringUri() {
            val uri = buildUri()

            assertEquals(expectedUri, uri.toString())
        }

        @Test
        fun buildStringUri_ParsedWithStringUriData() {
            val uriData = StringUriData(buildUri())

            assertEquals(nullableSegment ?: "default", uriData.getNullableSegment())
            assertEquals(nonNullSegment, uriData.getNonNullSegment())
            assertEquals(nullableParam, uriData.getNullableParam())
            assertEquals(nonNullParam, uriData.getNonNullParam())
            assertEquals(nullableWithDefaultParam ?: "default", uriData.getNullableWithDefaultParam())
            assertEquals(nonNullWithDefaultParam, uriData.getNonNullWithDefaultParam())
        }

        private fun buildUri(): Uri = TestUriBuilderImpl().buildStringUri(
            nullableSegment,
            nonNullSegment,
            nullableParam,
            nonNullParam,
            nullableWithDefaultParam,
            nonNullWithDefaultParam,
        )

        companion object {
            // the testParams() provider is used by test runner to provide the parameters for the tests
            @Suppress("unused")
            @JvmStatic
            @ParameterizedRobolectricTestRunner.Parameters(name = "{6}")
            fun testParams(): List<Array<Any?>> = listOf(
                // arrayOf(nullableSegment, nonNullSegment, nullableParam, nonNullParam,
                //         nullableWithDefaultParam, nonNullWithDefaultParam, expectedUri),
                arrayOf(
                    "a b",
                    "c/d",
                    "e&f",
                    "g=h",
                    "i?j",
                    "été",
                    "content://com.example.provider.test/path/segment/a%20b/c%2Fd?nullableParam=e%26f&nonNullParam=g%3Dh&nullableWithDefaultParam=i%3Fj&nonNullWithDefaultParam=%C3%A9t%C3%A9",
                ),
                // `null` values of nullable segments and params with @DefaultValue are replaced
                // with the default value, `null` params without @DefaultValue are omitted.
                arrayOf(
                    null,
                    "b",
                    null,
                    "d",
                    null,
                    "f",
                    "content://com.example.provider.test/path/segment/default/b?nonNullParam=d&nullableWithDefaultParam=default&nonNullWithDefaultParam=f",
                ),
            )
        }
    }

    @RunWith(RobolectricTestRunner::class)
    class BuildUriBasedUriTest {

        @Test
        fun buildUriBasedUri() {
            val uri = buildUri()

            assertEquals(
                "content://com.example.provider.test/path/segment/https%3A%2F%2Fexample.com%2Fa" +
                    "?param=content%3A%2F%2Fauthority%2Fb" +
                    "&paramWithDefault=https%3A%2F%2Fexample.com%2Fc%3Fd%3De",
                uri.toString(),
            )
        }

        @Test
        fun buildUriBasedUri_ParsedWithUriBasedUriData() {
            val uriData = UriBasedUriData(buildUri())

            assertEquals(Uri.parse("https://example.com/a"), uriData.getUri())
            assertEquals(Uri.parse("content://authority/b"), uriData.getParam())
            assertEquals(Uri.parse("https://example.com/c?d=e"), uriData.getParamWithDefault())
        }

        private fun buildUri(): Uri = TestUriBuilderImpl().buildUriBasedUri(
            Uri.parse("https://example.com/a"),
            Uri.parse("content://authority/b"),
            Uri.parse("https://example.com/c?d=e"),
        )
    }

    @RunWith(RobolectricTestRunner::class)
    class BuildNullableUriBasedUriTest {

        @Test
        fun buildNullableUriBasedUri() {
            val uri = TestUriBuilderImpl().buildNullableUriBasedUri(
                Uri.parse("https://example.com/a"),
                Uri.parse("content://authority/b"),
                Uri.parse("https://example.com/c"),
            )

            assertEquals(
                "content://com.example.provider.test/path/segment/https%3A%2F%2Fexample.com%2Fa" +
                    "?param=content%3A%2F%2Fauthority%2Fb" +
                    "&paramWithDefault=https%3A%2F%2Fexample.com%2Fc",
                uri.toString(),
            )
        }

        @Test
        fun buildNullableUriBasedUri_NullValues() {
            val uri = TestUriBuilderImpl().buildNullableUriBasedUri(null, null, null)

            assertEquals(
                "content://com.example.provider.test/path/segment/https%3A%2F%2Fexample.com%2Furi" +
                    "?paramWithDefault=https%3A%2F%2Fexample.com%2Furi",
                uri.toString(),
            )
        }

        @Test
        fun buildNullableUriBasedUri_ParsedWithNullableUriBasedUriData() {
            val uri = TestUriBuilderImpl().buildNullableUriBasedUri(
                Uri.parse("https://example.com/a"),
                Uri.parse("content://authority/b"),
                Uri.parse("https://example.com/c"),
            )
            val uriData = NullableUriBasedUriData(uri)

            assertEquals(Uri.parse("https://example.com/a"), uriData.getUri())
            assertEquals(Uri.parse("content://authority/b"), uriData.getParam())
            assertEquals(Uri.parse("https://example.com/c"), uriData.getParamWithDefault())
        }

        @Test
        fun buildNullableUriBasedUri_NullValues_ParsedWithNullableUriBasedUriData() {
            val uri = TestUriBuilderImpl().buildNullableUriBasedUri(null, null, null)
            val uriData = NullableUriBasedUriData(uri)

            assertEquals(Uri.parse("https://example.com/uri"), uriData.getUri())
            assertNull(uriData.getParam())
            assertEquals(Uri.parse("https://example.com/uri"), uriData.getParamWithDefault())
        }
    }

    @RunWith(RobolectricTestRunner::class)
    class BuildUserUriTest {

        @Test
        fun buildUserUri() {
            val uri = buildUri()

            assertEquals(
                "content://com.example.provider.test/path/segment/1%3BJohn?param=2%3BJane%20Doe&paramWithDefault=3%3BX",
                uri.toString(),
            )
        }

        @Test
        fun buildUserUri_ParsedWithUserUriData() {
            val uriData = UserUriData(buildUri())

            assertEquals(1 to "John", uriData.getUser().toPair())
            assertEquals(2 to "Jane Doe", uriData.getParam().toPair())
            assertEquals(3 to "X", uriData.getParamWithDefault().toPair())
        }

        private fun buildUri(): Uri = TestUriBuilderImpl().buildUserUri(
            User(1, "John"),
            User(2, "Jane Doe"),
            User(3, "X"),
        )
    }

    @RunWith(RobolectricTestRunner::class)
    class BuildNullableUserUriTest {

        @Test
        fun buildNullableUserUri() {
            val uri = TestUriBuilderImpl().buildNullableUserUri(
                User(1, "John"),
                User(2, "Jane Doe"),
                User(3, "X"),
            )

            assertEquals(
                "content://com.example.provider.test/path/segment/1%3BJohn?param=2%3BJane%20Doe&paramWithDefault=3%3BX",
                uri.toString(),
            )
        }

        @Test
        fun buildNullableUserUri_NullValues() {
            val uri = TestUriBuilderImpl().buildNullableUserUri(null, null, null)

            assertEquals(
                "content://com.example.provider.test/path/segment/42%3BJohn%20Doe?paramWithDefault=42%3BJohn%20Doe",
                uri.toString(),
            )
        }

        @Test
        fun buildNullableUserUri_ParsedWithNullableUserUriData() {
            val uri = TestUriBuilderImpl().buildNullableUserUri(
                User(1, "John"),
                User(2, "Jane Doe"),
                User(3, "X"),
            )
            val uriData = NullableUserUriData(uri)

            assertEquals(1 to "John", uriData.getUser().toPair())
            assertEquals(2 to "Jane Doe", uriData.getParam()?.toPair())
            assertEquals(3 to "X", uriData.getParamWithDefault().toPair())
        }

        @Test
        fun buildNullableUserUri_NullValues_ParsedWithNullableUserUriData() {
            val uri = TestUriBuilderImpl().buildNullableUserUri(null, null, null)
            val uriData = NullableUserUriData(uri)

            assertEquals(42 to "John Doe", uriData.getUser().toPair())
            assertNull(uriData.getParam())
            assertEquals(42 to "John Doe", uriData.getParamWithDefault().toPair())
        }
    }
}

/**
 * [User] doesn't implement `equals()`, so it is compared by its properties.
 */
private fun User.toPair(): Pair<Int, String> = id to name
