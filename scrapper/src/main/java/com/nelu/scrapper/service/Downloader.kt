package com.nelu.scrapper.service

import com.nelu.scrapper.Scrapper
import com.nelu.scrapper.data.model.ModelDownload
import com.nelu.scrapper.di.Initializer.apiService
import com.nelu.scrapper.di.Initializer.daoDownloads
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.LinkedBlockingQueue

class Downloader(
    private val coroutineScope: CoroutineScope
) {

    private var isExecutingTasks = false

    private val taskQueue = LinkedBlockingQueue<ModelDownload>()


    fun start(task: ModelDownload) {
        taskQueue.offer(task)
        daoDownloads.insertDownloads(task.copy(progress = -1))
        executeTasks()
    }

    fun start(taskList: List<ModelDownload>) {
        taskList.forEach { task ->
            taskQueue.offer(task)
            daoDownloads.insertDownloads(task.copy(progress = -1))
        }
        executeTasks()
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
        val outputFile = File(Scrapper.context.filesDir, modelDownload.id)

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
                }

                daoDownloads.insertDownloads(
                    modelDownload.copy(progress = 100)
                )
            }
        }
    }
}