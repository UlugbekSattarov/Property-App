package com.example.propertyappg11.data.network

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.propertyappg11.data.PropProperty
import com.example.propertyappg11.util.MarsDatabaseConverter

@Database(entities = [PropProperty::class], version = 2, exportSchema = false)
@TypeConverters(MarsDatabaseConverter::class)
abstract class PropRemoteDatabase : RoomDatabase() {

    abstract fun marsPropertyDao(): PropRemotePropertyDAO


    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.apply {
                    execSQL(
                        "ALTER TABLE MarsProperties ADD COLUMN surfaceArea REAL NOT NULL DEFAULT 0"
                    )
                    execSQL(
                        "ALTER TABLE MarsProperties ADD COLUMN latitude REAL NOT NULL DEFAULT 0"
                    )
                    execSQL(
                        "ALTER TABLE MarsProperties ADD COLUMN longitude REAL NOT NULL DEFAULT 0"
                    )

                }
            }
        }

    }
}
