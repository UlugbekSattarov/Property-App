package com.example.marsrealestate.data

import androidx.room.*
import java.util.*


@Entity
data class Favorite(val propertyId : String,
                    val dateFavorited : Date,
                    @PrimaryKey(autoGenerate = true) val favoriteId : Int = 0)


data class FavoriteProperty(
    @Embedded val property : MarsProperty,
    @Relation(parentColumn = "id",entityColumn = "propertyId")
    val favorite: Favorite
)


