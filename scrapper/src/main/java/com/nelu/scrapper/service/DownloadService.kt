package com.nelu.scrapper.service

import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.nelu.scrapper.di.Initializer.apiService
import com.nelu.scrapper.di.Initializer.daoDownloads
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DownloadService : Service() {

    companion object {
        fun launch(context: Context) {
            if (isServiceRunning(context, DownloadService::class.java).not()) {
                context.startService(
                    Intent(
                        context,
                        DownloadService::class.java
                    )
                )
            }
        }

        private fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val services = manager.getRunningServices(Integer.MAX_VALUE)
            for (service in services) {
                if (serviceClass.name == service.service.className) {
                    return true
                }
            }
            return false
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
//        CoroutineScope(Dispatchers.IO).launch {
//            daoDownloads.getAllQueue().forEach {
//                apiService.downloadFile(it.url)?.let {
//
//                }
//            }
//        }
    }
}