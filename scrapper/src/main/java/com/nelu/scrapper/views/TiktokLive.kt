package com.nelu.scrapper.views

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.annotation.Keep
import androidx.core.content.res.ResourcesCompat
import com.nelu.scrapper.R
import com.nelu.scrapper.config.App.TIKTOK
import com.nelu.scrapper.config.JSQuery.TIKTOK_AUTO_PLAY
import com.nelu.scrapper.views.JSQuery.CLICK_COPY
import com.nelu.scrapper.views.JSQuery.CLICK_SHARE
import com.nelu.scrapper.views.JSQuery.GET_USER_ID
import com.nelu.scrapper.views.JSQuery.HIDE_COPY_DIALOG
import com.nelu.scrapper.views.JSQuery.HIDE_BUTTON
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
class TiktokLive(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {


    /** Private property */
    private var onClick: OnClick? = null

    private val iconSize = 32.dpToPx(context)

    private val webView: WebView

    private val shareIcon: ImageView

    private val downloadIcon: ImageView


    /** Public accessor */
    fun pause () = webView.onPause()

    fun resume() = webView.onResume()

    fun start() = webView.loadUrl(TIKTOK)

    fun setListener(onClick: OnClick) {
        this.onClick = onClick
    }

    init {
        shareIcon = ImageView(context)
        downloadIcon = ImageView(context)

        shareIcon.elevation = 10F
        downloadIcon.elevation = 10F

        val progressBar = ProgressBar(context)
        val progressBarLayoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        progressBarLayoutParams.gravity = android.view.Gravity.CENTER
        progressBar.layoutParams = progressBarLayoutParams
        progressBar.elevation = 10F

        val customView = View(context)
        val customViewLayoutParams = LayoutParams(220.dpToPx(context), 120.dpToPx(context))
        customViewLayoutParams.gravity = android.view.Gravity.START or android.view.Gravity.BOTTOM
        customViewLayoutParams.setMargins(0, 0, 0, 0)
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
                            view?.evaluateJavascript(HIDE_BUTTON, null)
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
                            Log.e("DATA", it.toString())
                        }
                    }
                }

                shareIcon.setOnClickListener {
                    view?.evaluateJavascript(CLICK_SHARE) {
                        view.evaluateJavascript(CLICK_COPY) {
                            val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            if (clipboardManager.hasPrimaryClip()) {
                                val clipData: ClipData? = clipboardManager.primaryClip
                                if (clipData != null && clipData.itemCount > 0) {
                                    onClick?.onShare(clipData.getItemAt(0).text.toString())
                                }
                            }
                        }
                        view.evaluateJavascript(HIDE_COPY_DIALOG, null)
                    }
                }

                downloadIcon.setOnClickListener {
                    view?.evaluateJavascript(CLICK_SHARE) {
                        view.evaluateJavascript(CLICK_COPY) {
                            val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            if (clipboardManager.hasPrimaryClip()) {
                                val clipData: ClipData? = clipboardManager.primaryClip
                                if (clipData != null && clipData.itemCount > 0) {
                                    onClick?.onDownload(clipData.getItemAt(0).text.toString())
                                }
                            }
                        }
                        view.evaluateJavascript(HIDE_COPY_DIALOG, null)
                    }
                }

                view?.loadUrl(TIKTOK_AUTO_PLAY)

                progressBar.visibility = View.GONE
                webView.visibility = View.VISIBLE
            }
        }

        progressBar.visibility = View.VISIBLE
        webView.visibility = View.GONE

        val iconsLayout = LinearLayout(context)
        val iconsLayoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        iconsLayoutParams.gravity = Gravity.END or Gravity.BOTTOM
        iconsLayoutParams.bottomMargin = 48.dpToPx(context)
        iconsLayoutParams.marginEnd = 16.dpToPx(context)
        iconsLayout.layoutParams = iconsLayoutParams
        iconsLayout.orientation = LinearLayout.VERTICAL

        shareIcon.imageTintList = ColorStateList.valueOf(Color.WHITE)
        val shareIconLayoutParams = LayoutParams(iconSize, iconSize)
        shareIconLayoutParams.gravity = Gravity.END or Gravity.BOTTOM
        shareIcon.layoutParams = shareIconLayoutParams

        downloadIcon.imageTintList = ColorStateList.valueOf(Color.WHITE)
        val downloadIconLayoutParams = LayoutParams(iconSize, iconSize)
        downloadIconLayoutParams.gravity = Gravity.END or Gravity.BOTTOM
        downloadIconLayoutParams.topMargin = 24.dpToPx(context)
        downloadIcon.layoutParams = downloadIconLayoutParams

        shareIcon.setImageDrawable(
            ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.share,
                null
            )
        )

        downloadIcon.setImageDrawable(
            ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.download,
                null
            )
        )

        iconsLayout.addView(shareIcon)
        iconsLayout.addView(downloadIcon)

        progressBar.visibility = View.VISIBLE
        webView.visibility = View.GONE

        addView(progressBar)
        addView(webView)
        addView(customView)
        addView(iconsLayout)
    }

    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }

    @Keep
    interface OnClick {
        fun onShare(videoURL: String)

        fun onDownload(videoURL: String)

        fun onProfileClick(id: String)
    }
}