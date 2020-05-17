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

package boringyuri.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * The value of a path segment or a query parameter that can be used as a fallback for
 * <code>null</code> method parameter on building the {@code Uri} or for absent query
 * parameter on reading the data from the {@code Uri}. The default value is also used
 * when incompatible data received in a specific path segment or a query parameter.
 * </p>
 * <p>Examples:</p>
 * <pre><code>
 *     &#64;UriBuilder("/user/{id}")
 *     Uri buildUserUri(&#64;@Nullable &#64;Path("id") @DefaultValue("f42b3a") String userId);
 * </code></pre>
 * Calling {@code foo.buildUserUri(null)} yields {@code /user/f42b3a}.
 * <pre><code>
 *     &#64;UriBuilder("/user")
 *     Uri buildUserUri(&#64;@Nullable &#64;Param("id") @DefaultValue("f42b3a") String userId);
 * </code></pre>
 * Calling {@code foo.buildUserUri(null)} yields {@code /user?id=f42b3a}.
 * <pre><code>
 *     &#64;UserData("/user/{id}")
 *     interface UserUriData {
 *
 *         &#64;Path("id")
 *         &#64;DefaultValue("42")
 *         long getUserId()
 *
 *         &#64;Param("age")
 *         &#64;DefaultValue("27")
 *         int getAge()
 *     }
 * </code></pre>
 * Calling {@code new UserUriData("https://example.com/user/xxxx").getUserId()} yields {@code 42}
 * and calling {@code new UserUriData("https://example.com/user/33?age=five).getAge()} yields
 * {@code 27}.
 * <p>
 * The default value must be correctly serialized for a specific type of a method parameter or
 * a getter method, otherwise you may have an incorrect or unexpected result. For types that
 * require a {@link boringyuri.api.adapter.TypeAdapter} the default value must be serialized
 * according to the rules of the specified {@code TypeAdapter}.
 * </p>
 *
 * @see Path
 * @see Param
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.PARAMETER, ElementType.METHOD})
public @interface DefaultValue {
    /**
     * Serialized default value for a path segment or a query parameter.
     */
    String value();
}
