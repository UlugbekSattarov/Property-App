package com.example.marsrealestate.data.network

import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.marsrealestate.data.MarsProperty
import com.example.marsrealestate.data.query.MarsApiFilter
import com.example.marsrealestate.data.query.MarsApiQuery
import com.example.marsrealestate.data.query.MarsApiSorting

@Dao
interface MarsRemotePropertyDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg marsProperty : MarsProperty)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(marsProperties : List<MarsProperty>)

    @Query("SELECT * from MarsProperties WHERE id = :id")
    suspend fun getProperty(id: String) : MarsProperty?

    @Query("SELECT COUNT(*) from MarsProperties ")
    suspend fun getPropertiesCount() : Int


    @RawQuery
    suspend fun getProperties(query : SupportSQLiteQuery) : List<MarsProperty>

    suspend fun getProperties(query: MarsApiQuery, sorting : MarsApiSorting) : List<MarsProperty> {

        val idFilterStr = if (query.filter?.queryString.isNullOrBlank())
            "%"
            else "%${query.filter!!.queryString}%"


        val typeFilterStr = when (query.filter?.type) {
            null -> "%"
            MarsApiFilter.MarsPropertyType.ALL -> "%"
            else -> query.filter.type
        }

        val sortingStr = when (sorting) {
            MarsApiSorting.Default -> "id ASC"
            MarsApiSorting.PriceAscending -> "price ASC"
            MarsApiSorting.PriceDescending  -> "price DESC"
        }

        val dbQuery = "SELECT * FROM MarsProperties " +
                "WHERE id LIKE '$idFilterStr' " +
                "AND type LIKE '$typeFilterStr' " +
                "ORDER BY $sortingStr "

        return getProperties(SimpleSQLiteQuery(dbQuery))
    }


    @Query("DELETE from MarsProperties WHERE id = :id")
    suspend fun removeProperty(id : String)



}