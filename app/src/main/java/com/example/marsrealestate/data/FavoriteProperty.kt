package com.example.marsrealestate.data

import androidx.room.*
import java.util.*


@Entity
data class Favorite(val propertyId : String,
                    val dateFavorited : Date,
                    @PrimaryKey  val favoriteId : String = UUID.randomUUID().toString())


data class FavoriteProperty(
    @Embedded val property : MarsProperty,
    @Relation(parentColumn = "id",entityColumn = "propertyId")
    val favorite: Favorite
)


