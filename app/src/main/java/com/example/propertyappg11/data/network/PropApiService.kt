package com.example.propertyappg11.data.network

import com.example.propertyappg11.data.MarsProperty
import com.example.propertyappg11.data.query.MarsApiQuery


interface MarsApiService {

    suspend fun getProperties(query: MarsApiQuery): List<MarsProperty>

    suspend fun getProperty(id: String): MarsProperty

    suspend fun addProperty(marsProperty: MarsProperty) : MarsProperty

    suspend fun removeProperty(propertyId: String)


}



