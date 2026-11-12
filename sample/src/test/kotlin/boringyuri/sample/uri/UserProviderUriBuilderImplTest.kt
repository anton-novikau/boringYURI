/*
 * Copyright 2026 Anton Novikau
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

import android.net.Uri
import boringyuri.sample.data.User
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UserProviderUriBuilderImplTest {

    private lateinit var baseUri: String
    private lateinit var uriBuilder: UserProviderUriBuilder

    @Before
    fun setUp() {
        baseUri = "https://example.com"
        uriBuilder = UserProviderUriBuilderImpl()
    }

    @Test
    fun buildUserUri_NameAndPhoneNumber() {
        val expected = Uri.parse("$baseUri/user?name=John%20Doe&phone_number=%2B15417543010")
        val actual = uriBuilder.buildUserUri("John Doe", "+15417543010")

        assertEquals(expected, actual)
    }

    @Test
    fun buildUserUri_NullNameAndPhoneNumber_ParamsAreOmitted() {
        val expected = Uri.parse("$baseUri/user")
        val actual = uriBuilder.buildUserUri(null, null)

        assertEquals(expected, actual)
    }

    @Test
    fun buildUserUri_UserId() {
        val expected = Uri.parse("$baseUri/user/42")
        val actual = uriBuilder.buildUserUri(42)

        assertEquals(expected, actual)
    }

    @Test
    fun buildUserUri_User_UsesTypeAdapterOfTheClass() {
        // User is annotated with @TypeAdapter(UserTypeAdapter::class) which joins the data with ';'
        val expected = Uri.parse("$baseUri/user?data=42%3BJohn%20Doe")
        val actual = uriBuilder.buildUserUri(User(42, "John Doe"))

        assertEquals(expected, actual)
    }

    @Test
    fun buildAdminUri_UsesTypeAdapterOfTheParameter() {
        // @TypeAdapter(AdminTypeAdapter::class) at use overrides the one of the User class
        // and joins the data with ','
        val expected = Uri.parse("$baseUri/admin?data=42%2CJohn%20Doe")
        val actual = uriBuilder.buildAdminUri(User(42, "John Doe"))

        assertEquals(expected, actual)
    }

    @Test
    fun buildUserPhotoUri() {
        val expected = Uri.parse("$baseUri/user/42/photo")
        val actual = uriBuilder.buildUserPhotoUri(42)

        assertEquals(expected, actual)
    }

    @Test
    fun buildSaveUsersUri() {
        val users = arrayOf(User(1, "John"), User(2, "Jane"))
        val expected = Uri.parse("$baseUri/user/save?user=1%3BJohn&user=2%3BJane")
        val actual = uriBuilder.buildSaveUsersUri(users)

        assertEquals(expected, actual)
    }

    @Test
    fun buildSaveUsersUri_NullElementsAreOmitted() {
        val users = arrayOf(User(1, "John"), null, User(2, "Jane"))
        val expected = Uri.parse("$baseUri/user/save?user=1%3BJohn&user=2%3BJane")
        val actual = uriBuilder.buildSaveUsersUri(users)

        assertEquals(expected, actual)
    }

    @Test
    fun buildSaveAdminsUri_UsesTypeAdapterOfTheParameter() {
        val users = arrayOf(User(1, "John"), User(2, "Jane"))
        val expected = Uri.parse("$baseUri/admin/save?user=1%2CJohn&user=2%2CJane")
        val actual = uriBuilder.buildSaveAdminsUri(users)

        assertEquals(expected, actual)
    }
}
