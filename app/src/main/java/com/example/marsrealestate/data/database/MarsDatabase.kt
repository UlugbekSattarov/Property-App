package com.example.marsrealestate.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.marsrealestate.data.Converters
import com.example.marsrealestate.data.Favorite
import com.example.marsrealestate.data.MarsProperty

// Annotates class to be a Room Database with a table (entity) of the MarsProperty class
@Database(entities = [MarsProperty::class,Favorite::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class MarsDatabase : RoomDatabase() {

    abstract fun marsPropertyDao(): MarsPropertyDAO



}