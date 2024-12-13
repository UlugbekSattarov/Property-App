package com.example.propertyappg11.data.network

import android.util.Log
import com.example.propertyappg11.data.MarsProperty
import com.example.propertyappg11.data.query.MarsApiQuery
import com.example.propertyappg11.data.query.MarsApiSorting
import kotlinx.coroutines.*
import kotlin.random.Random


class MarsApiServiceNoServerImpl(private val dao : MarsRemotePropertyDAO,
                                 private val imageUrls : List<String>) : MarsApiService {


    private val types = arrayOf(
        MarsProperty.TYPE_BUY,
        MarsProperty.TYPE_RENT)

    val properties by lazy {
        List(30) { MarsProperty("${it +140_000}",
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
                Log.d(MarsApiServiceNoServerImpl::class.qualifiedName,"Added ${properties.count()} properties to database" )
            }
        }
    }


    @Suppress("unused")
    suspend fun getPropertiesInMemory(query : MarsApiQuery, sortedBy : MarsApiSorting): List<MarsProperty> {
        delay(1500)


        return properties
            .filter { p -> query.filter?.matches(p) ?: true }
            .run {
                when (sortedBy) {
                    MarsApiSorting.PriceAscending -> sortedBy { p -> p.price}
                    MarsApiSorting.PriceDescending -> sortedByDescending { p -> p.price}
                    else -> this
                }
            }
            .drop(query.itemsPerPage * (query.pageNumber -1))
            .take(query.itemsPerPage)
            .toList()
    }

    @Suppress("unused")
    fun getPropertyInMemory(id: String): MarsProperty? = properties.firstOrNull { it.id == id }



    override suspend fun getProperties(
        query: MarsApiQuery
    ): List<MarsProperty> {
        return dao.getProperties(query)
    }

    override suspend fun getProperty(id: String): MarsProperty = dao.getProperty(id)

    override suspend fun addProperty(marsProperty: MarsProperty) =
        dao.addNewProperty(marsProperty)

    override suspend fun removeProperty(propertyId: String) =
        dao.removeProperty(propertyId)
}



