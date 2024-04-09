package com.nelu.scrapper.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nelu.scrapper.data.model.ModelDownload

@Dao
interface DaoDownloads {

    /** Basic Query */
    @Query("SELECT * FROM ModelDownload")
    fun getAllDownloads(): List<ModelDownload>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDownloads(data: ModelDownload)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDownloads(data: List<ModelDownload>)

    /** Advanced Query */

    @Query("SELECT * FROM ModelDownload WHERE progress = -1 ORDER BY addedDate DESC")
    fun getAllQueue(): List<ModelDownload>

    @Query("SELECT * FROM ModelDownload WHERE progress = 100 ORDER BY addedDate DESC")
    fun getAllCompleted(): List<ModelDownload>

    /**
     * Live Data.
     * Be careful when accessing
     */
    @Query("SELECT * FROM ModelDownload WHERE progress BETWEEN 0 AND 99 ORDER BY addedDate DESC LIMIT 1")
    fun getCurrentProgress(): LiveData<ModelDownload>

    @Query("SELECT * FROM ModelDownload WHERE progress = -1 ORDER BY addedDate DESC")
    fun getAllQueueLive(): LiveData<List<ModelDownload>>

    @Query("SELECT * FROM ModelDownload WHERE progress = 100 ORDER BY addedDate DESC")
    fun getAllCompletedLive(): LiveData<List<ModelDownload>>
}