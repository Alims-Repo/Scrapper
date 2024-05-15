package com.nelu.scrapper.data.repo.base

import android.app.Activity
import com.nelu.scrapper.data.model.ModelTiktok
import kotlinx.coroutines.flow.Flow

interface BaseTiktok {

    suspend fun getVideo(url: String) : ModelTiktok?

    suspend fun getProfilePic(activity: Activity, profile: String): String?

    suspend fun getProfile(activity: Activity, url: String, page: Int = 3) : List<ModelTiktok>

    suspend fun getProfilePaginate(activity: Activity, url: String, page: Int = 3) : Flow<List<ModelTiktok>>
}