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

import androidx.annotation.NonNull;
import androidx.core.util.Pair;

import boringyuri.api.adapter.BoringTypeAdapter;
import boringyuri.api.adapter.TypeAdapter;

public class PairStringIntTypeAdapter implements BoringTypeAdapter<Pair<String, Integer>> {
    @NonNull
    @Override
    public String serialize(@NonNull Pair<String, Integer> original) {
        return original.first + "," + original.second;
    }

    @NonNull
    @Override
    public Pair<String, Integer> deserialize(@NonNull String serialized) {
        String[] parts = serialized.split(",");

        return Pair.create(parts[0], Integer.valueOf(parts[1]));
    }
}
