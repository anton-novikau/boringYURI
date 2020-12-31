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

package boringyuri.processor.type;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.WildcardTypeName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import boringyuri.api.adapter.BoringTypeAdapter;

public interface CommonTypeName {
    @NonNull
    ClassName NON_NULL = ClassName.get(NonNull.class);
    @NonNull
    ClassName NULLABLE = ClassName.get(Nullable.class);
    @NonNull
    ClassName JB_NON_NULL = ClassName.get(org.jetbrains.annotations.NotNull.class);
    @NonNull
    ClassName JB_NULLABLE = ClassName.get(org.jetbrains.annotations.Nullable.class);
    @NonNull
    ClassName STRING = ClassName.get(String.class);
    @NonNull
    ClassName OVERRIDE = ClassName.get(Override.class);
    @NonNull
    ClassName MAP = ClassName.get(Map.class);
    @NonNull
    ClassName HASH_MAP = ClassName.get(HashMap.class);
    @NonNull
    ClassName CLASS = ClassName.get(Class.class);
    @NonNull
    ClassName UNSUPPORTED_OPERATION = ClassName.get(UnsupportedOperationException.class);

    @NonNull
    ClassName ANDROID_URI = ClassName.get("android.net", "Uri");
    @NonNull
    ClassName ANDROID_URI_BUILDER = ANDROID_URI.nestedClass("Builder");
    @NonNull
    ClassName ANDROID_URI_MATCHER = ClassName.get(
            "android.content",
            "UriMatcher"
    );

    @NonNull
    ClassName TYPE_ADAPTER = ClassName.get(BoringTypeAdapter.class);
    @NonNull
    ParameterizedTypeName ANY_TYPE_ADAPTER = ParameterizedTypeName.get(
            TYPE_ADAPTER,
            WildcardTypeName.subtypeOf(TypeName.OBJECT)
    );
    @NonNull
    ParameterizedTypeName STRING_LIST = ParameterizedTypeName.get(
            ClassName.get(List.class),
            STRING
    );
}
