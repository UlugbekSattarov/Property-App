package com.example.propertyappg11.data

import androidx.lifecycle.LiveData
import com.example.propertyappg11.data.query.PropApiQuery
import java.util.*

interface PropRepository {

    suspend fun login(username : String, password : String) : String

    suspend fun getProperties(query: PropApiQuery) : List<PropProperty>
    suspend fun getProperty(id: String) : PropProperty
    suspend fun addProperty(property: PropProperty) : PropProperty

    fun observeFavorites() : LiveData<List<FavoriteProperty>>
    suspend fun getFavorites() : List<FavoriteProperty>
    suspend fun saveToFavorite(property : PropProperty, dateFavorited : Date? = null)
    suspend fun removeFromFavorite(propertyId: String)
    suspend fun isFavorite(propertyId : String) : Boolean
    fun observeIsFavorite(propertyId : String) : LiveData<Boolean>


}
