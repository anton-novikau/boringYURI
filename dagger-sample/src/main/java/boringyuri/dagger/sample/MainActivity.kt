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

package boringyuri.dagger.sample

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import boringyuri.dagger.sample.data.User
import boringyuri.dagger.sample.uri.UserProviderUriBuilder
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var uriBuilder: UserProviderUriBuilder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userUri = uriBuilder.buildUserUri(User(USER_ID, "John Doe"))

        Log.d(TAG, "onCreate(): user Uri = $userUri")
        findViewById<TextView>(R.id.uri).text = userUri.toString()
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val USER_ID = 42
    }
}
