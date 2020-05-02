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

package boringyuri.api.constant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Query parameter of an integer (or long integer) constant value.</p>
 * <p>Example:</p>
 * <pre><code>
 *     &#64;UriBuilder("contacts")
 *     &#64;LongParam(name = "limit", value= 100)
 *     public Uri buildFetchContactsUri();
 * </code></pre>
 * <p>
 * Calling {@code foo.buildFetchContactsUri()} yields {@code /contacts?limit=100}
 * </p>
 *
 * @see boringyuri.api.Param
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
@Repeatable(LongParams.class)
public @interface LongParam {
    /**
     * Query parameter name.
     */
    String name();

    /**
     * Query parameter value.
     */
    long value() default 0L;
}
