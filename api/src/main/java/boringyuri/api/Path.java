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
 * An annotation that indicates to use the method parameter as a {@code Uri} path segment.
 * Every path segment can be referenced in the base path of the <code>&#64;UriBuilder</code>
 * in parenthesises by the path name (the name in the {@link #value()} or the name of the
 * annotated method parameter). Example:
 * </p>
 * <pre><code>
 *     &#64;UriBuilder("/user/{id}")
 *     Uri buildUserUri(&#64;Path("id") String userId);
 * </code></pre>
 * <p>
 * Calling {@code foo.buildUserUri(42)} yields {@code /user/42}. But the same result
 * can be achieved changing the placeholder name to the method parameter name and omitting
 * the {@link #value()} of the <code>&#64;Path</code>:
 * </p>
 * <pre><code>
 *     &#64;UriBuilder("/user/{userId}")
 *     Uri buildUserUri(&#64;Path String userId);
 * </code></pre>
 * <p>
 * If the named placeholder is not found in the base path an order of the method parameters
 * becomes important as it defines the order of the path segments. Example:
 * </p>
 * <pre><code>
 *     &#64;UriBuilder("/user")
 *     public Uri buildFetchUserDetailsUri(&#64;Path String group, &#64;Path long userId);
 * </code></pre>
 * Calling {@code foo.buildFetchUserDetailsUri("friends", 42)} yields {@code /user/friends/42}
 * <p>
 * Values are URL encoded by default. Disable with {@code encoded = true}.
 * </p>
 * <pre><code>
 *    &#64;UriBuilder("/user/{name}")
 *    Uri buildEncodedUri(&#64;Path String name);
 *
 *    &#64;UriBuilder("/user/{name}")
 *    Uri buildNotEncoded(&#64;Path(encoded=true) String name);
 * </code></pre>
 * <p>
 * Calling foo.buildEncodedUri("John+Doe") yields /user/John%2BDoe whereas
 * foo.buildNotEncoded("John+Doe") yields /user/John+Doe.
 * </p>
 * <p>
 * <b>IMPORTANT:</b> Path parameters may not be <code>null</code> and must be explicitly marked
 * as <code>&#64;NonNull</code> (or they must have a non nullable type in kotlin).
 * </p>
 * <p>
 * When a method of a {@code Uri} data class is annotated with <code>&#64;Path</code> it
 * means the method will obtain the value from the position of the named placeholder in
 * <code>&#64;UriData</code> value. If the placeholder name is not found the parser will
 * fall back to the ordered segments which is <b>highly not recommended</b> as it may
 * cause to an unpredictable result. Example:
 * </p>
 * <pre><code>
 *      &#64;UriData("/&#42;/{group}/{userId}")
 *      public interface UserData {
 *
 *          &#64;Path
 *          String getGroup();
 *          &#64;Path
 *          long getUserId()
 *
 *      }
 * </code></pre>
 * <p>
 * Calling {@code userData.getGroup()} for the {@code uri} path {@code "/user/friends/42"}
 * yields {@code "friends"} whereas {@code userData.getUserId()} yields {@code 42}.
 * </p>
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.PARAMETER, ElementType.METHOD})
public @interface Path {
    /**
     * Specifies whether the argument value to the annotated method parameter
     * is already URL encoded. Default is <code>false</code>.
     */
    boolean encoded() default false;

    /**
     * <p>
     * Specifies the {@code Uri} path segment name.
     * </p><p>
     * If not specified, the method parameter name will be used. If &#64;Path is used
     * with a getter method of a uri data interface and the segment name is not specified,
     * the getter name will be taken without the {@code get}, {@code is} or {@code are}
     * prefix and the first letter will be lowered. For example:
     * </p>
     * <pre><code>
     * &#64;Path
     * String getName();
     * </code></pre>
     * Becomes "name"
     *
     * <pre><code>
     * &#64;Path
     * String isEnabled();
     * </code></pre>
     * Becomes "enabled"
     */
    String value() default "";
}
