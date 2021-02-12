package com.example.marsrealestate.testshared.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.example.marsrealestate.data.Favorite
import com.example.marsrealestate.data.FavoriteProperty
import com.example.marsrealestate.data.MarsProperty
import com.example.marsrealestate.data.MarsRepository
import com.example.marsrealestate.data.network.MarsApiFilter
import java.lang.Exception
import java.util.*

class FakeTestRepository : MarsRepository {

    /**
     * The [FakeTestRepository] will throw [Exception] on [getProperties]
     */
    var willThrowExceptionForTesting = false

    private var properties = generateProperties()
    private var favorites = generateFavorites()

    val observableProperties = MutableLiveData<List<MarsProperty>>().apply {
        value = properties
    }
    val observableFavorites = MutableLiveData<List<FavoriteProperty>>().apply {
        value = favorites
    }


    private fun generateProperties() : MutableList<MarsProperty> {
        val random = Random()
        return MutableList<MarsProperty>(100) {
            val type = if (it %2 == 0) "rent" else "buy"
            MarsProperty(it.toString(),"",type,random.nextDouble())
        }
    }

    private fun refreshProperties() {
        observableProperties.value = properties
    }

    private fun generateFavorites() : MutableList<FavoriteProperty> {
        val random = Random()
        return mutableListOf<FavoriteProperty>().apply {
            for (i in 0..(properties.size / 2)) {
                val f = Favorite(properties[i].id, Date(random.nextLong()))
                add(FavoriteProperty(properties[i], f))
            }
        }
    }

    private fun refreshFavorites() {
        observableFavorites.value = favorites
    }


   fun setPropertiesDataset( props : List<MarsProperty>) {
       properties = props.toMutableList()
       refreshProperties()
   }

    fun setFavoritesDataset( favs : List<FavoriteProperty>) {
       favorites = favs.toMutableList()
       refreshFavorites()
   }



    override suspend fun getProperties(filter: MarsApiFilter): List<MarsProperty> {
        if (willThrowExceptionForTesting)
            throw Exception("Exception throwed for testing")
        return properties
    }

    override suspend fun getProperty(id: String): MarsProperty? {
        return properties.find { it.id == id }
    }

    override fun observeProperty(id: String): LiveData<MarsProperty?> {
        return MutableLiveData(properties.find { it.id == id })
    }

    override fun observeFavorites(): LiveData<List<FavoriteProperty>> {
        return observableFavorites
    }

    override suspend fun getFavorites(): List<FavoriteProperty> {
        if (willThrowExceptionForTesting)
            throw Exception("Exception throwed for testing")
        return favorites
    }

    override suspend fun saveToFavorite(property: MarsProperty, dateFavorited: Date?) {
        val f = Favorite(property.id,Date())
        favorites.add(FavoriteProperty(property,f))
        refreshFavorites()
    }



    override suspend fun removeFromFavorite(propertyId: String) {
        favorites.removeIf { it.property.id == propertyId }
    }

    override suspend fun isFavorite(propertyId: String): Boolean {
        return favorites.any { it.property.id == propertyId }
    }

    override fun observeIsFavorite(propertyId: String): LiveData<Boolean> =
        observeFavorites()
        .map { props -> props.find { p : FavoriteProperty -> p.property.id == propertyId} != null }
}