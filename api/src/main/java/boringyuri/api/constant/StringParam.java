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
 * Query parameter of a constant {@code String} value.
 * <p>
 * Example:
 * <pre><code>
 *     &#64UriBuilder("image")
 *     &#StringParam(name = "format", value= "png")
 *     public Uri buildImageUri();
 * </code></pre>
 * Calling {@code foo.buildImageUri()} yields {@code /image?format=png}
 * </p>
 *
 * @see boringyuri.api.Param
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
@Repeatable(StringParams.class)
public @interface StringParam {
    /**
     * Query parameter name.
     */
    String name();

    /**
     * Query parameter value.
     */
    String value();
}
