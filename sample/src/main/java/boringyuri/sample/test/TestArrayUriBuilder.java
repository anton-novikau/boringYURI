/*
 * Copyright 2020 Anton Novikau
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

package boringyuri.sample.test;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import boringyuri.api.DefaultValue;
import boringyuri.api.Param;
import boringyuri.api.UriBuilder;
import boringyuri.api.UriFactory;
import boringyuri.api.WithUriData;
import boringyuri.api.adapter.TypeAdapter;
import boringyuri.sample.data.User;
import boringyuri.sample.data.adapter.DoubleArrayTypeAdapter;

@UriFactory(scheme = "content", authority = "com.example.provider.test")
public interface TestArrayUriBuilder {

    class Contract {
        static final String DEFAULT_STRING = "default";
        static final String DEFAULT_URI = "https://example.com/uri";
        static final String DEFAULT_USER = "42;John Doe";
        static final String DEFAULT_BOOLEAN = "true";
        static final String DEFAULT_CHAR = "c";
        static final String DEFAULT_NUMBER = "1";
        static final String DEFAULT_FLOAT_NUMBER = "1.2";
        static final String DEFAULT_FLOAT_ARRAY = "1.2;2.3";
    }

    @NonNull
    @UriBuilder("/path/segment")
    @WithUriData
    Uri buildStringArrayUri(
            @Nullable @Param String[] nullableParam,
            @NonNull @Param String[] nonNullParam,
            @Nullable @Param @DefaultValue(Contract.DEFAULT_STRING) String[] nullableWithDefaultParam,
            @NonNull @Param @DefaultValue(Contract.DEFAULT_STRING) String[] nonNullWithDefaultParam);

    @NonNull
    @UriBuilder("/path/segment")
    @WithUriData
    Uri buildUriBasedArrayUri(
            @NonNull @Param Uri[] param,
            @NonNull @Param @DefaultValue(Contract.DEFAULT_URI) Uri[] paramWithDefault);

    @NonNull
    @UriBuilder("/path/segment")
    @WithUriData
    Uri buildNullableUriBasedArrayUri(
            @Nullable @Param Uri[] param,
            @Nullable @Param @DefaultValue(Contract.DEFAULT_URI) Uri[] paramWithDefault);

    @NonNull
    @UriBuilder("/path/segment")
    @WithUriData
    Uri buildBooleanArrayUri(
            @Nullable @Param Boolean[] nullableParam,
            @NonNull @Param boolean[] nonNullParam,
            @Nullable @Param @DefaultValue(Contract.DEFAULT_BOOLEAN) Boolean[] nullableWithDefaultParam,
            @NonNull @Param @DefaultValue(Contract.DEFAULT_BOOLEAN) boolean[] nonNullWithDefaultParam);

    @NonNull
    @UriBuilder("/path/segment")
    @WithUriData
    Uri buildCharArrayUri(
            @Nullable @Param Character[] nullableParam,
            @NonNull @Param char[] nonNullParam,
            @Nullable @Param @DefaultValue(Contract.DEFAULT_CHAR) Character[] nullableWithDefaultParam,
            @Param @DefaultValue(Contract.DEFAULT_CHAR) char[] nonNullWithDefaultParam);

    @NonNull
    @UriBuilder("/path/segment")
    @WithUriData
    Uri buildByteArrayUri(
            @Nullable @Param Byte[] nullableParam,
            @NonNull @Param byte[] nonNullParam,
            @Nullable @Param @DefaultValue(Contract.DEFAULT_NUMBER) Byte[] nullableWithDefaultParam,
            @NonNull @Param @DefaultValue(Contract.DEFAULT_NUMBER) byte[] nonNullWithDefaultParam);

    @NonNull
    @UriBuilder("/path/segment")
    @WithUriData
    Uri buildShortArrayUri(
            @Nullable @Param Short[] nullableParam,
            @NonNull @Param short[] nonNullParam,
            @Nullable @Param @DefaultValue(Contract.DEFAULT_NUMBER) Short[] nullableWithDefaultParam,
            @NonNull @Param @DefaultValue(Contract.DEFAULT_NUMBER) short[] nonNullWithDefaultParam);

    @NonNull
    @UriBuilder("/path/segment")
    @WithUriData
    Uri buildIntArrayUri(
            @Nullable @Param Integer[] nullableParam,
            @NonNull @Param int[] nonNullParam,
            @Nullable @Param @DefaultValue(Contract.DEFAULT_NUMBER) Integer[] nullableWithDefaultParam,
            @NonNull @Param @DefaultValue(Contract.DEFAULT_NUMBER) int[] nonNullWithDefaultParam);

    @NonNull
    @UriBuilder("/path/segment")
    @WithUriData
    Uri buildLongArrayUri(
            @Nullable @Param Long[] nullableParam,
            @NonNull @Param long[] nonNullParam,
            @Nullable @Param @DefaultValue(Contract.DEFAULT_NUMBER) Long[] nullableWithDefaultParam,
            @NonNull @Param @DefaultValue(Contract.DEFAULT_NUMBER) long[] nonNullWithDefaultParam);

    @NonNull
    @UriBuilder("/path/segment")
    @WithUriData
    Uri buildFloatArrayUri(
            @Nullable @Param Float[] nullableParam,
            @NonNull @Param float[] nonNullParam,
            @Nullable @Param @DefaultValue(Contract.DEFAULT_FLOAT_NUMBER) Float[] nullableWithDefaultParam,
            @NonNull @Param @DefaultValue(Contract.DEFAULT_FLOAT_NUMBER) float[] nonNullWithDefaultParam);

    @NonNull
    @UriBuilder("/path/segment")
    @WithUriData
    Uri buildDoubleArrayUri(
            @Nullable @Param Double[] nullableParam,
            @NonNull @Param double[] nonNullParam,
            @Nullable @Param @DefaultValue(Contract.DEFAULT_FLOAT_NUMBER) Double[] nullableWithDefaultParam,
            @NonNull @Param @DefaultValue(Contract.DEFAULT_FLOAT_NUMBER) double[] nonNullWithDefaultParam);

    @NonNull
    @UriBuilder("/path/segment")
    @WithUriData
    Uri buildUserArrayUri(
            @NonNull @Param User[] param,
            @NonNull @Param @DefaultValue(Contract.DEFAULT_USER) User[] paramWithDefault);

    @NonNull
    @UriBuilder("/path/segment")
    @WithUriData
    Uri buildNullableUserArrayUri(
            @Nullable @Param User[] param,
            @Nullable @Param @DefaultValue(Contract.DEFAULT_USER) User[] paramWithDefault);

    @NonNull
    @UriBuilder("/path/segment")
    @WithUriData
    Uri buildDoubleDoubleArrayUri(
            @Nullable
            @Param
            @TypeAdapter(DoubleArrayTypeAdapter.class) double[][] nullableParam,
            @NonNull
            @Param
            @TypeAdapter(DoubleArrayTypeAdapter.class) double[][] nonNullParam,
            @Nullable
            @Param
            @DefaultValue(Contract.DEFAULT_FLOAT_ARRAY)
            @TypeAdapter(DoubleArrayTypeAdapter.class) double[][] nullableWithDefaultParam,
            @NonNull
            @Param
            @DefaultValue(Contract.DEFAULT_FLOAT_ARRAY)
            @TypeAdapter(DoubleArrayTypeAdapter.class) double[][] nonNullWithDefaultParam);
}
