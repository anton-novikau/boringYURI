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

import android.net.Uri;

import androidx.annotation.NonNull;

import boringyuri.api.Param;
import boringyuri.api.Path;
import boringyuri.api.UriBuilder;
import boringyuri.api.UriFactory;
import boringyuri.api.adapter.TypeAdapter;
import boringyuri.sample.data.User;
import boringyuri.sample.data.adapter.AdminTypeAdapter;

@UriFactory(scheme = "https", authority = "example.com")
public interface UserProviderUriBuilder {

    @NonNull
    @UriBuilder("/user")
    Uri buildUserUri(
            @Param("name") String name,
            @Param("phone_number") String phoneNumber);

    @NonNull
    @UriBuilder("/user/{userId}")
    Uri buildUserUri(@Path int userId);

    @NonNull
    @UriBuilder("/user")
    Uri buildUserUri(@Param("data") @NonNull User user);

    @NonNull
    @UriBuilder("/admin")
    Uri buildAdminUri(
            @Param("data")
            @TypeAdapter(AdminTypeAdapter.class)
            @NonNull User user);

    @NonNull
    @UriBuilder("/user/{id}/photo")
    Uri buildUserPhotoUri(@Path("id") int userId);

    @NonNull
    @UriBuilder("/user/save")
    Uri buildSaveUsersUri(@Param("user") @NonNull User[] users);

    @NonNull
    @UriBuilder("/admin/save")
    Uri buildSaveAdminsUri(
            @Param("user")
            @TypeAdapter(AdminTypeAdapter.class)
            @NonNull User[] users);

    @NonNull
    static UserProviderUriBuilder create() {
        return new UserProviderUriBuilderImpl();
    }

}
