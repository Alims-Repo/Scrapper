package com.nelu.scrapper.data.repo.base

import android.app.Activity
import com.nelu.scrapper.data.model.ModelTiktok

interface BaseTiktok {

    suspend fun getVideo(url: String) : ModelTiktok?

    suspend fun getProfilePic(activity: Activity, profile: String): String?

    suspend fun getProfile(activity: Activity, url: String, page: Int = 3) : List<ModelTiktok>
}