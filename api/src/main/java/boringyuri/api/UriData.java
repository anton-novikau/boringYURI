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
 * An annotation that indicates to generate a data class that is not bound to a
 * specific {@code Uri}. The generated data class can be used to parse data from
 * two or more different {@code Uri}s.
 * </p>
 * <p>
 * Example: You have a {@code Uri} builder for a {@code ContentProvider} that can produce
 * two similar {@code Uri}s with different base paths (so they can be distinguished by the
 * {@code UriMatcher}) but the same query parameters.
 * </p>
 * <pre><code>
 *     &#64;UriFactory(scheme = "content", authority="com.example.provider")
 *     interface UserProviderUriBuilder {
 *
 *          &#64;UriBuilder("user/regular")
 *          Uri buildRegularUserUri(@Param String phoneNumber, @Param String name);
 *
 *          &#64;UriBuilder("user/admin")
 *          Uri buildAdminUserUri(@Param String phoneNumber, @Param String name);
 *
 *     }
 * </code></pre>
 * <p>
 * To obtain the query parameters data from two of these {@code Uri}s it would be better
 * to have objects of the same type, so the {@code ContentProvider} could handle some
 * similar user related logic for both requests. In this case an interface with getters
 * can be defined and this interface will represent both {@code Uri}s data.
 * </p>
 * <pre><code>
 *     &#64;UriData
 *     public interface UserData {
 *
 *         &#64;Param
 *         String getPhoneNumber();
 *
 *         &#64;Param
 *         String getName();
 *
 *     }
 * </code></pre>
 * <p>
 * For the interface there will be created a {@code Uri} based data class implementation
 * similar to the one described for {@link WithUriData}:
 * </p>
 * <pre><code>
 *     public final class UserDataImpl implements UserData {
 *         &#64;NonNull
 *         private final Uri dataUri;
 *
 *         ...
 *
 *         private String phoneNumber;
 *
 *         private String name;
 *
 *         public UserDataImpl(&#64;NonNull Uri uri) {
 *             dataUri = uri;
 *         }
 *
 *         public String getPhoneNumber() {
 *             ...
 *             return phoneNumber;
 *         }
 *
 *         public String getName() {
 *             ...
 *             return name;
 *         }
 *     }
 * </code></pre>
 *
 * @see WithUriData
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface UriData {
    /**
     * <p>
     * Relative {@code Uri} path that can be used as a reference to parse
     * <code>&#64;Path</code> segments into an appropriate getter method.
     *</p>
     * <p>
     * If the same data class is used for two or more different {@code Uri}s
     * the wildcard <code>&#42;</code> can be used to replace the constant path
     * segment that varies in these {@code Uri}s. Example:
     * </p>
     * <pre><code>
     *     &#64;UriData("/user/&#42;/{id}")
     *     public interface UserData {
     *          ....
     *     }
     * </code></pre>
     * <p>
     * Which means that the same {@code UserData} implementation can be used to
     * parse the data from <code>/user/friend/42</code> and from
     * <code>/user/colleague/24</code>.
     * </p>
     * <p>If the path is empty it means the first variable path segment will
     * be expected to be in the very first position after the authority of
     * the {@code Uri}. Example:</p>
     * <pre><code>
     *     &#64;UriData
     *     public interface UserData {
     *
     *          &#64;Path
     *          long getId()
     *
     *     }
     * </code></pre>
     * <p>
     * Here the path segment <code>id</code> <b>can</b> be parsed from
     * <code>content://com.example.provider/42</code> using the {@code UriData}
     * class, but <b>can not</b> be parsed from
     * <code>content://com.example.provider/user/42</code>
     * </p>
     * <p>If the path doesn't start with a '/', the parser will prepend the
     * given path with a '/'.</p>
     */
    String value() default "";
}
