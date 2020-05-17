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
import boringyuri.api.Path;
import boringyuri.api.UriBuilder;
import boringyuri.api.UriFactory;
import boringyuri.api.WithUriData;
import boringyuri.sample.data.User;

@UriFactory(scheme = "content", authority = "com.example.provider.test")
public interface TestUriBuilder {

    class Contract {
        static final String DEFAULT_STRING = "default";
        static final String DEFAULT_URI = "https://example.com/uri";
        static final String DEFAULT_USER = "42;John Doe";
        static final String DEFAULT_BOOLEAN = "true";
        static final String DEFAULT_CHAR = "c";
        static final String DEFAULT_NUMBER = "1";
        static final String DEFAULT_FLOAT_NUMBER = "1.2";
    }

    @NonNull
    @UriBuilder("/path/segment/{nullableSegment}/{nonNullSegment}")
    @WithUriData
    Uri buildStringUri(
            @Nullable @Path @DefaultValue(Contract.DEFAULT_STRING) String nullableSegment,
            @NonNull @Path String nonNullSegment,
            @Nullable @Param String nullableParam,
            @NonNull @Param String nonNullParam,
            @Nullable @Param @DefaultValue(Contract.DEFAULT_STRING) String nullableWithDefaultParam,
            @NonNull @Param @DefaultValue(Contract.DEFAULT_STRING) String nonNullWithDefaultParam);

    @NonNull
    @UriBuilder("/path/segment/{uri}")
    @WithUriData
    Uri buildUriBasedUri(
            @NonNull @Path Uri uri,
            @NonNull @Param Uri param,
            @NonNull @Param @DefaultValue(Contract.DEFAULT_URI) Uri paramWithDefault);

    @NonNull
    @UriBuilder("/path/segment/{uri}")
    @WithUriData
    Uri buildNullableUriBasedUri(
            @Nullable @Path @DefaultValue(Contract.DEFAULT_URI) Uri uri,
            @Nullable @Param Uri param,
            @Nullable @Param @DefaultValue(Contract.DEFAULT_URI) Uri paramWithDefault);

    @NonNull
    @UriBuilder("/path/segment/{nullableSegment}/{nonNullSegment}")
    @WithUriData
    Uri buildBooleanUri(
            @Nullable @Path @DefaultValue(Contract.DEFAULT_BOOLEAN) Boolean nullableSegment,
            @Path boolean nonNullSegment,
            @Nullable @Param Boolean nullableParam,
            @Param boolean nonNullParam,
            @Nullable @Param @DefaultValue(Contract.DEFAULT_BOOLEAN) Boolean nullableWithDefaultParam,
            @Param @DefaultValue(Contract.DEFAULT_BOOLEAN) boolean nonNullWithDefaultParam);

    @NonNull
    @UriBuilder("/path/segment/{nullableSegment}/{nonNullSegment}")
    @WithUriData
    Uri buildCharUri(
            @Nullable @Path @DefaultValue(Contract.DEFAULT_CHAR) Character nullableSegment,
            @Path char nonNullSegment,
            @Nullable @Param Character nullableParam,
            @Param char nonNullParam,
            @Nullable @Param @DefaultValue(Contract.DEFAULT_CHAR) Character nullableWithDefaultParam,
            @Param @DefaultValue(Contract.DEFAULT_CHAR) char nonNullWithDefaultParam);

    @NonNull
    @UriBuilder("/path/segment/{nullableSegment}/{nonNullSegment}")
    @WithUriData
    Uri buildByteUri(
            @Nullable @Path @DefaultValue(Contract.DEFAULT_NUMBER) Byte nullableSegment,
            @Path byte nonNullSegment,
            @Nullable @Param Byte nullableParam,
            @Param byte nonNullParam,
            @Nullable @Param @DefaultValue(Contract.DEFAULT_NUMBER) Byte nullableWithDefaultParam,
            @Param @DefaultValue(Contract.DEFAULT_NUMBER) byte nonNullWithDefaultParam);

    @NonNull
    @UriBuilder("/path/segment/{nullableSegment}/{nonNullSegment}")
    @WithUriData
    Uri buildShortUri(
            @Nullable @Path @DefaultValue(Contract.DEFAULT_NUMBER) Short nullableSegment,
            @Path short nonNullSegment,
            @Nullable @Param Short nullableParam,
            @Param short nonNullParam,
            @Nullable @Param @DefaultValue(Contract.DEFAULT_NUMBER) Short nullableWithDefaultParam,
            @Param @DefaultValue(Contract.DEFAULT_NUMBER) short nonNullWithDefaultParam);

    @NonNull
    @UriBuilder("/path/segment/{nullableSegment}/{nonNullSegment}")
    @WithUriData
    Uri buildIntUri(
            @Nullable @Path @DefaultValue(Contract.DEFAULT_NUMBER) Integer nullableSegment,
            @Path int nonNullSegment,
            @Nullable @Param Integer nullableParam,
            @Param int nonNullParam,
            @Nullable @Param @DefaultValue(Contract.DEFAULT_NUMBER) Integer nullableWithDefaultParam,
            @Param @DefaultValue(Contract.DEFAULT_NUMBER) int nonNullWithDefaultParam);

    @NonNull
    @UriBuilder("/path/segment/{nullableSegment}/{nonNullSegment}")
    @WithUriData
    Uri buildLongUri(
            @Nullable @Path @DefaultValue(Contract.DEFAULT_NUMBER) Long nullableSegment,
            @Path long nonNullSegment,
            @Nullable @Param Long nullableParam,
            @Param long nonNullParam,
            @Nullable @Param @DefaultValue(Contract.DEFAULT_NUMBER) Long nullableWithDefaultParam,
            @Param @DefaultValue(Contract.DEFAULT_NUMBER) long nonNullWithDefaultParam);

    @NonNull
    @UriBuilder("/path/segment/{nullableSegment}/{nonNullSegment}")
    @WithUriData
    Uri buildFloatUri(
            @Nullable @Path @DefaultValue(Contract.DEFAULT_FLOAT_NUMBER) Float nullableSegment,
            @Path float nonNullSegment,
            @Nullable @Param Float nullableParam,
            @Param float nonNullParam,
            @Nullable @Param @DefaultValue(Contract.DEFAULT_FLOAT_NUMBER) Float nullableWithDefaultParam,
            @Param @DefaultValue(Contract.DEFAULT_FLOAT_NUMBER) float nonNullWithDefaultParam);

    @NonNull
    @UriBuilder("/path/segment/{nullableSegment}/{nonNullSegment}")
    @WithUriData
    Uri buildDoubleUri(
            @Nullable @Path @DefaultValue(Contract.DEFAULT_FLOAT_NUMBER) Double nullableSegment,
            @Path double nonNullSegment,
            @Nullable @Param Double nullableParam,
            @Param double nonNullParam,
            @Nullable @Param @DefaultValue(Contract.DEFAULT_FLOAT_NUMBER) Double nullableWithDefaultParam,
            @Param @DefaultValue(Contract.DEFAULT_FLOAT_NUMBER) double nonNullWithDefaultParam);

    @NonNull
    @UriBuilder("/path/segment/{user}")
    @WithUriData
    Uri buildUserUri(
            @NonNull @Path User user,
            @NonNull @Param User param,
            @NonNull @Param @DefaultValue(Contract.DEFAULT_USER) User paramWithDefault);

    @NonNull
    @UriBuilder("/path/segment/{user}")
    @WithUriData
    Uri buildNullableUserUri(
            @Nullable @Path @DefaultValue(Contract.DEFAULT_USER) User user,
            @Nullable @Param User param,
            @Nullable @Param @DefaultValue(Contract.DEFAULT_USER) User paramWithDefault);
}
