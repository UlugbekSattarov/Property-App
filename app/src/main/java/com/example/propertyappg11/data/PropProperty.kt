package com.example.propertyappg11.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

//Parcelize is used to pass a MarsProperty as an argument for a fragment
@Parcelize
@Entity(tableName = "MarsProperties")
data class MarsProperty (
    @PrimaryKey val id: String,
    val imgSrcUrl: String,
    val type: String,
    val price: Double,
    val surfaceArea : Float ,
    val latitude : Float,
    val longitude : Float)  : Parcelable {

    val isRental
        get() = type == TYPE_RENT

    companion object {

        const val TYPE_RENT = "rent"
        const val TYPE_BUY = "buy"

        @JvmField
        val DEFAULT = MarsProperty("", "", TYPE_BUY, 0.0,surfaceArea = 0f,latitude = 0f,longitude = 0f)

    }
}

fun String.isValidPropertyType() = this == MarsProperty.TYPE_BUY || this == MarsProperty.TYPE_RENT

