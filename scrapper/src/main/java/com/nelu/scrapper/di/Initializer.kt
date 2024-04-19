package com.nelu.scrapper.di

import androidx.room.Room
import com.nelu.scrapper.Scrapper
import com.nelu.scrapper.config.App.BASE_URL
import com.nelu.scrapper.config.App.DATABASE_NAME
import com.nelu.scrapper.data.apis.ApiService
import com.nelu.scrapper.data.model.ModelDownload
import com.nelu.scrapper.db.AppDatabase
import com.nelu.scrapper.db.dao.DaoDownloads
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

object Initializer {
    /** Remote */
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(
            OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build())
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

    /** Internals */
    private val appPath = File(
        Scrapper.basePath,
        Scrapper.context.applicationInfo.loadLabel(
            Scrapper.context.packageManager
        ).toString()
    )

    private fun getVideoPath()  = File(appPath, "Video").also { it.mkdirs() }.path

    private fun getAudioPath() = File(appPath, "Audio").also { it.mkdirs() }.path

    private fun getImagePath() = File(appPath, "Image").also { it.mkdirs() }.path

    fun getPath(model: ModelDownload) : String {
        return if (model.audio) getAudioPath() else getVideoPath()
    }
}