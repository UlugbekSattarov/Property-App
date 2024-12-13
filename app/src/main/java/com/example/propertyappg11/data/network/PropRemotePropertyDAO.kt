package com.example.propertyappg11.data.network

import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.propertyappg11.data.PropProperty
import com.example.propertyappg11.data.query.PropApiFilter
import com.example.propertyappg11.data.query.PropApiQuery
import com.example.propertyappg11.data.query.PropApiSorting

@Dao
interface PropRemotePropertyDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg propProperty : PropProperty)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(marsProperties : List<PropProperty>)

    @Query("SELECT * from MarsProperties WHERE id = :id")
    suspend fun getProperty(id: String) : PropProperty

    @Query("SELECT COUNT(*) from MarsProperties ")
    suspend fun getPropertiesCount() : Int

    @Query("SELECT id from MarsProperties ORDER BY id DESC LIMIT 1")
    suspend fun getLastPropertyId() : String

    /**
     * Add a new property and auto increment the id
     */
    @Transaction
    suspend fun addNewProperty(propProperty: PropProperty) : PropProperty {
        val newId = getLastPropertyId().toInt() + 1
        propProperty.copy(id = newId.toString()).also {
            insert(it)
            return it
        }
    }


    @RawQuery
    suspend fun getProperties(query : SupportSQLiteQuery) : List<PropProperty>

    suspend fun getProperties(query: PropApiQuery) : List<PropProperty> {

        //PageNumber starts at 1 and not 0, so we have to subtract 1
        val offset = (query.pageNumber - 1) * query.itemsPerPage

        val idFilterStr = if (query.filter?.queryString.isNullOrBlank())
            "%"
            else "%${query.filter!!.queryString}%"


        val typeFilterStr = when (query.filter?.type) {
            null -> "%"
            PropApiFilter.PropPropertyType.ALL -> "%"
            else -> query.filter.type
        }

        val sortingStr = when (query.sortedBy) {
            PropApiSorting.Default -> "id ASC"
            PropApiSorting.PriceAscending -> "price ASC"
            PropApiSorting.PriceDescending  -> "price DESC"
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
