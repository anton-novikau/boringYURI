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

package boringyuri.sample.data.adapter;

import android.graphics.Rect;

import androidx.annotation.NonNull;

import java.util.Objects;

import boringyuri.api.adapter.BoringTypeAdapter;

public class RectTypeAdapter implements BoringTypeAdapter<Rect> {
    @NonNull
    @Override
    public String serialize(@NonNull Rect rect) {
        return rect.flattenToString();
    }

    @NonNull
    @Override
    public Rect deserialize(@NonNull String serialized) {
        return Objects.requireNonNull(Rect.unflattenFromString(serialized));
    }
}
