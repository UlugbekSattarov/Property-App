package com.example.propertyappg11.data.network

import com.example.propertyappg11.data.PropProperty
import com.example.propertyappg11.data.query.PropApiQuery


interface PropApiService {

    suspend fun getProperties(query: PropApiQuery): List<PropProperty>

    suspend fun getProperty(id: String): PropProperty

    suspend fun addProperty(propProperty: PropProperty) : PropProperty

    suspend fun removeProperty(propertyId: String)


}



