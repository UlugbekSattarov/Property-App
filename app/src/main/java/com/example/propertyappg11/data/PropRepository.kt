package com.example.propertyappg11.data

import androidx.lifecycle.LiveData
import com.example.propertyappg11.data.query.MarsApiQuery
import java.util.*

interface MarsRepository {

    suspend fun login(username : String, password : String) : String

    suspend fun getProperties(query: MarsApiQuery) : List<MarsProperty>
    suspend fun getProperty(id: String) : MarsProperty
    suspend fun addProperty(property: MarsProperty) : MarsProperty

    fun observeFavorites() : LiveData<List<FavoriteProperty>>
    suspend fun getFavorites() : List<FavoriteProperty>
    suspend fun saveToFavorite(property : MarsProperty, dateFavorited : Date? = null)
    suspend fun removeFromFavorite(propertyId: String)
    suspend fun isFavorite(propertyId : String) : Boolean
    fun observeIsFavorite(propertyId : String) : LiveData<Boolean>


}
