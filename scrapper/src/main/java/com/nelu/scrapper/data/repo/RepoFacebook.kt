package com.nelu.scrapper.data.repo

import android.app.Activity
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.nelu.scrapper.data.model.ModelFacebook
import com.nelu.scrapper.data.model.ModelFacebook.Companion.toModelFacebook
import com.nelu.scrapper.data.model.ModelRequest
import com.nelu.scrapper.data.repo.base.BaseFacebook
import com.nelu.scrapper.di.Initializer.apiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.json.JSONArray
import kotlin.coroutines.resume
import kotlin.system.measureTimeMillis

class RepoFacebook : BaseFacebook {

    override suspend fun getVideo(url: String): ModelFacebook? {
        return withContext(Dispatchers.IO) {
            repeat(3) { attempt ->
                delay(1000L * attempt)
                try {
                    val response = apiService.getFacebookVideo(ModelRequest(url)).execute()
                    if (response.isSuccessful) {
                        return@withContext response.body()?.toModelFacebook()
                    }
                } catch (e: Exception) {
                    // Handle exception if needed
                }
            }
            return@withContext null // Return null if all attempts fail
        }
//        return suspendCancellableCoroutine { continuation ->
//
//            val coroutineScope = CoroutineScope(Dispatchers.IO)
//            coroutineScope.launch {
//                listOf(
//                    async {
//                        try {
//                            apiService.getFacebookVideo(
//                                ModelRequest(url)
//                            ).execute()?.let {
//                                if (it.isSuccessful) {
//                                    continuation.resume(it.body()?.toModelFacebook())
//                                    coroutineScope.coroutineContext.cancelChildren()
//                                }
//                            }
//                        } catch (e: Exception) {}
//                    },
//                    async {
//                        delay(1000)
//                        try {
//                            apiService.getFacebookVideo(
//                                ModelRequest(url)
//                            ).execute()?.let {
//                                if (it.isSuccessful) {
//                                    continuation.resume(it.body()?.toModelFacebook())
//                                    coroutineScope.coroutineContext.cancelChildren()
//                                }
//                            }
//                        } catch (e: Exception) {}
//                    },
//                    async {
//                        delay(2000)
//                        try {
//                            apiService.getFacebookVideo(
//                                ModelRequest(url)
//                            ).execute()?.let {
//                                if (it.isSuccessful) {
//                                    continuation.resume(it.body()?.toModelFacebook())
//                                    coroutineScope.coroutineContext.cancelChildren()
//                                }
//                            }
//                        } catch (e: Exception) {}
//                    }
//                ).awaitAll()
//            }
//        }
    }

    override suspend fun getVideo(url: String, activity: Activity): String? {
        //https://snapsave.app/
        return suspendCancellableCoroutine { continuation ->
            activity.runOnUiThread {
                WebView(activity).let { w ->
                    w.layoutParams = ViewGroup.LayoutParams(1, 1)
                    val s = w.settings
                    s.javaScriptEnabled = true
                    s.domStorageEnabled = true
                    w.webChromeClient = object : WebChromeClient() {
                        private var isPageLoaded = false
                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                            super.onProgressChanged(view, newProgress)
                            if (newProgress > 50 && !isPageLoaded) {
                                isPageLoaded = true
                                w.evaluateJavascript(
                                    "(function() { " +
                                            "document.getElementById('url').value ='" + url + "';" +
                                            "document.getElementById('send').click();" +
                                            "})();"
                                ) {
                                    var done = false
                                    CoroutineScope(Dispatchers.IO).launch {
                                        for (x in 0..10) {
                                            if (done) return@launch
                                            activity.runOnUiThread {
                                                w.evaluateJavascript(
                                                    "(function() { " +
                                                            "   var downloadLink = document.querySelector('a.button.is-success.is-small'); " +
                                                            "   return downloadLink ? downloadLink.getAttribute('href') : ''; " +
                                                            "})();"
                                                ) {
                                                    if (it.replace("\"", "").isNotEmpty()) {
                                                        done = true
                                                        continuation.resume(it.replace("\"", "").toString())
                                                    }
                                                    Log.e("Loop - $x", it.toString())
                                                }
                                            }
                                            delay(2000)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    w.loadUrl("https://snapsave.app/")
                }
            }
        }
    }
}