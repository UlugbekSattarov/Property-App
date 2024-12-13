package com.example.propertyappg11.testshared.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.example.propertyappg11.data.Favorite
import com.example.propertyappg11.data.FavoriteProperty
import com.example.propertyappg11.data.MarsProperty
import com.example.propertyappg11.data.MarsRepository
import com.example.propertyappg11.data.query.MarsApiQuery
import com.example.propertyappg11.data.query.MarsApiSorting
import com.example.propertyappg11.util.Result
import java.lang.Exception
import java.util.*
import kotlin.NoSuchElementException

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
            MarsProperty(it.toString(),"",type,random.nextDouble(),45.2f,56f,250f)
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

    override suspend fun login(username: String, password: String): String {
        return username
    }




    override suspend fun getProperties(query: MarsApiQuery): List<MarsProperty> {
        if (willThrowExceptionForTesting)
            throw Exception("Exception throwed for testing")
        return properties
            .filter { p -> query.filter?.matches(p) ?: true }
            .run {
                when (query.sortedBy) {
                    MarsApiSorting.PriceAscending -> sortedBy { p -> p.price}
                    MarsApiSorting.PriceDescending -> sortedByDescending { p -> p.price}
                    else -> this
                }
            }
            .drop(query.itemsPerPage * (query.pageNumber -1))
            .take(query.itemsPerPage)
            .toList()
    }



    override suspend fun getProperty(id: String): MarsProperty {
        if (willThrowExceptionForTesting)
            throw Exception("Exception throwed for testing")
        return properties.find { it.id == id } ?: throw NoSuchElementException("No property found with id $id")
    }

    override suspend fun addProperty(property: MarsProperty) : MarsProperty {
        properties.add(property)
        return property
    }

//    override fun observeProperty(id: String): LiveData<MarsProperty?> {
//        if (willThrowExceptionForTesting)
//            throw Exception("Exception throwed for testing")
//        return MutableLiveData(properties.find { it.id == id })
//    }

    override fun observeFavorites(): LiveData<List<FavoriteProperty>> {
        if (willThrowExceptionForTesting)
            throw Exception("Exception throwed for testing")
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
