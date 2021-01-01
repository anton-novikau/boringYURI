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
 * The annotation to map a {@code Uri} produced by the {@link UriBuilder} to an integer code
 * in a {@code UriMatcher}, so it can be easily distinguished from the other {@code Uri}s.
 * </p>
 * <pre><code>
 *     &#64;UriFactory(scheme = "content", authority="com.example.provider")
 *     &#64;WithUriMatcher("UserProviderUriMatcher")
 *     interface UserProviderUriBuilder {
 *
 *          &#64;UriBuilder("user/colleague")
 *          &#64;MatchesTo("CODE_USER")
 *          Uri buildRegularUserUri(@Param String phoneNumber, @Param String name);
 *
 *          &#64;UriBuilder("user/admin")
 *          &#64;MatchesTo("CODE_ADMIN")
 *          Uri buildAdminUserUri(@Param String phoneNumber, @Param String name);
 *
 *     }
 *
 *     Uri colleagueUri = builder.buildRegularUserUri("2220042", "John Doe");
 *     Uri adminUri = builder.buildAdminUserUri("3330042", "Jane Smith");
 *
 *     switch (matcher.match(colleagueUri)) {
 *         case UserProviderUriMatcher.MatcherCode.CODE_ADMIN: // some unique value
 *             // handle admin uri here
 *             ...
 *             break;
 *         case UserProviderUriMatcher.MatcherCode.CODE_USER: // another unique value
 *             // handle user uri here
 *             ...
 *             break;
 *     }
 * </code></pre>
 * <p>
 * The uniqueness of the codes generated for {@code MatchesTo} is guaranteed by the library,
 * unlike the value in {@link MatcherCode} that is completely maintained by the developer.
 * </p>
 * <p>
 * The repeated names inside one {@link UriFactory} match two {@code Uri}s to the same integer.
 * </p>
 * <pre><code>
 *     &#64;UriFactory(scheme = "content", authority="com.example.provider")
 *     &#64;WithUriMatcher("UserProviderUriMatcher")
 *     interface UserProviderUriBuilder {
 *
 *          &#64;UriBuilder("user/colleague")
 *          &#64;MatchesTo("CODE_USER")
 *          Uri buildRegularUserUri(@Param String phoneNumber, @Param String name);
 *
 *          &#64;UriBuilder("user/admin")
 *          &#64;MatchesTo("CODE_USER")
 *          Uri buildAdminUserUri(@Param String phoneNumber, @Param String name);
 *
 *     }
 *
 *     Uri colleagueUri = builder.buildRegularUserUri("2220042", "John Doe");
 *     Uri adminUri = builder.buildAdminUserUri("3330042", "Jane Smith");
 *
 *     matcher.match(colleagueUri) == UserProviderUriMatcher.MatcherCode.CODE_USER
 *     matcher.match(adminUri) == UserProviderUriMatcher.MatcherCode.CODE_USER
 * </code></pre>
 * But:
 * <pre><code>
 *     &#64;UriFactory(scheme = "content", authority="com.example.provider")
 *     &#64;WithUriMatcher("BackgroundUriMatcher")
 *     interface BackgroundUriBuilder {
 *
 *          &#64;UriBuilder("background/color/{color}")
 *          &#64;MatchesTo("CODE_COLOR")
 *          Uri buildColorBackgroundUri(@Path int color);
 *
 *     }
 *
 *     &#64;UriFactory(scheme = "content", authority="com.example.provider")
 *     &#64;WithUriMatcher("ColorUriMatcher")
 *     interface ColorUriBuilder {
 *
 *          &#64;UriBuilder("color/{red}/{green}/{blue}")
 *          &#64;MatchesTo("CODE_COLOR")
 *          Uri buildColorUri(@Path int red, @Path int green, @Path int blue);
 *
 *     }
 *
 *     Uri backgroundUri = backgroundUriBuilder.buildColorBackgroundUri(0xffff0000);
 *     Uri colorUri = colorUriBuilder.buildColorUri(255, 0, 0);
 *
 *     matcher.match(backgroundUri) == BackgroundUriMatcher.MatcherCode.CODE_COLOR
 *     matcher.match(colorUri) == ColorUriMatcher.MatcherCode.CODE_COLOR
 *
 *     where BackgroundUriMatcher.MatcherCode.CODE_COLOR != ColorUriMatcher.MatcherCode.CODE_COLOR
 * </code></pre>
 * <p>
 * It can be used only in combination with {@link UriBuilder} set on a builder method and
 * {@link WithUriMatcher} set on a {@code Uri} factory interface.
 * </p>
 *
 * @see MatcherCode
 * @see WithUriMatcher
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface MatchesTo {

    /**
     * <p>
     * The name of an integer constant field that will be used as a code to match a {@code Uri}
     * built by the annotated {@link UriBuilder} method.
     * </p>
     * <p>
     * The {@code value} must contain only valid java field symbols which are alphanumerics and
     * underscore. It can not start with a number, but can start with an underscore.
     * The {@code value} can be either in the upper case or the lowercase, but the constant will
     * always comply the java code style, so it will be transformed to the upper case.
     * </p>
     */
    String value();

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
