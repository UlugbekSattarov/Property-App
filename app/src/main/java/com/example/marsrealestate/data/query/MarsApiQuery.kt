package com.example.marsrealestate.data.query

class MarsApiQuery(val pageNumber: Int = 1,
                   val itemsPerPage: Int = 10,
                   val filter: MarsApiFilter? = null)