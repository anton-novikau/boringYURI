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
 * An annotation for a {@code Uri} factory that is responsible for creating {@code Uri}s
 * related to the same service (i.e. some endpoint with the same URL scheme and authority).
 * </p>
 * <p>
 * Example:
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
 * Calling {@code uriBuilder.buildRegularUserUri("10020042", "John Doe")} will produce
 * {@code content://com.example.provider/user/regular?phoneNumber="10020042"&name="John%20Doe"}
 * whereas {@code uriBuilder.buildAdminUserUri("20030042", "Jane Doe")} will produce
 * {@code content://com.example.provider/user/admin?phoneNumber="20030042"&name="Jane%20Doe"}
 * </p>
 *
 * @see UriBuilder
 * @see WithUriData
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface UriFactory {
    /**
     * A {@code Uri} scheme applied to all the build methods in the factory.
     * Example: "http" or "content".
     */
    String scheme();

    /**
     * <p>
     * An authority part of the {@code Uri}s built by every method declared
     * in the factory. For server addresses, the authority is structured as follows:
     * {@code [ userinfo '@' ] host [ ':' port ]}
     * </p>
     *
     * <p>Examples: "google.com", "bob@google.com:80"</p>
     */
    String authority();
}
