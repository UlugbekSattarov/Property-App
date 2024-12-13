package com.example.propertyappg11

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import com.example.propertyappg11.data.PropRepository
import com.example.propertyappg11.data.PropRepositoryImpl
import com.example.propertyappg11.data.database.PropDatabase
import com.example.propertyappg11.data.network.PropApiService
import com.example.propertyappg11.data.network.PropApiServiceNoServerImpl
import com.example.propertyappg11.data.network.PropRemoteDatabase
import com.example.propertyappg11.util.helpers.ResourceUrlHelper

object ServiceLocator {
    @Volatile
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var propRepository : PropRepository? = null
        @VisibleForTesting set

    fun getMarsRepository(context : Context) : PropRepository {
        return propRepository ?: synchronized(this){
            propRepository ?: createMarsRepository(context)
        }
    }


    private fun createMarsRepository(context: Context) : PropRepository {
        val localDataSource = createLocalDataSource(context)
        val remoteDataSource = createRemoteDataSource(context)
        val repo = PropRepositoryImpl(remoteDataSource,localDataSource.marsPropertyDao())
        propRepository = repo
        return repo
    }


    private fun createLocalDataSource(context: Context) : PropDatabase{
        return Room.databaseBuilder(context,
            PropDatabase::class.java,
            "mars_database.db")
            .addMigrations(PropDatabase.MIGRATION_1_2)
            .fallbackToDestructiveMigration()
            .build()
    }

    private fun createRemoteDataSource(context: Context) : PropApiService{
        val db = Room.databaseBuilder(context,
            PropRemoteDatabase::class.java,
            "mars_database_remote.db")
            .addMigrations(PropRemoteDatabase.MIGRATION_1_2)
            .build()


        return PropApiServiceNoServerImpl(db.marsPropertyDao(), ResourceUrlHelper.getAllLandscapesUrl(context))
    }

}
