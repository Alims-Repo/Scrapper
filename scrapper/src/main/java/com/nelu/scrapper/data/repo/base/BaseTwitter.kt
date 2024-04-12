package com.nelu.scrapper.data.repo.base

import com.nelu.scrapper.data.model.ModelFacebook
import com.nelu.scrapper.data.model.ModelTwitter

interface BaseTwitter {

    suspend fun getVideo(url: String) : ModelTwitter?
}