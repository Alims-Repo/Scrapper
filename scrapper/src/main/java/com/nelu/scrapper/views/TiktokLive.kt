package com.nelu.scrapper.views

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.annotation.Keep
import com.nelu.scrapper.config.App.TIKTOK
import com.nelu.scrapper.config.JSQuery.TIKTOK_AUTO_PLAY
import com.nelu.scrapper.views.JSQuery.GET_USER_ID
import com.nelu.scrapper.views.JSQuery.REMOVE_BUTTON
import com.nelu.scrapper.views.JSQuery.REMOVE_CONTAINER
import com.nelu.scrapper.views.JSQuery.REMOVE_FOOTER
import com.nelu.scrapper.views.JSQuery.REMOVE_HEADER
import com.nelu.scrapper.views.JSQuery.REMOVE_MODAL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray

@Keep
class TiktokLive(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    private val webView: WebView
    private var onClick: OnClick? = null

    fun setListener(onClick: OnClick) {
        this.onClick = onClick
    }

    fun start() = webView.loadUrl(TIKTOK)

    init {
        val progressBar = ProgressBar(context)
        val progressBarLayoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        progressBarLayoutParams.gravity = android.view.Gravity.CENTER
        progressBar.layoutParams = progressBarLayoutParams
        progressBar.elevation = 10F

        val customView = View(context)
        val customViewLayoutParams = LayoutParams(200.dpToPx(context), 100.dpToPx(context))
        customViewLayoutParams.gravity = android.view.Gravity.START or android.view.Gravity.BOTTOM
        customViewLayoutParams.setMargins(0, 0, 0, 32.dpToPx(context))
        customView.layoutParams = customViewLayoutParams

        // Create WebView
        webView = WebView(context)
        val webViewLayoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        webViewLayoutParams.bottomMargin = (-48).dpToPx(context)
        webView.layoutParams = webViewLayoutParams

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true

        // Set WebViewClient to handle page loading
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                view?.evaluateJavascript(REMOVE_FOOTER, null)
                view?.evaluateJavascript(REMOVE_MODAL, null)
                view?.evaluateJavascript(REMOVE_CONTAINER, null)
                view?.evaluateJavascript(REMOVE_HEADER, null)

                CoroutineScope(Dispatchers.Main).launch {
                    while (!(context as Activity).isDestroyed) {
                        try {
                            view?.evaluateJavascript(REMOVE_BUTTON, null)
                        } catch (e: Exception) { Log.d(this@TiktokLive.javaClass.name, "Line 70: $e") }
                        delay(2500)
                    }
                }

                customView.setOnClickListener {
                    view?.evaluateJavascript(GET_USER_ID) { html ->
                        JSONArray(
                            html.substring(1, html.length-1)
                                .replace("\\", "")
                        ).let {
                            (if (it.length() == 3)
                                it.getString(0)
                            else it.getString(1)).let { id->
                                onClick?.onProfileClick(id.substring(2, id.length))
                            }
                        }
                    }
                }

                view?.loadUrl(TIKTOK_AUTO_PLAY)

                progressBar.visibility = View.GONE
                webView.visibility = View.VISIBLE
            }
        }

        progressBar.visibility = View.VISIBLE
        webView.visibility = View.GONE

        addView(progressBar)
        addView(webView)
        addView(customView)
    }

    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }

    @Keep
    interface OnClick {
        fun onProfileClick(id: String)
    }
}