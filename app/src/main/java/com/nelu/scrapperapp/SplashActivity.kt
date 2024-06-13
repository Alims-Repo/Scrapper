package com.nelu.scrapperapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nelu.scrapper.Scrapper
import com.nelu.scrapper.data.model.ModelDownload
import com.nelu.scrapper.data.model.ModelFacebook.Companion.toModelDownload
import com.nelu.scrapper.data.model.ModelTiktok
import com.nelu.scrapper.data.model.ModelTiktok.Companion.toModelDownload
import com.nelu.scrapper.data.repo.RepoFacebook
import com.nelu.scrapper.di.Initializer.daoDownloads
import com.nelu.scrapper.views.TiktokLive
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis

class SplashActivity : AppCompatActivity(), TiktokLive.OnClick {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

//        daoDownloads.getAllCompleted().let {
//            Log.e("Completed", it?.toString() ?: "Empty")
//        }

        daoDownloads.getAllCompletedLive().observe(this) {
            Log.e("Update", it?.toString() ?: "Empty")
        }


        CoroutineScope(Dispatchers.IO).launch {
            Scrapper.tiktok.getVideo("https://vt.tiktok.com/ZSYhpseKm/").let {
                Log.e("DATA", it?.toString() ?: "EMPTY")
                Scrapper.downloads.download(
                    it?.toModelDownload(
                        it.music, true
                    )!!
                )
            }
//            Scrapper.tiktok.getProfilePaginate(
//                this@SplashActivity,
//                "https://www.tiktok.com/@sumaiya_mimu"
//            ).collect {
//                Log.e("Collect", it.size.toString())
//            }
//            Scrapper.getUrlType("https://vt.tiktok.com/ZSFsHXB3F/").let {
//                Log.e("TYPE", it.toString())
//            }
//
//            listOf(
//                async {
//                    Log.e("TIME", measureTimeMillis {
//                        Scrapper.facebook.getVideo("https://www.facebook.com/share/v/uAjY17QzG1keiwpw/?mibextid=jmPrMh")?.let {
//                            Log.e("DATA", it.toString())
//                        }
//                    }.toString())
//                }, async {
//                    Scrapper.instagram.getVideo("https://www.instagram.com/reel/C4lF8_0o-0p/?igsh=ODAxZ2V4aXQwaWx5")?.let {
//                        Log.e("instagram", it.toString())
//                    }
//                }, async {
//                    Scrapper.twitter.getVideo("https://twitter.com/TeamAbhiSha/status/1743351410761019794?t=vms8wxcU0mQuhVxwGCHjFw&s=19")?.let {
//                        Log.e("twitter", it.toString())
//                    }
//                },
//                async {
//                    Scrapper.tiktok.getVideo("https://vt.tiktok.com/ZSFsHXB3F/")?.let {
//                        Log.e("tiktok", it.toString())
//                    }
//                }
//            ).awaitAll()

            //Scrapper.downloads.download(
            //                    model.toModelDownload(model.noWaterHD ?: model.noWaterSD)
            //                ).let {
            //                    runOnUiThread {
            //                        it.observe(this@SplashActivity) {
            //                            Log.e("Progress", it?.toString() ?: "Empty")
            //                        }
            //                    }
            //                }

//            Scrapper.tiktok.getProfile(
//                this@SplashActivity,
//                "https://www.tiktok.com/@sumaiya_mimu"
//            ).let {
//                Log.e("DATA", it.size.toString())
//                Log.e("DATA", it.map { it.thumbnail }.toString())
//            }

//            Scrapper.tiktok.getProfilePic(this@SplashActivity, "sumaiya_mimu").let {
//                Log.e("Pro", it?.toString() ?: "EMPTY")
//            }

//            Scrapper.tiktok.getVideo("https://vt.tiktok.com/ZSFGv5QhH").let {
//                Log.e("ID", it?.id.toString())
//            }
//            Scrapper.tiktok.getVideo("https://vt.tiktok.com/ZSFGv5QhH").let {
//                Log.e("ID", it?.id.toString())
//            }

        }

//        findViewById<TiktokLive>(R.id.tiktok).run {
//            setListener(this@SplashActivity)
//            start()
//        }
    }

    override fun onShare(videoURL: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, videoURL)
        startActivity(Intent.createChooser(shareIntent, "Share URL"))
    }

    override fun onDownload(videoURL: String) {
        CoroutineScope(Dispatchers.IO).launch {
            Scrapper.tiktok.getVideo(videoURL)?.let {
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