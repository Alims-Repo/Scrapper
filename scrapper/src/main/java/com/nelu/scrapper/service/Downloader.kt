package com.nelu.scrapper.service

import com.nelu.scrapper.Scrapper
import com.nelu.scrapper.data.model.ModelDownload
import com.nelu.scrapper.di.Initializer.apiService
import com.nelu.scrapper.di.Initializer.daoDownloads
import com.nelu.scrapper.di.Initializer.getPath
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.LinkedBlockingQueue

class Downloader(
    private val coroutineScope: CoroutineScope
) {

    private var stop = false

    private var pause = false

    private var isExecutingTasks = false

    private val taskQueue = LinkedBlockingQueue<ModelDownload>()

    fun pause() {
        pause = true
    }

    fun resume() {
        pause = false
    }

    fun start(task: ModelDownload) {
        if (taskQueue.contains(task)) return
        taskQueue.offer(task)
        daoDownloads.insertDownloads(task.copy(progress = -1))
        executeTasks()
    }

    fun start(taskList: List<ModelDownload>) {
        taskList.forEach { task ->
            if (taskQueue.contains(task)) return@forEach
            taskQueue.offer(task)
            daoDownloads.insertDownloads(task.copy(progress = -1))
        }
        executeTasks()
    }

    fun deleteCurrent() {
        stop = true
    }

    private fun executeTasks() {
        if (isExecutingTasks) return
        isExecutingTasks = true
        coroutineScope.launch {
            while (true) {
                val task = taskQueue.poll() ?: break
                getDownloadTask(task)
            }
            isExecutingTasks = false
        }
    }

    private suspend fun getDownloadTask(modelDownload: ModelDownload) {
        val response = apiService.downloadFile(modelDownload.url).execute()
        val body = response.body()

        if (!response.isSuccessful || body == null) return

        val totalFileSize = body.contentLength()
        val outputFile = File(
            getPath(modelDownload),
            if (modelDownload.id.isEmpty())
                System.currentTimeMillis().toString()
            else modelDownload.id + if (modelDownload.audio) ".mp3" else ".mp4"
        )

        if (outputFile.exists()) outputFile.deleteRecursively()

        var fileSizeDownloaded: Long = 0

        daoDownloads.insertDownloads(
            modelDownload.copy(progress = 0)
        )

        body.byteStream().use { inputStream ->
            outputFile.outputStream().use { outputStream ->
                val buffer = ByteArray(4096)
                var bytesRead: Int

                var last = 0
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                    fileSizeDownloaded += bytesRead

                    val progress = ((fileSizeDownloaded * 100) / totalFileSize).toInt()
                    if (last != progress) {
                        last = progress
                        daoDownloads.insertDownloads(
                            modelDownload.copy(progress = progress)
                        )
                    }

                    while (pause) {
                        delay(1000)
                    }

                    if (stop) {
                        stop = false
                        daoDownloads.delete(modelDownload.id)
                        return@use
                    }
                }

                daoDownloads.insertDownloads(
                    modelDownload.copy(progress = 100, path = outputFile.path)
                )
            }
        }
    }
}