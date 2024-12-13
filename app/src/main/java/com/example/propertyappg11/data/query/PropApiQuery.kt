package com.example.propertyappg11.data.query

class MarsApiQuery(val pageNumber: Int = 1,
                   val itemsPerPage: Int = 10,
                   val filter: MarsApiFilter? = null,
                   val sortedBy : MarsApiSorting = MarsApiSorting.Default)
