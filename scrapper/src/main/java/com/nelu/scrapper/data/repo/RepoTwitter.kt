package com.nelu.scrapper.data.repo

import com.nelu.scrapper.data.model.ModelRequest
import com.nelu.scrapper.data.model.ModelTwitter
import com.nelu.scrapper.data.model.ModelTwitter.Companion.toModelTwitter
import com.nelu.scrapper.data.repo.base.BaseTwitter
import com.nelu.scrapper.di.Initializer.apiService

class RepoTwitter: BaseTwitter {

    override suspend fun getVideo(url: String): ModelTwitter? {
        return apiService.getTwitterVideo(
            ModelRequest(url)
        ).execute().let {
            if (it.isSuccessful) it.body()?.toModelTwitter() else null
        }
    }
}