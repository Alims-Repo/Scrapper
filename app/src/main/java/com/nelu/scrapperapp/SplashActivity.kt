package com.nelu.scrapperapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nelu.scrapper.Scrapper
import com.nelu.scrapper.data.model.ModelDownload
import com.nelu.scrapper.data.model.ModelTiktok.Companion.toModelDownload
import com.nelu.scrapper.di.Initializer.daoDownloads
import com.nelu.scrapper.views.TiktokLive
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis

class SplashActivity : AppCompatActivity(), TiktokLive.OnClick {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        daoDownloads.getAllCompleted().let {
            Log.e("Completed", it?.toString() ?: "Empty")
        }

        daoDownloads.getCurrentProgress().observe(this) {
            Log.e("Update", it?.toString() ?: "Empty")
        }


        CoroutineScope(Dispatchers.IO).launch {
//            Scrapper.tiktok.getVideo(
//                this@SplashActivity,
//                "https://www.tiktok.com/@rifatrohman06/video/7354755867369114881"
//            )?.let { model->
//                Scrapper.downloads.download(
//                    model.toModelDownload(model.noWaterHD ?: model.noWaterSD)
//                ).let {
//                    runOnUiThread {
//                        it.observe(this@SplashActivity) {
//                            Log.e("Progress", it?.toString() ?: "Empty")
//                        }
//                    }
//                }
//            }

//            Scrapper.tiktok.getProfile(
//                this@SplashActivity,
//                "https://www.tiktok.com/@sumaiya_mimu"
//            ).let {
//                Log.e("DATA", it.size.toString())
//                Log.e("DATA", it.toString())
//            }
        }

        findViewById<TiktokLive>(R.id.tiktok).run {
            setListener(this@SplashActivity)
            start()
        }
    }

    override fun onShare(videoURL: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, videoURL)
        startActivity(Intent.createChooser(shareIntent, "Share URL"))
    }

    override fun onDownload(videoURL: String) {
        CoroutineScope(Dispatchers.IO).launch {
            Scrapper.tiktok.getVideo(
                this@SplashActivity, videoURL
            )?.let {
                Scrapper.downloads.download(it.toModelDownload(it.noWaterHD ?: it.noWaterSD))
            }
        }

        Scrapper.downloads.getAllQueueLive().observe(this) {
            Log.e("STATUS All Queue", it?.size?.toString() ?: "")
        }

        Scrapper.downloads.getCurrentProgress().observe(this) {
            Log.e("STATUS Current Progress", it?.progress?.toString() ?: "")
        }
    }

    override fun onProfileClick(id: String) {
//        CoroutineScope(Dispatchers.IO).launch {
//            measureTimeMillis {
//                Scrapper.tiktok.getProfile(this@SplashActivity, id).let {
//                    Log.e("DATA", it.toString())
//                }
//            }.let {
//                runOnUiThread {
//                    Toast.makeText(
//                        this@SplashActivity,
//                        it.toString(),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }
    }
}