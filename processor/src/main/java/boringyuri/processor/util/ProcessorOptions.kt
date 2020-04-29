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

package boringyuri.processor.util

import boringyuri.processor.base.ProcessingSession
import javax.lang.model.element.Element
import kotlin.reflect.KClass

object ProcessorOptions {
    /**
     * Option to turn off the ordered segments warning:
     *
     * "Template {path name} is not found in &#64;UriBuilder("/base/path").
     * Fallback to ordered segments may cause an unpredictable result".
     *
     * Type: [Boolean]
     */
    const val OPT_ORDERED_SEGMENTS_WARNING = "boringyuri.suppress_warning.ordered_segments"

    fun warnOrderedSegmentsUsage(
        logger: Logger,
        session: ProcessingSession,
        pathSegment: String,
        basePath: String,
        annotation: KClass<out Annotation>,
        originatingElement: Element? = null
    ) {
        if (session.getOption(OPT_ORDERED_SEGMENTS_WARNING)?.toBoolean() != true) {
            logger.warn(
                originatingElement,
                "Template {$pathSegment} is not found in @${annotation.simpleName}(\"$basePath\"). " +
                        "Fallback to ordered segments may cause an unpredictable result."
            )
        }
    }
}
