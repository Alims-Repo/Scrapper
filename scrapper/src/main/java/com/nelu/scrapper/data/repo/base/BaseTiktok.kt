package com.nelu.scrapper.data.repo.base

import android.app.Activity
import com.nelu.scrapper.data.model.ModelTiktok

interface BaseTiktok {

    suspend fun getVideo(url: String) : ModelTiktok?

    suspend fun getProfile(activity: Activity, url: String, page: Int = 3) : List<ModelTiktok>
}