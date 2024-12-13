package com.example.propertyappg11.data.query

import com.example.propertyappg11.data.PropProperty

class PropApiFilter(val type: PropPropertyType = PropPropertyType.ALL,
                    val queryString: String = "") {

    enum class PropPropertyType(val value: String) {
        ALL("all"),
        RENT(PropProperty.TYPE_RENT),
        BUY(PropProperty.TYPE_BUY)
    }

    fun matches(property: PropProperty) : Boolean{
        val isCorrectType =
            if (type == PropPropertyType.ALL) true
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
fun PropProperty.matches(filter : PropApiFilter) : Boolean =
    filter.matches(this)
