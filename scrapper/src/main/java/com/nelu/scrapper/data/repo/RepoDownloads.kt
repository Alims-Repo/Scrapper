package com.nelu.scrapper.data.repo

import androidx.lifecycle.LiveData
import com.nelu.scrapper.Scrapper
import com.nelu.scrapper.data.model.ModelDownload
import com.nelu.scrapper.data.repo.base.BaseDownloads
import com.nelu.scrapper.di.Initializer.daoDownloads
import com.nelu.scrapper.service.Downloader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.io.File

class RepoDownloads(
    private val coroutineScope: CoroutineScope
) : BaseDownloads {

    private val downloader = Downloader(coroutineScope)

    override fun download(model: ModelDownload): LiveData<ModelDownload> {
        downloader.start(model)
        return daoDownloads.getCurrentProgress()
    }

    override fun download(model: ArrayList<ModelDownload>): Boolean {
        downloader.start(model)
        return true
    }

    override fun deleteCurrent() {
        downloader.deleteCurrent()
    }

    override fun pause() {
        downloader.pause()
    }

    override fun resume() {
        downloader.resume()
    }

    override fun getAllQueueLive(): LiveData<List<ModelDownload>> {
        return daoDownloads.getAllQueueLive()
    }

    override fun getCurrentProgress(): LiveData<ModelDownload> {
        return daoDownloads.getCurrentProgress()
    }

    override fun getAllCompletedLive(): LiveData<List<ModelDownload>> {
        return daoDownloads.getAllCompletedLive()
    }

    override fun getAllDownloads(): List<ModelDownload> {
        return daoDownloads.getAllDownloads()
    }

    override fun delete(id: String) {
        daoDownloads.delete(id)
        if (File(Scrapper.context.filesDir, id).exists())
            File(Scrapper.context.filesDir, id).delete()
    }
}