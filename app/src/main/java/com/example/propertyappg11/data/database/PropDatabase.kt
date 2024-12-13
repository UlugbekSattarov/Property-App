package com.example.propertyappg11.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.propertyappg11.data.Favorite
import com.example.propertyappg11.data.MarsProperty
import com.example.propertyappg11.util.MarsDatabaseConverter

// Annotates class to be a Room Database with a table (entity) of the MarsProperty class
/** Version 2 added [MarsProperty.surfaceArea], [MarsProperty.latitude] and [MarsProperty.longitude]
 * as attributes for [MarsProperty]
 */
@Database(entities = [MarsProperty::class,Favorite::class], version = 3, exportSchema = false)
@TypeConverters(MarsDatabaseConverter::class)
abstract class  MarsDatabase : RoomDatabase() {

    abstract fun marsPropertyDao(): MarsPropertyDAO


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
