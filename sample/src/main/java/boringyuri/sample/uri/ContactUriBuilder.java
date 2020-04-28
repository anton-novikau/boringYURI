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

package boringyuri.sample.uri;

import android.content.ContentResolver;
import android.graphics.Rect;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import boringyuri.api.Param;
import boringyuri.api.Path;
import boringyuri.api.UriBuilder;
import boringyuri.api.UriFactory;
import boringyuri.api.WithUriData;
import boringyuri.api.adapter.TypeAdapter;
import boringyuri.sample.data.Address;
import boringyuri.sample.data.BoringContactProvider;
import boringyuri.sample.data.adapter.RectTypeAdapter;

@UriFactory(
        scheme = ContentResolver.SCHEME_CONTENT,
        authority = "boringyuri.sample.provider"
)
public interface ContactUriBuilder {
    @NonNull
    @UriBuilder("/data/{contactId}")
    Uri buildContactDataUri(@Path long contactId);

    @NonNull
    @UriBuilder("/file/photo/{group}/{contactId}")
    @WithUriData
    Uri buildContactPhotoUri(
            @Path @NonNull String group,
            @Path long contactId,
            @Param("desired_dimensions") @TypeAdapter(RectTypeAdapter.class) Rect desiredDimens);

    @NonNull
    @UriBuilder("/file/vcard/{contactId}")
    Uri buildVCardUri(
            @Path long contactId,
            @NonNull @Param String firstName,
            @Nullable @Param String lastName,
            @Nullable @Param("address") Address homeAddress);
}
