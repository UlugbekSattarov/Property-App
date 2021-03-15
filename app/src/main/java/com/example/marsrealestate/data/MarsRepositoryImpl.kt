package com.example.marsrealestate.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.marsrealestate.data.database.MarsPropertyDAO
import com.example.marsrealestate.data.network.MarsApiFilter
import com.example.marsrealestate.data.network.MarsApiPropertySorting
import com.example.marsrealestate.data.network.MarsApiQuery
import com.example.marsrealestate.data.network.MarsApiService
import java.util.*

class MarsRepositoryImpl(private val remoteDataSource: MarsApiService,
                         private val localDataSource : MarsPropertyDAO) : MarsRepository {


    override suspend fun getProperties(query: MarsApiQuery,sortedBy : MarsApiPropertySorting ): List<MarsProperty> =
        remoteDataSource.getProperties(query,sortedBy)

    override suspend fun getProperty(id: String): MarsProperty? = remoteDataSource.getProperty(id)

    override fun observeProperty(id: String): LiveData<MarsProperty?> = localDataSource.observeProperty(id)


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
