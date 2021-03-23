package com.example.marsrealestate

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import com.example.marsrealestate.data.MarsRepository
import com.example.marsrealestate.data.MarsRepositoryImpl
import com.example.marsrealestate.data.database.MarsDatabase
import com.example.marsrealestate.data.network.MarsApiService
import com.example.marsrealestate.data.network.MarsApiServiceData
import com.example.marsrealestate.data.network.MarsApiServiceNoServerImpl
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ServiceLocator {

    /**
     * Should not be set outside of test scope
     */
    @Volatile
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var marsRepository : MarsRepository? = null
        @VisibleForTesting set

    fun getMarsRepository(context : Context) : MarsRepository {
        return marsRepository ?: synchronized(this){
            marsRepository ?: createMarsRepository(context)
        }
    }


    private fun createMarsRepository(context: Context) : MarsRepository {
        val localDataSource = createLocalDataSource(context)
        val remoteDataSource = createRemoteDataSource()
        val repo = MarsRepositoryImpl(remoteDataSource,localDataSource.marsPropertyDao())
        marsRepository = repo
        return repo
    }


    private fun createLocalDataSource(context: Context) : MarsDatabase{
        return Room.databaseBuilder(context,
            MarsDatabase::class.java,
            "mars_database.db")
            .addMigrations(MarsDatabase.MIGRATION_1_2)
            .build()
    }

    private fun createRemoteDataSource() : MarsApiService{
//        val moshi = Moshi.Builder()
//            .add(KotlinJsonAdapterFactory())
//            .build()
//
//        val retrofit = Retrofit.Builder()
//            .addConverterFactory(MoshiConverterFactory.create(moshi))
//            .baseUrl(MarsApiServiceData.BASE_URL)
//            .build()
//
//        return retrofit.create(MarsApiService::class.java)

        return MarsApiServiceNoServerImpl()
    }

}