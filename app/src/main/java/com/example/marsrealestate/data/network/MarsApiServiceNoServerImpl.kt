package com.example.marsrealestate.data.network

import android.util.Log
import com.example.marsrealestate.R
import com.example.marsrealestate.data.MarsProperty
import com.example.marsrealestate.data.query.MarsApiQuery
import com.example.marsrealestate.data.query.MarsApiSorting
import kotlinx.coroutines.*
import kotlin.random.Random


class MarsApiServiceNoServerImpl(private val dao : MarsRemotePropertyDAO) : MarsApiService {

    private val images = arrayOf(
        R.drawable.mars_landscape_1,
        R.drawable.mars_landscape_2,
        R.drawable.mars_landscape_3,
        R.drawable.mars_landscape_4,
        R.drawable.mars_landscape_5,
        R.drawable.mars_landscape_6
    )

    private val types = arrayOf(
        MarsProperty.TYPE_BUY,
        MarsProperty.TYPE_RENT,
        MarsProperty.TYPE_RENT,
        MarsProperty.TYPE_BUY,
        MarsProperty.TYPE_RENT,
        MarsProperty.TYPE_BUY,
        MarsProperty.TYPE_BUY,
        MarsProperty.TYPE_RENT)

    val properties by lazy {
        List(30) { MarsProperty("${it +140_000}",
            "${images[it%images.size]}",
            types[it%types.size],
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


    suspend fun getPropertyInMemory(id: String): MarsProperty? = properties.firstOrNull { it.id == id }



    override suspend fun getProperties(
        query: MarsApiQuery,
        sortedBy: MarsApiSorting
    ): List<MarsProperty> {
        return dao.getProperties(query,sortedBy)
    }

    override suspend fun getProperty(id: String): MarsProperty? = dao.getProperty(id)

    override suspend fun addProperty(marsProperty: MarsProperty) =
        dao.addNewProperty(marsProperty)

    override suspend fun removeProperty(propertyId: String) =
        dao.removeProperty(propertyId)
}



