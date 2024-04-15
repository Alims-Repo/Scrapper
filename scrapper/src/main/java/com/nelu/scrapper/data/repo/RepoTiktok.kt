package com.nelu.scrapper.data.repo

import android.app.Activity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.nelu.scrapper.Scrapper
import com.nelu.scrapper.config.JSQuery.GET_PROFILE_DATA
import com.nelu.scrapper.config.JSQuery.REMOVE_LOGIN
import com.nelu.scrapper.data.model.ModelRequest
import com.nelu.scrapper.data.model.ModelTiktok
import com.nelu.scrapper.data.model.ModelTiktok.Companion.toModelTiktok
import com.nelu.scrapper.data.model.ModelTiktok.Companion.toTiktokArray
import com.nelu.scrapper.data.repo.base.BaseTiktok
import com.nelu.scrapper.di.Initializer.apiService
import com.nelu.scrapper.utils.extractUsernameFromTiktokUrl
import com.nelu.scrapper.utils.getWebView
import com.nelu.scrapper.utils.paginateWebView
import com.nelu.scrapper.utils.toProfileUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import kotlin.coroutines.resume

class RepoTiktok : BaseTiktok {

    override suspend fun getVideo(url: String): ModelTiktok? {
        var vid = ""
        var thumb = ""
        var model: ModelTiktok? = null

        withContext(Dispatchers.IO) {
            listOf(
                async {
                    apiService.getTiktokVideo(ModelRequest(url)).execute().let {
                        if (it.isSuccessful) model = it.body()!!.toModelTiktok(url)
                    }
                }, async {
                    apiService.getTiktokThumbnail(
                        "https://www.tiktok.com/oembed?url=$url"
                    ).execute() .let {
                        if (it.isSuccessful) {
                            vid = it.body()?.get("embed_product_id")?.asString.toString()
                            thumb = it.body()?.get("thumbnail_url")?.asString.toString()
                        }
                    }
                }
            ).awaitAll()
        }
        return model?.apply {
            id = vid
            thumbnail = thumb
        }
    }

    override suspend fun getProfilePic(activity: Activity, profile: String): String? {
        return suspendCancellableCoroutine { continuation->
            activity.runOnUiThread {
                WebView(activity).let { w->
                    w.layoutParams = ViewGroup.LayoutParams(1, 1)
                    val s = w.settings

                    s.javaScriptEnabled = true

                    s.domStorageEnabled = true

                    w.webChromeClient = object : WebChromeClient() {
                        private var isPageLoaded = false
                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                            super.onProgressChanged(view, newProgress)
                            Log.e("Progress", newProgress.toString())
                            if (newProgress > 50 && !isPageLoaded) {
                                isPageLoaded = true
                                w.evaluateJavascript(
                                    "(function() { " +
                                            "document.getElementById('input-query').value ='" + profile + "';" +
                                            "document.getElementById('btn-download').click();" +
                                            "})();"
                                ) {
                                    var done = false
                                    CoroutineScope(Dispatchers.IO).launch {
                                        for (x in 0..5) {
                                            Log.e("C", x.toString())
                                            if (done) return@launch
                                            activity.runOnUiThread {
                                                w.evaluateJavascript(
                                                    """
                                                    (function() {
                                                        var elements = document.querySelectorAll('.flex.flex-col.justify-center.items-center');
                                                        var data = [];
                                                        elements.forEach(function(element) {
                                                            var sourceTag = element.querySelector('img');
                                                            var src = sourceTag ? sourceTag.getAttribute('src') : null;
                                                            var obj = {'src': src };
                                                            if (src != null) data.push(src);
                                                        });
                                                        return data;
                                                    })();
                                                """.trimIndent()
                                                ) {
                                                    if (JSONArray(it).length() > 0) {
                                                        done = true
                                                        continuation.resume(
                                                            JSONArray(it).getString(
                                                                0
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                            delay(2000)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    activity.addContentView(w, w.layoutParams)
                    w.loadUrl("https://ttsave.app/profile")
                    w.visibility = View.GONE
                }
            }
        }
    }

    override suspend fun getProfile(activity: Activity, url: String, page: Int): List<ModelTiktok> {
        val profileID = if (url.contains("http")) extractUsernameFromTiktokUrl(url) else url
        return getWebView(activity, profileID.toProfileUrl(), true, 3000, 20000).let { view->
            delay(3000)
            withContext(Dispatchers.Main) {
                view?.evaluateJavascript(REMOVE_LOGIN, null)
                paginateWebView(view, page)
                suspendCancellableCoroutine { continuation ->
                    view?.evaluateJavascript(GET_PROFILE_DATA) { html ->
                        continuation.resume(html.toTiktokArray(profileID))
                    }
                }
            }
        }
    }
}