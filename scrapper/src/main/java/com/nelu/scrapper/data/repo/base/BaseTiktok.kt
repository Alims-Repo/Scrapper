package com.nelu.scrapper.data.repo.base

import android.app.Activity
import com.nelu.scrapper.data.model.ModelTiktok

interface BaseTiktok {

    suspend fun getViewInfo(url: String) : ModelTiktok?

    suspend fun getProfile(activity: Activity, url: String) : List<ModelTiktok>
}