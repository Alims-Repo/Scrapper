package com.nelu.scrapper.data.repo.base

import com.nelu.scrapper.data.model.ModelDownload
import com.nelu.scrapper.data.model.TypeVideo

interface BaseScrapper {

    /** Other Interface Accessor */
    val tiktok: BaseTiktok

    val downloads: BaseDownloads

    /** Universal Functions */
    fun getUrlType(url : String) : TypeVideo
}