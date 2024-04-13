package com.nelu.scrapper.data.repo

import android.app.Activity
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

class RepoTiktok : BaseTiktok {

    override suspend fun getVideo(url: String): ModelTiktok? {
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
                        if (it.isSuccessful) thumb = it.body()?.get("thumbnail_url")?.asString.toString()
                    }
                }
            ).awaitAll()
        }
        return model?.apply { thumbnail = thumb }
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