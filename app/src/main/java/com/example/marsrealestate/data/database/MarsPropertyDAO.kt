package com.example.marsrealestate.data.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.room.*
import com.example.marsrealestate.data.Favorite
import com.example.marsrealestate.data.FavoriteProperty
import com.example.marsrealestate.data.MarsProperty
import java.util.*

@Dao
interface MarsPropertyDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(marsProperty : MarsProperty)

    @Query("SELECT * from MarsProperties WHERE id = :id")
    suspend fun getProperty(id: String) : MarsProperty?

    @Query("SELECT * from MarsProperties WHERE id = :id")
    fun observeProperty(id: String) : LiveData<MarsProperty?>

    @Query("SELECT * from MarsProperties")
    fun observeProperties() : LiveData<List<MarsProperty>>

    @Query("DELETE FROM MarsProperties WHERE id = :id")
    suspend fun removeProperty(id : String)





    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite : Favorite)

    @Query("SELECT * FROM Favorite WHERE propertyId = :propertyId")
    suspend fun getFavoriteFromPropertyId(propertyId: String) : Favorite?


    @Query("DELETE FROM Favorite WHERE propertyId = :propertyId")
    suspend fun removeFavoriteFromProperty(propertyId : String)


    @Transaction
    suspend fun addToFavorite(property: MarsProperty, dateFavorited : Date? = null ) {
        insert(property)
        val dateFav = dateFavorited ?: Date()
        insert(Favorite(propertyId = property.id,dateFavorited = dateFav))
    }

    @Transaction
    suspend fun removeFromFavorite(propertyId :String) {
        removeProperty(propertyId)
        removeFavoriteFromProperty(propertyId)
    }

    @Transaction
    @Query("SELECT * FROM MarsProperties WHERE id IN (SELECT propertyId FROM Favorite)")
    suspend fun getFavoriteProperties() : List<FavoriteProperty>

    @Transaction
    @Query("SELECT * FROM MarsProperties WHERE id IN (SELECT propertyId FROM Favorite)")
    fun observeFavorites() : LiveData<List<FavoriteProperty>>


    fun observeIsFavorite(propertyId: String) =
        observeFavorites()
            .map { props -> props.find { p : FavoriteProperty -> p.property.id == propertyId} != null }

}