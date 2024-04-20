package com.nelu.scrapper.data.repo.base

import android.app.Activity
import com.nelu.scrapper.data.model.ModelFacebook
import com.nelu.scrapper.data.model.ModelTiktok

interface BaseFacebook {

    suspend fun getVideo(url: String) : ModelFacebook?

    suspend fun getVideo(url: String, activity: Activity) : String?
}