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

package boringyuri.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import boringyuri.sample.uri.LocationUriBuilder
import boringyuri.sample.uri.ShowPinsByCoordinatesUriData

class MainActivity : AppCompatActivity() {

    private val uriBuilder = LocationUriBuilder.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val locationUri = uriBuilder.buildShowPinsByCoordinatesUri(
            arrayOf(
                doubleArrayOf(37.773972, -122.431297),
                doubleArrayOf(53.893009, 27.567444)
            )
        )

        Log.d(TAG, "onCreate(): Uri = $locationUri")
        findViewById<TextView>(R.id.uri).text = locationUri.toString()

        val uriData = ShowPinsByCoordinatesUriData(locationUri)
        findViewById<TextView>(R.id.data).text = uriData.coordinates.joinToString {
            it?.contentToString() ?: "null"
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
