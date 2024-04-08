package com.nelu.scrapper.data.repo

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.nelu.scrapper.config.App.DOWNLOAD_AUDIO_LINK
import com.nelu.scrapper.config.App.DOWNLOAD_ORIGINAL_VIDEO_LINK
import com.nelu.scrapper.config.App.DOWNLOAD_VIDEO_WITHOUT_WATERMARK
import com.nelu.scrapper.config.App.DOWNLOAD_VIDEO_WITHOUT_WATERMARK_HD
import com.nelu.scrapper.config.App.USER_AGENT
import com.nelu.scrapper.config.JSQuery.GET_PROFILE_DATA
import com.nelu.scrapper.config.JSQuery.REMOVE_LOGIN
import com.nelu.scrapper.data.model.ModelRequest
import com.nelu.scrapper.data.model.ModelTiktok
import com.nelu.scrapper.data.model.ModelTiktok.Companion.toModelTiktok
import com.nelu.scrapper.data.repo.base.BaseTiktok
import com.nelu.scrapper.di.Initializer.apiService
import com.nelu.scrapper.utils.paginateWebView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONArray
import kotlin.coroutines.resume

class RepoTiktok : BaseTiktok {

    override suspend fun getViewInfo(url: String): ModelTiktok? {
        apiService.getTiktokList(ModelRequest(url)).execute().let {
            if (it.isSuccessful)
                return it.body()!!.toModelTiktok()
        }
        return null
    }

    override suspend fun getProfile(activity: Activity, url: String): List<ModelTiktok> {
        val profileID = if (url.contains("http")) extractUsernameFromUrl(url) else url
        return suspendCancellableCoroutine { continuation ->
            activity.runOnUiThread {
                WebView(activity).let { webView ->
                    webView.layoutParams = ViewGroup.LayoutParams(3000, 20000)

                    webView.webChromeClient = object : WebChromeClient() {
                        private var isPageLoaded = false
                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                            super.onProgressChanged(view, newProgress)
                            if (newProgress == 100 && !isPageLoaded) {
                                isPageLoaded = true
                                CoroutineScope(Dispatchers.Main).launch {
                                    delay(3000)
                                    view?.evaluateJavascript(REMOVE_LOGIN, null)

                                    var scroll = 0
                                    while (scroll < 2) {
                                        paginateWebView(view)
                                        delay(2000)
                                        scroll++
                                    }

                                    delay(3000)
                                    view?.evaluateJavascript(GET_PROFILE_DATA) { html ->
                                        val final = html.replace("\\","")
                                        val arrayData = JSONArray(final.substring(1, final.length-1))

                                        val list = mutableListOf<ModelTiktok>()

                                        for (x in 0 until arrayData.length()) {
                                            val obj = arrayData.getJSONObject(x)
                                            val videoID = extractVideoIdFromUrl(obj.getString("href"))
                                            list.add(
                                                ModelTiktok(
                                                    id = videoID ?: System.currentTimeMillis().toString(),
                                                    name = profileID,
                                                    profileImage = "",
                                                    thumbnail = obj.getString("src"),
                                                    title = obj.getString("alt"),
                                                    music = DOWNLOAD_AUDIO_LINK + "$videoID.mp3",
                                                    water = DOWNLOAD_ORIGINAL_VIDEO_LINK + "$videoID.mp4",
                                                    noWaterSD = DOWNLOAD_VIDEO_WITHOUT_WATERMARK + "$videoID.mp4",
                                                    noWaterHD = DOWNLOAD_VIDEO_WITHOUT_WATERMARK_HD + "$videoID.mp4"
                                                )
                                            )
                                        }

                                        continuation.resume(list)
                                    }
                                }
                            }
                        }
                    }

                    webView.settings.run {
                        userAgentString = USER_AGENT
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        javaScriptEnabled = true
                        domStorageEnabled = true
                    }
//
                    activity.addContentView(webView, webView.layoutParams)
                    webView.loadUrl("https://www.tiktok.com/@$profileID")
                    webView.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun extractUsernameFromUrl(url: String): String {
        val regex = Regex("""@([^/\s]+)""")
        val matchResult = regex.find(url)
        return matchResult?.groupValues?.get(1) ?: ""
    }

    private fun extractVideoIdFromUrl(url: String): String? {
        val regex = Regex("/video/(\\d+)")
        val matchResult = regex.find(url)
        return matchResult?.groupValues?.getOrNull(1)
    }
}