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

package boringyuri.sample.uri

import android.content.ContentResolver
import android.net.Uri
import androidx.annotation.ColorInt
import boringyuri.api.*

@UriFactory(scheme = ContentResolver.SCHEME_CONTENT, authority = "boringyuri.sample.backgrounds")
@WithUriMatcher("boringyuri.sample.uri.matcher.BackgroundUriMatcher")
interface BackgroundProviderUriBuilder {

    @UriBuilder("/bg/color/{color}")
    @MatcherCode(BackgroundMatcherCode.COLOR)
    fun buildColorBackgroundUri(@ColorInt @Path color: Int): Uri

    @UriBuilder("/bg/original/{id}")
    @MatcherCode(BackgroundMatcherCode.ORIGINAL)
    fun buildGalleryBackgroundUri(@Path id: Int): Uri

    @UriBuilder("/bg/thumbnail/{id}")
    @MatcherCode(BackgroundMatcherCode.CROPPED)
    fun buildCroppedBackgroundUri(@Path("id") backgroundId: String, @Param orientation: Int): Uri
}