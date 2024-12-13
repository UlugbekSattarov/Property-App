package com.example.propertyappg11.data.network

import android.util.Log
import com.example.propertyappg11.data.PropProperty
import com.example.propertyappg11.data.query.PropApiQuery
import com.example.propertyappg11.data.query.PropApiSorting
import kotlinx.coroutines.*
import kotlin.random.Random


class PropApiServiceNoServerImpl(private val dao : PropRemotePropertyDAO,
                                 private val imageUrls : List<String>) : PropApiService {


    private val types = arrayOf(
        PropProperty.TYPE_BUY,
        PropProperty.TYPE_RENT)

    val properties by lazy {
        List(30) { PropProperty("${it +140_000}",
            if (imageUrls.isNotEmpty()) imageUrls[it%imageUrls.size] else "",
            types.random(),
            (100_000.0 + (0..200_000).random()) / (if (types[it%types.size] == "rent") 10 else 1) ,
            surfaceArea = (Random.nextFloat() * 50) + 0.2f,
            latitude = (Random.nextFloat() * 180) - 90,
            longitude = (Random.nextFloat() * 360)
        ) }.asSequence()
    }


    init {
        runBlocking {
            if (dao.getPropertiesCount() == 0) {
                dao.insert(properties.toList())
                Log.d(PropApiServiceNoServerImpl::class.qualifiedName,"Added ${properties.count()} properties to database" )
            }
        }
    }


    @Suppress("unused")
    suspend fun getPropertiesInMemory(query : PropApiQuery, sortedBy : PropApiSorting): List<PropProperty> {
        delay(1500)


        return properties
            .filter { p -> query.filter?.matches(p) ?: true }
            .run {
                when (sortedBy) {
                    PropApiSorting.PriceAscending -> sortedBy { p -> p.price}
                    PropApiSorting.PriceDescending -> sortedByDescending { p -> p.price}
                    else -> this
                }
            }
            .drop(query.itemsPerPage * (query.pageNumber -1))
            .take(query.itemsPerPage)
            .toList()
    }

    @Suppress("unused")
    fun getPropertyInMemory(id: String): PropProperty? = properties.firstOrNull { it.id == id }



    override suspend fun getProperties(
        query: PropApiQuery
    ): List<PropProperty> {
        return dao.getProperties(query)
    }

    override suspend fun getProperty(id: String): PropProperty = dao.getProperty(id)

    override suspend fun addProperty(propProperty: PropProperty) =
        dao.addNewProperty(propProperty)

    override suspend fun removeProperty(propertyId: String) =
        dao.removeProperty(propertyId)
}



