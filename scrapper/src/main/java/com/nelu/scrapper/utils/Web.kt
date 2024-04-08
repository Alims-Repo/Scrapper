package com.nelu.scrapper.utils

import android.util.Log
import android.webkit.WebView

fun paginateWebView(webview: WebView?) {
    webview?.evaluateJavascript(
        "(function() { return document.body.scrollHeight; })();"
    ) { result ->
        Log.e("Res", result.toString())
        val totalHeight = result?.toIntOrNull() ?: 0
        val pageSize = 1000
        var currentPosition = 0

        while (currentPosition < totalHeight) {
            val script = "window.scrollTo(0, ${currentPosition + pageSize});"
            webview.evaluateJavascript(script, null)
            currentPosition += pageSize
        }
    }
}