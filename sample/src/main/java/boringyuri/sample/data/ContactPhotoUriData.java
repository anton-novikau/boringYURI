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

package boringyuri.sample.data;

import android.graphics.Rect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import boringyuri.api.Param;
import boringyuri.api.Path;
import boringyuri.api.UriData;
import boringyuri.api.adapter.TypeAdapter;
import boringyuri.sample.data.adapter.RectTypeAdapter;

@UriData
public interface ContactPhotoUriData {
    @NonNull
    @Path
    String getGroup();

    @Path
    long getContactId();

    @Nullable
    @Param("desired_dimensions")
    @TypeAdapter(RectTypeAdapter.class)
    Rect getDesiredDimens();
}
