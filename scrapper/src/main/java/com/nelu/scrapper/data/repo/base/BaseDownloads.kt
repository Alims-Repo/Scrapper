package com.nelu.scrapper.data.repo.base

import androidx.lifecycle.LiveData
import androidx.room.Query
import com.nelu.scrapper.data.model.ModelDownload

interface BaseDownloads {


    /**
     * Downloads a single [ModelDownload] and returns a [LiveData] object that emits the download progress.
     *
     * @param model The [ModelDownload] to download.
     * @return A [LiveData] object that emits the download progress.
     */
    fun download(model : ModelDownload) : LiveData<ModelDownload>


    /**
     * Downloads a list of [ModelDownload] objects.
     *
     * @param model The list of [ModelDownload] objects to download.
     * @return `true` if the downloads are initiated successfully, `false` otherwise.
     */
    fun download(model: ArrayList<ModelDownload>) : Boolean


    /**
     * Retrieves a [LiveData] object representing the list of all downloads in the queue.
     *
     * @return A [LiveData] object containing the list of downloads in the queue.
     */
    fun getAllQueueLive(): LiveData<List<ModelDownload>>


    /**
     * Retrieves a [LiveData] object representing the current progress of the download.
     *
     * @return A [LiveData] object containing the current progress of the download.
     */
    fun getCurrentProgress(): LiveData<ModelDownload>


    /**
     * Retrieves a [LiveData] object representing the list of all completed downloads.
     *
     * @return A [LiveData] object containing the list of completed downloads.
     */
    fun getAllCompletedLive(): LiveData<List<ModelDownload>>


    /**
     * Retrieves a list of all downloads.
     * @return A list containing all downloads.
     */
    fun getAllDownloads(): List<ModelDownload>

    fun delete(id: String)
}