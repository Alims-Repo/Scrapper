package com.nelu.scrapper.data.repo.base

import androidx.lifecycle.LiveData
import androidx.room.Query
import com.nelu.scrapper.data.model.ModelDownload

interface BaseDownloads {

    /** For Initiating Downloads */
    fun download(model : ModelDownload) : LiveData<ModelDownload>

    fun download(model: ArrayList<ModelDownload>) : Boolean

    /** Live data changes update */
    fun getAllQueueLive(): LiveData<List<ModelDownload>>

    fun getCurrentProgress(): LiveData<ModelDownload>

    fun getAllCompletedLive(): LiveData<List<ModelDownload>>

    /** Data history */
    fun getAllDownloads(): List<ModelDownload>
}