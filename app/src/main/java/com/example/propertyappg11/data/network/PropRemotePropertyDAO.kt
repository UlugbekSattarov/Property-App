package com.example.propertyappg11.data.network

import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.propertyappg11.data.MarsProperty
import com.example.propertyappg11.data.query.MarsApiFilter
import com.example.propertyappg11.data.query.MarsApiQuery
import com.example.propertyappg11.data.query.MarsApiSorting

@Dao
interface MarsRemotePropertyDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg marsProperty : MarsProperty)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(marsProperties : List<MarsProperty>)

    @Query("SELECT * from MarsProperties WHERE id = :id")
    suspend fun getProperty(id: String) : MarsProperty

    @Query("SELECT COUNT(*) from MarsProperties ")
    suspend fun getPropertiesCount() : Int

    @Query("SELECT id from MarsProperties ORDER BY id DESC LIMIT 1")
    suspend fun getLastPropertyId() : String

    /**
     * Add a new property and auto increment the id
     */
    @Transaction
    suspend fun addNewProperty(marsProperty: MarsProperty) : MarsProperty {
        val newId = getLastPropertyId().toInt() + 1
        marsProperty.copy(id = newId.toString()).also {
            insert(it)
            return it
        }
    }


    @RawQuery
    suspend fun getProperties(query : SupportSQLiteQuery) : List<MarsProperty>

    suspend fun getProperties(query: MarsApiQuery) : List<MarsProperty> {

        //PageNumber starts at 1 and not 0, so we have to subtract 1
        val offset = (query.pageNumber - 1) * query.itemsPerPage

        val idFilterStr = if (query.filter?.queryString.isNullOrBlank())
            "%"
            else "%${query.filter!!.queryString}%"


        val typeFilterStr = when (query.filter?.type) {
            null -> "%"
            MarsApiFilter.MarsPropertyType.ALL -> "%"
            else -> query.filter.type
        }

        val sortingStr = when (query.sortedBy) {
            MarsApiSorting.Default -> "id ASC"
            MarsApiSorting.PriceAscending -> "price ASC"
            MarsApiSorting.PriceDescending  -> "price DESC"
        }

        val dbQuery = "SELECT * FROM MarsProperties " +
                "WHERE id LIKE '$idFilterStr' " +
                "AND type LIKE '$typeFilterStr' " +
                "ORDER BY $sortingStr " +
                "LIMIT ${query.itemsPerPage} OFFSET $offset"

        return getProperties(SimpleSQLiteQuery(dbQuery))
    }


    @Query("DELETE from MarsProperties WHERE id = :id")
    suspend fun removeProperty(id : String)



}
