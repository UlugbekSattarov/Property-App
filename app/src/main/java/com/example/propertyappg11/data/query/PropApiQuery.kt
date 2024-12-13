package com.example.propertyappg11.data.query

class PropApiQuery(val pageNumber: Int = 1,
                   val itemsPerPage: Int = 10,
                   val filter: PropApiFilter? = null,
                   val sortedBy : PropApiSorting = PropApiSorting.Default)
