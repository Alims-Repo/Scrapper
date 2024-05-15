package com.nelu.scrapper.utils

import android.app.Activity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.nelu.scrapper.config.App.DOWNLOAD_AUDIO_LINK
import com.nelu.scrapper.config.App.DOWNLOAD_ORIGINAL_VIDEO_LINK
import com.nelu.scrapper.config.App.DOWNLOAD_VIDEO_WITHOUT_WATERMARK
import com.nelu.scrapper.config.App.DOWNLOAD_VIDEO_WITHOUT_WATERMARK_HD
import com.nelu.scrapper.config.App.USER_AGENT
import com.nelu.scrapper.config.JSQuery.GET_PROFILE_DATA
import com.nelu.scrapper.config.JSQuery.REMOVE_LOGIN
import com.nelu.scrapper.data.model.ModelTiktok
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import kotlin.coroutines.resume

suspend fun getWebView(activity: Activity, url: String, desktop: Boolean = false, width: Int = 1, height: Int = 1): WebView? {
    return suspendCancellableCoroutine { continuation ->
        activity.runOnUiThread {
            WebView(activity).run {
                layoutParams = ViewGroup.LayoutParams(width, height)
                webChromeClient = object : WebChromeClient() {
                    private var isPageLoaded = false
                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                        super.onProgressChanged(view, newProgress)
                        if (newProgress == 100 && !isPageLoaded) {
                            isPageLoaded = true
                            continuation.resume(view)
                        }
                    }
                }

                settings.run {
                    if (desktop) {
                        userAgentString = USER_AGENT
                        loadWithOverviewMode = true
                        useWideViewPort = true
                    }
                    javaScriptEnabled = true
                    domStorageEnabled = true
                }

                activity.addContentView(this, layoutParams)
                loadUrl(url)
                visibility = View.INVISIBLE
            }
        }
    }
}

suspend fun getThumbnail(activity: Activity, url: String): String {
    return suspendCancellableCoroutine { continuation ->
        activity.runOnUiThread {
            WebView(activity).let { w ->
                w.layoutParams = ViewGroup.LayoutParams(1, 1)
                val s = w.settings

                s.javaScriptEnabled = true

                s.domStorageEnabled = true

                w.webChromeClient = object : WebChromeClient() {
                    var sent = false
                    private var isPageLoaded = false
                    override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                        try {
                            JSONObject(
                                consoleMessage.message().toString()
                            ).getString("thumbnail_url").let {
                                continuation.resume(it)
                            }
                        } catch (e: JSONException) {
                            Log.d("CONSOLE EXC", e.toString())
                        } catch (e: Exception) {
                            Log.d("CONSOLE EXC", e.toString())
                        }
                        return true
                    }

                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                        super.onProgressChanged(view, newProgress)
                        if (newProgress == 100 && !isPageLoaded) {
                            isPageLoaded = true
                            w.evaluateJavascript(
                                "(function() { " +
                                        "document.getElementById('link').value ='" + url + "';" +
                                        "document.getElementById('make').click();" +
                                        "})();", null
                            )
                        }
                    }
                }

                activity.addContentView(w, w.layoutParams)
                w.loadUrl("file:///android_asset/index.html")
                w.visibility = View.GONE
            }
        }
    }
}

//suspend fun paginateWebView(webView: WebView?, count: Int = 3) {
//    // Define a function for recursive pagination
//    suspend fun paginateRecursively(currentPosition: Int = 0, pageCount: Int = count) {
//        if (currentPosition >= pageCount) {
//            return  // Exit recursion when pagination is complete
//        }
//
//        // Evaluate JavaScript to get total height of the page
//        webView?.evaluateJavascript("(function() { return document.body.scrollHeight; })();") { result ->
//            val totalHeight = result?.toIntOrNull() ?: 0
//            val pageSize = 1000
//
//            // Paginate by scrolling to each section of the page
//            var scrollToPosition = currentPosition * pageSize
//            while (scrollToPosition < totalHeight) {
//                val script = "window.scrollTo(0, $scrollToPosition);"
//                webView?.evaluateJavascript(script, null)
//                scrollToPosition += pageSize
//                delay(500)  // Adjust delay as needed to allow time for page rendering
//            }
//
//            // Continue recursive pagination for the next section
//            coroutineScope.launch {
//                paginateRecursively(currentPosition + 1, pageCount)
//            }
//        }
//    }
//
//    // Start pagination
//    paginateRecursively()
//}


suspend fun paginateWebView(webView: WebView?, count: Int = 3) {
    var currentPosition : Int = 0
    for (x in count downTo 0) {
        webView?.evaluateJavascript(
            "(function() { return document.body.scrollHeight; })();"
        ) { result ->
            val totalHeight = result?.toIntOrNull() ?: 0
            val pageSize = 1000

            while (currentPosition < totalHeight) {
                val script = "window.scrollTo(0, ${currentPosition + pageSize});"
                webView.evaluateJavascript(script, null)
                currentPosition += pageSize
            }
        }
        delay(2000)
    }
}

suspend fun paginateOnce(webView: WebView?, currentPosition : Int) {
    webView?.evaluateJavascript(
        "(function() { return document.body.scrollHeight; })();"
    ) { result ->
        val totalHeight = result?.toIntOrNull() ?: 0
        val pageSize = 1000

        while (currentPosition < totalHeight) {
            val script = "window.scrollTo(0, ${currentPosition + pageSize});"
            webView.evaluateJavascript(script, null)
        }
    }
}