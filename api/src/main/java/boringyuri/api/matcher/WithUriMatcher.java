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

package boringyuri.api.matcher;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * An annotation that indicates that the given {@link boringyuri.api.UriFactory} needs also
 * a {@code UriMatcher} to be generated.
 * </p>
 * <p>
 * Example:
 * </p>
 * <pre><code>
 *     &#64;UriFactory(scheme = "content", authority="com.example.provider")
 *     &#64;WithUriMatcher("UserProviderUriMatcher")
 *     interface UserProviderUriBuilder {
 *
 *          &#64;UriBuilder("user/colleague")
 *          &#64;MatchesTo("REGULAR_USER")
 *          Uri buildRegularUserUri(@Param String phoneNumber, @Param String name);
 *
 *          &#64;UriBuilder("user/admin")
 *          &#64;MatchesTo("ADMIN_USER")
 *          Uri buildAdminUserUri(@Param String phoneNumber, @Param String name);
 *
 *     }
 * </code></pre>
 * <p>
 * The generated {@code UserProviderUriMatcher} can be used to distinguish one {@code Uri} from
 * another and map them to an integer matcher code:
 * </p>
 * <pre><code>
 *     Uri colleagueUri = builder.buildRegularUserUri("2220042", "John Doe");
 *     Uri adminUri = builder.buildAdminUserUri("3330042", "Jane Smith");
 *
 *     UriMatcher matcher = new UserProviderUriMatcher();
 *
 *     matcher.match(colleagueUri) returns UserProviderUriMatcher.MatcherCode.REGULAR_USER constant value
 *     matcher.match(adminUri) returns UserProviderUriMatcher.MatcherCode.ADMIN_USER constant value
 * </code></pre>
 * <p>
 * {@code UriMatcher}s are widely used in android {@code ContentProvider}s so it's recommended to
 * use this annotation if the {@code UriFactory} scheme is <b>content</b>.
 * </p>
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface WithUriMatcher {
    /**
     * <p>
     * A name of the generated {@code UriMatcher}. It can be a fully qualified name or
     * a simple name (in this case the factory's package will be used).
     * </p>
     * <p>
     * If the {@code UriMatcher}'s name is not provided it will be generated based on the factory's
     * class name adding the default class name suffix. The package name will be the same as the
     * the factory's package.
     * </p>
     */
    String value() default "";
}
