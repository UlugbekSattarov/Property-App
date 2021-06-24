package com.example.marsrealestate.data

import androidx.lifecycle.LiveData
import com.example.marsrealestate.data.database.MarsPropertyDAO
import com.example.marsrealestate.data.network.MarsApiService
import com.example.marsrealestate.data.query.MarsApiQuery
import com.example.marsrealestate.data.query.MarsApiSorting
import com.example.marsrealestate.util.Result
import kotlinx.coroutines.delay
import java.util.*

class MarsRepositoryImpl(private val remoteDataSource: MarsApiService,
                         private val localDataSource : MarsPropertyDAO) : MarsRepository {

    override suspend fun login(username: String, password: String): String {
        delay(1000)
        return username
    }




    override suspend fun getProperties(query: MarsApiQuery): List<MarsProperty> {
        delay(1000)
        return remoteDataSource.getProperties(query)
    }

    override suspend fun getProperty(id: String): MarsProperty?  {
        delay(1000)
        return remoteDataSource.getProperty(id)
    }

    override suspend fun addProperty(property: MarsProperty) : MarsProperty {
        delay(1000)
        return remoteDataSource.addProperty(property)
    }



    override fun observeFavorites(): LiveData<List<FavoriteProperty>> =
        localDataSource.observeFavorites()

    override suspend fun getFavorites(): List<FavoriteProperty> =
        localDataSource.getFavoriteProperties()


    override suspend fun saveToFavorite(property: MarsProperty, dateFavorited : Date?) =
        localDataSource.addToFavorite(property,dateFavorited)

    override suspend fun removeFromFavorite(propertyId: String) =
        localDataSource.removeFromFavorite(propertyId)

    override suspend fun isFavorite(propertyId: String) : Boolean =
        localDataSource.getFavoriteFromPropertyId(propertyId) != null

    override fun observeIsFavorite(propertyId: String): LiveData<Boolean>  =
        localDataSource.observeIsFavorite(propertyId)
}




