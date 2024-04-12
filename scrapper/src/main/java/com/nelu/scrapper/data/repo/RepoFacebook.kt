package com.nelu.scrapper.data.repo

import com.nelu.scrapper.data.model.ModelFacebook
import com.nelu.scrapper.data.model.ModelFacebook.Companion.toModelFacebook
import com.nelu.scrapper.data.model.ModelRequest
import com.nelu.scrapper.data.repo.base.BaseFacebook
import com.nelu.scrapper.di.Initializer.apiService

class RepoFacebook : BaseFacebook {

    override suspend fun getVideo(url: String): ModelFacebook? {
        return apiService.getFacebookVideo(
            ModelRequest(url)
        ).execute().let {
            if (it.isSuccessful) it.body()?.toModelFacebook() else null
        }
    }
}