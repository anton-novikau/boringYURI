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
 * Query parameter appended to the {@code Uri}.
 * </p><p>
 * Values of primitive types, primitive wrapper types, {@code String} or {@code Uri} can be
 * serialized and deserialized with a built-in type converter. For every other type of the
 * method parameter annotated with <code>&#64;Param</code> there should exist a
 * {@link boringyuri.api.adapter.BoringTypeAdapter BoringTypeAdapter} implementation
 * registered with {@link boringyuri.api.adapter.TypeAdapter TypeAdapter}.
 * </p><p>
 * Example:
 *
 * <pre><code>
 *     &#64;UriBuilder("user")
 *     public Uri buildFetchUserDetailsUri(&#64;Param int userId);
 * </code></pre>
 * Calling with {@code foo.buildFetchUserDetailsUri(100)} yields {@code /user?userId=100}
 *
 * <pre><code>
 *     &#64;UriBuilder("user")
 *     public Uri buildFetchUserDetailsUri(&#64;Param("id") int userId);
 * </code></pre>
 * Calling with {@code foo.buildFetchUserDetailsUri(100)} yields {@code /user?id=100}
 *
 * <pre><code>
 *     &#64;Param
 *     int getUserId();
 * </code></pre>
 * Calling with {@code foo.getUserId()} yields {@code userDetailsUri.getQueryParameter("userId")}
 *
 * <pre><code>
 *     &#64;Param("id")
 *     int getUserId();
 * </code></pre>
 * Calling with {@code foo.getUserId()} yields {@code userDetailsUri.getQueryParameter("id")}
 *
 *
 * @see boringyuri.api.Path
 * @see boringyuri.api.adapter.TypeAdapter
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.PARAMETER, ElementType.METHOD})
public @interface Param {
    /**
     * <p>
     * Specifies the {@code Uri} query parameter name.
     * </p><p>
     * If not specified, the method parameter name will be used. If &#64;Param is used
     * with a getter method of a uri data interface and the parameter name is not specified,
     * the getter name will be taken without the {@code get}, {@code is} or {@code are}
     * prefix and the lowering the first letter. For example:
     * </p>
     * <pre><code>
     * &#64;Param
     * String getName();
     * </code></pre>
     * Becomes "name"
     *
     * <pre><code>
     * &#64;Param
     * String isEnabled();
     * </code></pre>
     * Becomes "enabled"
     */
    String value() default "";
}
