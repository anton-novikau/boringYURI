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
 * An order of the method parameters annotated with <code>&#64;Path</code> is important as
 * it defines the order of the path segments. Example:
 * </p>
 * <pre><code>
 *     &#64;UriBuilder("user")
 *     public Uri buildFetchUserDetailsUri(&#64;Path String group, &#64;Path long userId);
 * </code></pre>
 * Calling {@code foo.buildFetchUserDetailsUri("friends", 42)} yields {@code /user/friends/42}
 * <p>
 * Values are URL encoded by default. Disable with {@code encoded = true}.
 * </p>
 * <pre><code>
 *    &#64;UriBuilder("user")
 *    Uri buildEncodedUri(&#64;Path String name);
 *
 *    &#64;UriBuilder("user")
 *    Uri buildNotEncoded(&#64;Path, encoded=true) String name);
 * </code></pre>
 * <p>
 * Calling foo.buildEncodedUri("John+Doe") yields /user/John%2BDoe whereas
 * foo.buildNotEncoded("John+Doe") yields /user/John+Doe.
 * </p>
 * <p>
 * <b>IMPORTANT:</b> Path parameters may not be <code>null</code>.
 * </p>
 * <p>
 * When a method of a {@code Uri} data class is annotated with <code>&#64;Path</code> it
 * means the method will obtain the value from the <code>N<sup>th</sup></code> path segment
 * of the {@code Uri}. An order of the getter methods defined in the {@code Uri} data interface
 * is important as it defines which path segment index must be taken for the getter value.
 * Example:
 * </p>
 * <pre><code>
 *      &#64;UriData
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
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.PARAMETER, ElementType.METHOD})
public @interface Path {
    /**
     * Specifies whether the argument value to the annotated method parameter
     * is already URL encoded. Default is <code>false</code>.
     */
    boolean encoded() default false;
}
