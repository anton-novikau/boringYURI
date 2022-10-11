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
@file:JvmName("MethodNameUtils")
package boringyuri.processor.common.steps.util

import com.squareup.javapoet.TypeName
import org.apache.commons.lang3.StringUtils
import org.apache.commons.text.CaseUtils

private const val GETTER_PREFIX = "get"
private const val BOOLEAN_GETTER_PREFIX = "is"
private val BOOLEAN_GETTER_PATTERN = "^(?:is|has|are|can)[A-Z]\\w+$".toRegex()

fun buildGetterName(paramName: String, paramType: TypeName): String {
    return if (TypeName.BOOLEAN == paramType || TypeName.BOOLEAN.box() == paramType) {
        if (BOOLEAN_GETTER_PATTERN.matches(paramName)) {
            fixParamName(paramName, false)
        } else {
            BOOLEAN_GETTER_PREFIX + fixParamName(paramName, true)
        }
    } else {
        GETTER_PREFIX + fixParamName(paramName, true)
    }
}

private fun fixParamName(paramName: String, capitalize: Boolean): String {
    return when {
        paramName.contains("_") -> {
            CaseUtils.toCamelCase(paramName, capitalize, '_')
        }
        capitalize -> {
            StringUtils.capitalize(paramName)
        }
        else -> paramName
    }
}