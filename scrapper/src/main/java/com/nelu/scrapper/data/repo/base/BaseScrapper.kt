package com.nelu.scrapper.data.repo.base

import com.nelu.scrapper.data.model.ModelDownload
import com.nelu.scrapper.data.model.TypeVideo

interface BaseScrapper {

    /** Other Interface Accessor */
    val tiktok: BaseTiktok

    /** Universal Functions */
    fun getUrlType(url : String) : TypeVideo

    suspend fun download(model : ModelDownload) : Boolean

    suspend fun download(model: ArrayList<ModelDownload>) : Boolean
}