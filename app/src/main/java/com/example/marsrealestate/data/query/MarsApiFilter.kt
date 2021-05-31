package com.example.marsrealestate.data.query

import com.example.marsrealestate.data.MarsProperty

class MarsApiFilter(val type: MarsPropertyType = MarsPropertyType.ALL,
                         val queryString: String = "") {

    enum class MarsPropertyType(val value: String) {
        ALL("all"),
        RENT(MarsProperty.TYPE_RENT),
        BUY(MarsProperty.TYPE_BUY)
    }

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

/**
 * For a more convenient use than filter.matches(property)
 */
fun MarsProperty.matches(filter : MarsApiFilter) : Boolean =
    filter.matches(this)