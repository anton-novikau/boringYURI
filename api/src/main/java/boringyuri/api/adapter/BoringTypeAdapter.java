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

import androidx.annotation.NonNull;

/**
 * <p>Converts Java or Kotlin objects to {@code String} and back.</p>
 * <p>
 * By default Boring YURI knows how to serialize and deserialize only
 * primitives, primitive wrappers, {@code String} and {@code Uri}.
 * If the standard conversion is not appropriate or it is required
 * to use some non standard type in the uri builder, implement this
 * interface and use it as in the examples below:
 * </p>
 * <pre><code>
 *      public class RectAdapter implements BoringTypeAdapter&lt;Rect&gt; {
 *
 *          &#64;NonNull
 *          public String serialize(&#64;NonNull Rect rect) {
 *              return rect.left + ";" + rect.top + ";" + rect.right + ";" + rect.bottom;
 *          }
 *
 *          &#64;NonNull
 *          public Rect deserialize(&#64;NonNull String rect) {
 *              String[] vertices = rect.splint(";");
 *
 *              return new Rect(
 *                  Integer.parseInt(vertices[0]),
 *                  Integer.parseInt(vertices[1]),
 *                  Integer.parseInt(vertices[2]),
 *                  Integer.parseInt(vertices[3]));
 *          }
 *
 *      }
 * </code></pre>
 * <p>To use the custom type adapter there are two possible ways:</p>
 * <ol>
 *     <li>
 *     Use {@link TypeAdapter} annotation for the class that must be converted.
 *     This way works good for the custom types that belong to your code.
 *     <pre><code>
 *          &#64;TypeAdapter(UserTypeAdapter.class)
 *          public class User {
 *
 *              public final String firstName;
 *              public final String lastName;
 *
 *              public User(String firstName, String lastName) {
 *                  this.firstName = firstName;
 *                  this.lastName = lastName;
 *              }
 *
 *          }
 *     </code></pre>
 *     </li>
 *     <li>
 *     Use {@link TypeAdapter} annotation for in combination with
 *     {@link boringyuri.api.Param} or {@link boringyuri.api.Path} in uri builder
 *     method signature or in data class getter method
 *     <pre><code>
 *          &#64;UriFactory(scheme = "https", authority = "example.com")
 *          public interface ImageApi {
 *
 *              &#64;UriBuilder("select_rect")
 *              public Uri buildHighlightUri(&#64;Param &#64;TypeAdapter(RectAdapter.class) Rect highlightArea);
 *
 *          }
 *     </code></pre>
 *     <pre><code>
 *          &#64;UriData
 *          public interface HighlightData {
 *
 *              &#64;Param
 *              &#64;TypeAdapter(RectAdapter.class)
 *              Rect getHighlightArea();
 *
 *          }
 *     </code></pre>
 *     </li>
 * </ol>
 *
 * @param <T> Type to convert into String and back into the object.
 */
public interface BoringTypeAdapter<T> {
    /**
     * Converts {@code original} object of the specified type {@code T} to {@code String}.
     */
    @NonNull
    String serialize(@NonNull T value);

    /**
     * Converts the given {@code String} back to the object of the specified type {@code T}.
     */
    @NonNull
    T deserialize(@NonNull String value);
}
