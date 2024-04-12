package com.nelu.scrapper.data.repo

import com.nelu.scrapper.data.model.ModelFacebook.Companion.toModelFacebook
import com.nelu.scrapper.data.model.ModelInstagram
import com.nelu.scrapper.data.model.ModelInstagram.Companion.toModelInstagram
import com.nelu.scrapper.data.model.ModelRequest
import com.nelu.scrapper.data.repo.base.BaseInstagram
import com.nelu.scrapper.di.Initializer.apiService

class RepoInstagram: BaseInstagram {

    override suspend fun getVideo(url: String): ModelInstagram? {
        return apiService.getInstagramVideo(
            ModelRequest(url)
        ).execute().let {
            if (it.isSuccessful) it.body()?.toModelInstagram() else null
        }
    }
}