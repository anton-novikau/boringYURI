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

import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import boringyuri.sample.data.Address
import boringyuri.sample.uri.ContactUriBuilder
import boringyuri.sample.uri.LocationUriBuilder
import boringyuri.sample.uri.ShowPinsByCoordinatesUriData
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val locationUriBuilder = LocationUriBuilder.create()
    private val contactUriBuilder = ContactUriBuilder.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val uriView = findViewById<TextView>(R.id.uri)
        val dataView = findViewById<TextView>(R.id.data)
        val fetchPhotoButton = findViewById<Button>(R.id.fetch_photo)
        val fetchVCardButton = findViewById<Button>(R.id.fetch_vcard)

        val locationUri = locationUriBuilder.buildShowPinsByCoordinatesUri(
            arrayOf(PIN_COORDINATES_1, PIN_COORDINATES_2)
        )

        Log.d(TAG, "onCreate(): location uri = $locationUri")

        uriView.text = locationUri.toString()
        val uriData = ShowPinsByCoordinatesUriData(locationUri)

        dataView.text = uriData.coordinates.joinToString {
            it?.contentToString() ?: "null"
        }

        val desiredDimens = Rect(
            0,
            0,
            resources.getDimensionPixelSize(R.dimen.desired_photo_width),
            resources.getDimensionPixelSize(R.dimen.desired_photo_height)
        )
        val photoUri = contactUriBuilder.buildContactPhotoUri("friends", PHOTO_CONTACT_ID, desiredDimens)
        fetchPhotoButton.setOnClickListener {
            uriView.text = photoUri.toString()
            dataView.text = getString(R.string.check_logs_message)
            fetchFile(photoUri)
        }

        val vcardUri = contactUriBuilder.buildVCardUri(
            VCARD_CONTACT_ID,
            "John",
            "Doe",
            Address("Minsk", "Independence Ave", "220000")
        )
        fetchVCardButton.setOnClickListener {
            uriView.text = vcardUri.toString()
            dataView.text = getString(R.string.check_logs_message)
            fetchFile(vcardUri)
        }
    }

    private fun fetchFile(uri: Uri) {
        try {
            contentResolver.openInputStream(uri)?.use {
                // Read stream data and convert it to a bitmap or process
                // it however the file should be processed.
                // Of course you must do it in the background thread,
                // not in the click listener's implementation that runs
                // in the main thread.
            }
        } catch (e: IOException) {
            Log.e(TAG, "fetchFile: Unable to open a file $uri", e)
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val VCARD_CONTACT_ID = 100L
        private const val PHOTO_CONTACT_ID = 150L
        private val PIN_COORDINATES_1 = doubleArrayOf(37.773972, -122.431297)
        private val PIN_COORDINATES_2 = doubleArrayOf(53.893009, 27.567444)
    }
}
