package com.example.marsrealestate.data.network

import com.example.marsrealestate.R
import com.example.marsrealestate.data.MarsProperty
import kotlinx.coroutines.delay
import kotlin.random.Random


class MarsApiServiceNoServerImpl : MarsApiService {

    private val images = arrayOf(
        R.drawable.mars_landscape_1,
        R.drawable.mars_landscape_2,
        R.drawable.mars_landscape_3,
        R.drawable.mars_landscape_4,
        R.drawable.mars_landscape_5,
        R.drawable.mars_landscape_6
    )

    private val types = arrayOf("buy","rent","rent","buy","rent","buy","buy","rent")

    val properties by lazy {
        List(30) { MarsProperty("${it +140_000}",
            "${images[it%images.size]}",
            types[it%types.size],
            100_000.0 + (0..200_000).random(),
            surfaceArea = (Random.nextFloat() * 50) + 0.2f,
            latitude = (Random.nextFloat() * 180) - 90,
            longitude = (Random.nextFloat() * 360)
            ) }.asSequence()
    }


    override suspend fun getProperties(query : MarsApiQuery, sortedBy : MarsApiPropertySorting ): List<MarsProperty> {
        delay(1500)


        return properties
            .filter { p -> query.filter?.matches(p) ?: true }
            .run {
                if (sortedBy == MarsApiPropertySorting.PriceAscending)
                     sortedBy { p -> p.price}
                else
                    sortedByDescending { p -> p.price}
            }
            .drop(query.itemsPerPage * (query.pageNumber -1))
            .take(query.itemsPerPage)
            .toList()
    }
}



