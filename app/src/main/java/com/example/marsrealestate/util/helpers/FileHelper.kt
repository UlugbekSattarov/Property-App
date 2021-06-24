package com.example.marsrealestate.util.helpers

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import java.text.SimpleDateFormat
import java.util.*

object FileHelper {

    private fun generateJpegFilename() : String {

        val prefix = "MarsRealEstate--"
        val date = SimpleDateFormat("yyyy-MM-dd--HH-mm-ss", Locale.getDefault()).format(Date())
        val suffix = ".jpg"

        return "$prefix$date$suffix"
    }

    /**
     * Adds an empty .jpg file to the  MediaStore and returns the [Uri] of the newly created file or
     * null if the operation was not successful.
     * */
    fun addEmptyImageToMediaStore(contentResolver : ContentResolver) : Uri? {
        val newFileName = generateJpegFilename()

        val newFile = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, newFileName)
        }

        val mediaStoreUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        return contentResolver.insert(mediaStoreUri,newFile)
    }


    fun deleteFile(contentResolver : ContentResolver,fileUri: Uri)  =
        contentResolver.delete(fileUri,null,null)



    fun markMarkAsPermanentlyAvailable(contentResolver : ContentResolver,fileUri : Uri) =
        contentResolver.takePersistableUriPermission(fileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)


}