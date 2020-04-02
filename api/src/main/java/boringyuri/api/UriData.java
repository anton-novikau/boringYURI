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
 * <pre><code>
 *     &#64UriFactory(scheme = "content", authority="com.example.provider")
 *     interface UserProviderUriBuilder {
 *
 *          &#64UriBuilder("user/regular")
 *          Uri buildRegularUserUri(@Param String phoneNumber, @Param String name);
 *
 *          &#64UriBuilder("user/admin")
 *          Uri buildAdminUserUri(@Param String phoneNumber, @Param String name);
 *
 *     }
 * </code></pre>
 * To obtain the query parameters data from two of these {@code Uri}s it would be better
 * to have objects of the same type, so the {@code ContentProvider} could handle some
 * similar user related logic for both requests. In this case an interface with getters
 * can be defined and this interface will represent both {@code Uri}s data.
 * <pre><code>
 *     &#64UriData
 *     public interface UserData {
 *
 *         &#64Param
 *         String getPhoneNumber();
 *
 *         &#64Param
 *         String getName();
 *
 *     }
 * </code></pre>
 * For the interface there will be created a {@code Uri} based data class implementation
 * similar to the one described for {@link WithUriData}:
 * <pre><code>
 *     public final class UserDataImpl {
 *         &#64NonNull
 *         private final Uri mDataUri;
 *
 *         ...
 *
 *         private String mPhoneNumber;
 *
 *         private String mName;
 *
 *         public UserDataImpl(&#64NonNull Uri uri) {
 *             mDataUri = uri;
 *         }
 *
 *         public String getPhoneNumber() {
 *             ...
 *             return mPhoneNumber;
 *         }
 *
 *         public String getName() {
 *             ...
 *             return mName;
 *         }
 *     }
 * </code></pre>
 * </p>
 *
 * @see WithUriData
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface UriData {
}
