package com.example.marsrealestate.data.network

import com.example.marsrealestate.data.MarsProperty
import com.example.marsrealestate.data.query.MarsApiQuery
import com.example.marsrealestate.data.query.MarsApiSorting


interface MarsApiService {

//    companion object {
//        const val BASE_URL = "https://android-kotlin-fun-ot_mars-server.appspot.com/"
//
//    }

//    @GET("realestate")
//    suspend fun getProperties(@Query("filter") type: String): List<MarsProperty>


    suspend fun getProperties(query: MarsApiQuery): List<MarsProperty>

    suspend fun getProperty(id: String): MarsProperty

    suspend fun addProperty(marsProperty: MarsProperty) : MarsProperty

    suspend fun removeProperty(propertyId: String)


}



