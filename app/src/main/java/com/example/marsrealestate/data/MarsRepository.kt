package com.example.marsrealestate.data

import androidx.lifecycle.LiveData
import com.example.marsrealestate.data.network.MarsApiFilter
import com.example.marsrealestate.data.network.MarsApiPropertySorting
import com.example.marsrealestate.data.network.MarsApiQuery
import java.util.*

interface MarsRepository {

    suspend fun getProperties(query: MarsApiQuery,sortedBy : MarsApiPropertySorting = MarsApiPropertySorting.PriceAscending) : List<MarsProperty>
    suspend fun getProperty(id: String) : MarsProperty?
    fun observeProperty(id: String) : LiveData<MarsProperty?>

    fun observeFavorites() : LiveData<List<FavoriteProperty>>
    suspend fun getFavorites() : List<FavoriteProperty>
    suspend fun saveToFavorite(property : MarsProperty, dateFavorited : Date? = null)
    suspend fun removeFromFavorite(propertyId: String)
    suspend fun isFavorite(propertyId : String) : Boolean
    fun observeIsFavorite(propertyId : String) : LiveData<Boolean>


}