package com.nelu.scrapper.di

import androidx.room.Room
import com.nelu.scrapper.Scrapper
import com.nelu.scrapper.config.App.BASE_URL
import com.nelu.scrapper.config.App.DATABASE_NAME
import com.nelu.scrapper.data.apis.ApiService
import com.nelu.scrapper.db.AppDatabase
import com.nelu.scrapper.db.dao.DaoDownloads
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Initializer {
    /** Remote */
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    /** Remote API */
    val apiService: ApiService get() = retrofit.create(ApiService::class.java)


    /** Local */
    private val appDatabase = Room.databaseBuilder(
        Scrapper.context,
        AppDatabase::class.java,
        DATABASE_NAME
    ).allowMainThreadQueries().build()

    /** Local Data Access Object's */
    val daoDownloads: DaoDownloads get() = appDatabase.userDao()
}