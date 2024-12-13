package com.example.propertyappg11.util.helpers

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.example.propertyappg11.R

object ResourceUrlHelper {

    fun getAllLandscapesUrl(context: Context) : List<String> =
        listOf(R.drawable.mars_landscape_1,
            R.drawable.mars_landscape_2,
            R.drawable.mars_landscape_3,
            R.drawable.mars_landscape_4,
            R.drawable.mars_landscape_5,
            R.drawable.mars_landscape_6,
        ).map { getResourceAsUrl(context,it) }


    fun getResourceAsUrl(context: Context, resourceId : Int) : String =
        Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(context.packageName)
            .appendPath(context.resources.getResourceTypeName(resourceId))
            .appendPath(context.resources.getResourceEntryName(resourceId))
            .build().toString()

}
