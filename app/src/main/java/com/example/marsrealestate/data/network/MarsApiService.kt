package com.example.marsrealestate.data.network

import com.example.marsrealestate.data.MarsProperty
import retrofit2.http.GET
import retrofit2.http.Query


data class MarsApiFilter(private val type: MarsPropertyType = MarsPropertyType.ALL,
                         private val queryString: String = "") {

    enum class MarsPropertyType(val value: String) { ALL("all"), RENT("rent"), BUY("buy") }

    fun matches(property: MarsProperty) : Boolean{
        val isCorrectType =
            if (type == MarsPropertyType.ALL) true
            else property.type == type.value

        val nameMatches =
            if (queryString.isBlank()) true
            else property.id.contains(queryString)

        return isCorrectType && nameMatches
    }

}

enum class MarsApiPropertySorting { PriceAscending, PriceDescending}

class MarsApiQuery(val pageNumber: Int = 1,
                   val itemsPerPage: Int = 10,
                   val filter: MarsApiFilter? = null) {
}


object MarsApiServiceData {
    const val BASE_URL = "https://android-kotlin-fun-ot_mars-server.appspot.com/"
}


interface MarsApiService {

//    @GET("realestate")
//    suspend fun getProperties(@Query("filter") type: String): List<MarsProperty>


    suspend fun getProperties( query: MarsApiQuery, sortedBy : MarsApiPropertySorting = MarsApiPropertySorting.PriceAscending): List<MarsProperty>


}



