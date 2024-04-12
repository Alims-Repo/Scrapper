package com.nelu.scrapper.data.repo.base

import com.nelu.scrapper.data.model.ModelFacebook
import com.nelu.scrapper.data.model.ModelInstagram

interface BaseInstagram {

    suspend fun getVideo(url: String) : ModelInstagram?
}