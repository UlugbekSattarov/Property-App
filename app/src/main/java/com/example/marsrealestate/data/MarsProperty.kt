package com.example.marsrealestate.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

//Parcelize is used to pass a MarsProperty as an argument for a fragment
@Parcelize
@Entity(tableName = "MarsProperties")
data class MarsProperty (
    @PrimaryKey val id: String,
    @Json(name = "img_src") val imgSrcUrl: String,

    val type: String,
    val price: Double,
    val surfaceArea : Float ,
    val latitude : Float,
    val longitude : Float)  : Parcelable {

    val isRental
        get() = type == "rent"

    companion object {

        @JvmField
        val DEFAULT = MarsProperty("0000", "", "rent", 0.0,surfaceArea = 0f,latitude = 0f,longitude = 0f)
    }


}

