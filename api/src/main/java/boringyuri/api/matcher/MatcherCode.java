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
 * The annotation to map a {@code Uri} produced by the {@link boringyuri.api.UriBuilder} to
 * an integer code in a {@code UriMatcher}, so it can be easily distinguished from the
 * other {@code Uri}s.
 * </p>
 * <pre><code>
 *     &#64;UriFactory(scheme = "content", authority="com.example.provider")
 *     &#64;WithUriMatcher("UserProviderUriMatcher")
 *     interface UserProviderUriBuilder {
 *         class Contract {
 *             public static final int CODE_USER = 100;
 *             public static final int CODE_ADMIN = 200;
 *         }
 *
 *          &#64;UriBuilder("user/colleague")
 *          &#64;MatcherCode(Contract.CODE_USER)
 *          Uri buildRegularUserUri(@Param String phoneNumber, @Param String name);
 *
 *          &#64;UriBuilder("user/admin")
 *          &#64;MatcherCode(Contract.CODE_ADMIN)
 *          Uri buildAdminUserUri(@Param String phoneNumber, @Param String name);
 *
 *     }
 *
 *     Uri colleagueUri = builder.buildRegularUserUri("2220042", "John Doe");
 *     Uri adminUri = builder.buildAdminUserUri("3330042", "Jane Smith");
 *
 *     switch (matcher.match(colleagueUri)) {
 *         case Contract.CODE_ADMIN: // 200
 *             // handle admin uri here
 *             ...
 *             break;
 *         case Contract.CODE_USER: // 100
 *             // handle user uri here
 *             ...
 *             break;
 *     }
 * </code></pre>
 * <p>
 * The uniqueness of the codes provided as a value of {@code MatcherCode} must be maintained
 * by the developer, unlike the matcher code produced by the {@link MatchesTo}, which uniqueness
 * is guaranteed by the library.
 * </p>
 * <p>
 * It can be used only in combination with {@link boringyuri.api.UriBuilder} set on a builder
 * method and {@link WithUriMatcher} set on a {@code Uri} factory interface.
 * </p>
 *
 * @see MatchesTo
 * @see WithUriMatcher
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface MatcherCode {

    /**
     * The matcher code value to be mapped to a {@code Uri} produced by the
     * {@link boringyuri.api.UriBuilder} method.
     */
    int value();

    /**
     * <p>
     * Flag that indicates if the {@code Uri} can be enabled or disabled based on some static
     * condition, eg. build environment (prod/debug) or build flavor. When the {@code Uri} is
     * disabled it just won't be added to the {@code UriMatcher} so there never be a valid
     * mapping found for it.
     * </p>
     * <p>
     * By default the {@code Uri} is always enabled.
     * </p>
     */
    boolean enabled() default true;
}
