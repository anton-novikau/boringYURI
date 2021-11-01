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

package boringyuri.sample.data

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Log
import boringyuri.sample.uri.ContactPhotoUriData
import boringyuri.sample.uri.ContactUriMatcher
import boringyuri.sample.uri.VCardUriData
import java.io.File

class BoringContactProvider : ContentProvider() {

    private val uriMatcher = ContactUriMatcher()

    override fun onCreate(): Boolean {
        return true
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        throw UnsupportedOperationException("not implemented")
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        throw UnsupportedOperationException("not implemented")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        throw UnsupportedOperationException("not implemented")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getType(uri: Uri): String? {
        throw UnsupportedOperationException("not implemented")
    }

    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? = context?.let {
        when (uriMatcher.match(uri)) {
            ContactUriMatcher.MatcherCode.CONTACT_PHOTO -> openContactPhoto(uri, it)
            ContactUriMatcher.MatcherCode.VCARD -> openContactVCard(uri, it)
            else -> {
                Log.d(TAG, "openFile: Unknown or unsupported uri is trying to open: $uri")
                null
            }
        }
    }

    private fun openContactPhoto(uri: Uri, context: Context): ParcelFileDescriptor? {
        Log.i(TAG, "openContactPhoto: uri = $uri")

        val uriData = ContactPhotoUriData(uri)
        val photoFile = File(
            context.getExternalFilesDir(uriData.group),
            uriData.contactId.toString()
        )

        val desiredDimens = uriData.desiredDimens
        Log.i(
            TAG,
            "openContactPhoto: contactId = ${uriData.contactId}, group = ${uriData.group}," +
                    " desiredDimens = ${desiredDimens.width()}x${desiredDimens.height()}"
        )

        // pre-process 'photoFile' bitmap to have a desired width and height
        // based on 'uriData.desiredDimens'

        return ParcelFileDescriptor.open(photoFile, ParcelFileDescriptor.MODE_READ_ONLY)
    }

    private fun openContactVCard(uri: Uri, context: Context): ParcelFileDescriptor? {
        Log.i(TAG, "openContactVCard: uri = $uri")

        val vcard = VCardUriData(uri)
        // obtain vcard data from uri and find an appropriate vcf file if exists
        val vcardFile = File(
            context.getExternalFilesDir("vcard"),
            vcard.contactId.toString()
        )
        Log.i(
            TAG,
            "openContactVCard: id = ${vcard.contactId}," +
                    " name = ${vcard.firstName} ${vcard.lastName}," +
                    " address = ${vcard.homeAddress}"
        )

        return ParcelFileDescriptor.open(vcardFile, ParcelFileDescriptor.MODE_READ_ONLY)
    }

    companion object {
        private const val TAG = "BoringContactProvider"
    }
}