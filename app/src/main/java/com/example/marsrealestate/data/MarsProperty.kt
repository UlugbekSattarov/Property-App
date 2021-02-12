package com.example.marsrealestate.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

//Parcelize is used to pass a MarsProperty as an argument for a fragment
@Parcelize
@Entity(tableName = "MarsProperties")
data class MarsProperty (
    @PrimaryKey val id: String,
    @Json(name = "img_src") val imgSrcUrl: String,

    val type: String,
    val price: Double)  : Parcelable {

    val isRental
        get() = type == "rent"

}

