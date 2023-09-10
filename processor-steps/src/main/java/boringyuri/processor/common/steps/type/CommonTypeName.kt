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

package boringyuri.processor.common.steps.type

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import boringyuri.api.adapter.BoringTypeAdapter
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.WildcardTypeName
import org.jetbrains.annotations.NotNull as JBNonNull
import org.jetbrains.annotations.Nullable as JBNullable

object CommonTypeName {
    val NON_NULL: ClassName = ClassName.get(NonNull::class.java)

    val NULLABLE: ClassName = ClassName.get(Nullable::class.java)

    val JB_NON_NULL: ClassName = ClassName.get(JBNonNull::class.java)

    val JB_NULLABLE: ClassName = ClassName.get(JBNullable::class.java)

    val STRING: ClassName = ClassName.get(String::class.java)

    val OVERRIDE: ClassName = ClassName.get(Override::class.java)

    var UNSUPPORTED_OPERATION: ClassName = ClassName.get(UnsupportedOperationException::class.java)

    val MAP: ClassName = ClassName.get(MutableMap::class.java)

    val HASH_MAP: ClassName = ClassName.get(HashMap::class.java)

    val CLASS: ClassName = ClassName.get(Class::class.java)

    val ANDROID_URI: ClassName = ClassName.get("android.net", "Uri")

    var ANDROID_URI_MATCHER: ClassName = ClassName.get("android.content", "UriMatcher")

    val ANDROID_URI_BUILDER: ClassName = ANDROID_URI.nestedClass("Builder")

    private var TYPE_ADAPTER: ClassName = ClassName.get(BoringTypeAdapter::class.java)

    val ANY_TYPE_ADAPTER: ParameterizedTypeName = ParameterizedTypeName.get(
        TYPE_ADAPTER,
        WildcardTypeName.subtypeOf(TypeName.OBJECT)
    )

    val STRING_LIST: ParameterizedTypeName = ParameterizedTypeName.get(
        ClassName.get(MutableList::class.java),
        STRING
    )

}
