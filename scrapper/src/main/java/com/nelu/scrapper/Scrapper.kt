package com.nelu.scrapper

import android.app.Application
import android.content.Context
import androidx.annotation.Keep
import com.nelu.scrapper.config.App
import com.nelu.scrapper.data.repo.base.BaseScrapper
import com.nelu.scrapper.data.repo.base.BaseTiktok
import com.nelu.scrapper.data.model.ModelDownload
import com.nelu.scrapper.data.model.TypeVideo
import com.nelu.scrapper.data.repo.base.BaseDownloads
import com.nelu.scrapper.data.repo.base.BaseFacebook
import com.nelu.scrapper.data.repo.base.BaseInstagram
import com.nelu.scrapper.data.repo.base.BaseTwitter
import com.nelu.scrapper.di.Scrapper

@Keep
object Scrapper : BaseScrapper {

    lateinit var context: Application

    /** Required variables to hold single instance of Scrapper */
    private lateinit var scrapper: BaseScrapper

    override val tiktok: BaseTiktok get() = scrapper.tiktok

    override val twitter: BaseTwitter get() = scrapper.twitter

    override val facebook: BaseFacebook get() = scrapper.facebook

    override val instagram: BaseInstagram get() = scrapper.instagram

    override val downloads: BaseDownloads get() = scrapper.downloads

    override fun getUrlType(url: String) = scrapper.getUrlType(url)

    fun init(application: Application) {
        context = application
        check(this::context.isInitialized) {
            "Scrapper not initialized, call Scrapper.init(this) from your application class"
        }
        if (!this::scrapper.isInitialized) scrapper = Scrapper().getScrapper()
    }
}