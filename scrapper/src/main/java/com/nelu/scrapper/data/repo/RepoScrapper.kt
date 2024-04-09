package com.nelu.scrapper.data.repo

import com.nelu.scrapper.data.repo.base.BaseScrapper
import com.nelu.scrapper.data.repo.base.BaseTiktok
import com.nelu.scrapper.data.model.ModelDownload
import com.nelu.scrapper.data.model.TypeVideo
import com.nelu.scrapper.di.Initializer.daoDownloads
import com.nelu.scrapper.service.Downloader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.regex.Pattern

class RepoScrapper : BaseScrapper {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override val tiktok: BaseTiktok = RepoTiktok()

    override fun getUrlType(url: String) =  when {
        isTikTokUrl(url) -> TypeVideo.TIKTOK
        isFacebookUrl(url) -> TypeVideo.FACEBOOK
        isInstagramUrl(url) -> TypeVideo.INSTAGRAM
        isTwitterUrl(url) -> TypeVideo.TWITTER
        else -> TypeVideo.UNKNOWN
    }

    override suspend fun download(model: ModelDownload): Boolean {
        Downloader(coroutineScope, model).start()
//        daoDownloads.insertDownloads(model)
        return true
    }

    override suspend fun download(model: ArrayList<ModelDownload>): Boolean {
        model.forEach { Downloader(coroutineScope, it).start() }
//        daoDownloads.insertDownloads(model)
        return true
    }

    private fun isTikTokUrl(url: String): Boolean {
        val tiktokPattern = Pattern.compile("^(https?://)?(www\\.)?(tiktok\\.com)")
        return tiktokPattern.matcher(url).find()
    }

    private fun isFacebookUrl(url: String): Boolean {
        val facebookPattern = Pattern.compile("^(https?://)?(www\\.)?(facebook\\.com)")
        return facebookPattern.matcher(url).find()
    }

    private fun isInstagramUrl(url: String): Boolean {
        val instagramPattern = Pattern.compile("^(https?://)?(www\\.)?(instagram\\.com)")
        return instagramPattern.matcher(url).find()
    }

    private fun isTwitterUrl(url: String): Boolean {
        val twitterPattern = Pattern.compile("^(https?://)?(www\\.)?(twitter\\.com)")
        return twitterPattern.matcher(url).find()
    }
}