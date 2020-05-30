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

package boringyuri.api.adapter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * An annotation that indicates to use {@link BoringTypeAdapter} with a class, method or
 * a method parameter when they are of a non standard type supported by Boring YURI (eg.
 * primitive, primitive wrapper, {@code String} or {@code Uri}) or the type require some
 * non standard type serialization/deserialization.
 * </p>
 * <p>Here are some examples of how this annotation can be used:</p>
 * <pre><code>
 *      &#64;TypeAdapter(UserTypeAdapter.class)
 *      public class User {
 *
 *          public final String firstName;
 *          public final String lastName;
 *
 *          public User(String firstName, String lastName) {
 *              this.firstName = firstName;
 *              this.lastName = lastName;
 *          }
 *
 *      }
 * </code></pre>
 *
 * <pre><code>
 *      &#64;UriFactory(scheme = "https", authority = "example.com")
 *      public interface ImageApi {
 *
 *          &#64;UriBuilder("/select_rect")
 *          public Uri buildHighlightUri(&#64;Param &#64;TypeAdapter(RectAdapter.class) Rect highlightArea);
 *
 *      }
 * </code></pre>
 *
 * <pre><code>
 *      &#64;UriData
 *      public interface HighlightData {
 *
 *          &#64;Param
 *          &#64;TypeAdapter(RectAdapter.class)
 *          Rect getHighlightArea();
 *
 *      }
 * </code></pre>
 *
 * For parameters of an {@code array} type the <code>&#64;TypeAdapter</code> should be used
 * for an {@code array} element, not for the {@code array} itself. Example:
 *
 * <pre><code>
 *      &#64;UriFactory(scheme = "https", authority = "example.com")
 *      public interface ImageApi {
 *
 *          &#64;UriBuilder("/select_rect")
 *          public Uri buildHighlightUri(&#64;Param("highlight") &#64;TypeAdapter(RectAdapter.class) Rect[] highlights);
 *
 *          &#64;UriBuilder("/user/details/save")
 *          public Uri saveDetailsUri(&#64;Param("user") User[] users);
 *      }
 * </code></pre>
 * <p>
 * In the example above the first method has a <code>&#64;TypeAdapter</code> specified at use
 * for the type {@code Rect} and the second method uses an array of {@code User}s from the very
 * first example where the {@code User} is annotated with <code>&#64;TypeAdapter(UserTypeAdapter.class)</code>.
 * </p>
 *
 * @see boringyuri.api.Path
 * @see boringyuri.api.Param
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE, ElementType.PARAMETER, ElementType.METHOD})
public @interface TypeAdapter {
    /**
     * Implementation of {@link BoringTypeAdapter} to convert the annotated type.
     */
    Class<? extends BoringTypeAdapter<?>> value();
}
