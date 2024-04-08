package com.nelu.scrapper.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nelu.scrapper.data.model.ModelDownload

@Dao
interface DaoDownloads {

    @Query("SELECT * FROM ModelDownload")
    fun getAllDownloads(): List<ModelDownload>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDownloads(data: ModelDownload)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDownloads(data: List<ModelDownload>)
}