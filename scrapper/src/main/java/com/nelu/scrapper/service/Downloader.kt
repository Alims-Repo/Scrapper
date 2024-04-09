package com.nelu.scrapper.service

import com.nelu.scrapper.Scrapper
import com.nelu.scrapper.data.model.ModelDownload
import com.nelu.scrapper.di.Initializer.apiService
import com.nelu.scrapper.di.Initializer.daoDownloads
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import java.io.File

class Downloader(
    private val coroutineScope: CoroutineScope,
    private val modelDownload: ModelDownload
) {

    suspend fun start() {
        coroutineScope.async {
            val response = apiService.downloadFile(modelDownload.url).execute()
            val body = response.body()

            if (!response.isSuccessful || body == null) return@async

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
        }.await()
    }
}