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
import boringyuri.api.matcher.MatcherCode
import boringyuri.api.matcher.WithUriMatcher
import boringyuri.sample.BuildConfig

@UriFactory(scheme = ContentResolver.SCHEME_CONTENT, authority = "boringyuri.sample.backgrounds")
@WithUriMatcher("boringyuri.sample.uri.matcher.BackgroundUriMatcher")
interface BackgroundProviderUriBuilder {
    object Contract {
        const val CODE_COLOR = 100
        const val CODE_ORIGINAL = 101
        const val CODE_CROPPED = 102
        const val CODE_DEBUG = 103
    }

    @UriBuilder("/bg/color/{color}")
    @MatcherCode(Contract.CODE_COLOR)
    fun buildColorBackgroundUri(@ColorInt @Path color: Int): Uri

    @UriBuilder("/bg/original/{id}")
    @MatcherCode(Contract.CODE_ORIGINAL)
    fun buildGalleryBackgroundUri(@Path id: Int): Uri

    @UriBuilder("/bg/thumbnail/{id}")
    @MatcherCode(Contract.CODE_CROPPED)
    @WithUriData("boringyuri.sample.data.CroppedBackgroundData")
    fun buildCroppedBackgroundUri(@Path("id") backgroundId: String, @Param orientation: Int): Uri

    @UriBuilder("/bg/debug")
    // You can't use BuildConfig.DEBUG here because android generates
    // it as Boolean.parseBoolean("true") which is not a constant value
    // so it can't be accepted as an annotation parameter.
    @MatcherCode(Contract.CODE_DEBUG, enabled = BuildConfig.DEBUG_ONLY)
    fun buildDebugBackgroundUri(): Uri
}