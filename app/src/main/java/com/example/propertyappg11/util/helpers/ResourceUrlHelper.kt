package com.example.propertyappg11.util.helpers

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.example.propertyappg11.R

object ResourceUrlHelper {

    fun getAllLandscapesUrl(context: Context) : List<String> =
        listOf(R.drawable.img1,
            R.drawable.img2,
            R.drawable.img3,
            R.drawable.img4,
            R.drawable.img5,
            R.drawable.img1,
        ).map { getResourceAsUrl(context,it) }


    fun getResourceAsUrl(context: Context, resourceId : Int) : String =
        Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(context.packageName)
            .appendPath(context.resources.getResourceTypeName(resourceId))
            .appendPath(context.resources.getResourceEntryName(resourceId))
            .build().toString()

}
