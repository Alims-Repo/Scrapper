package com.nelu.scrapper.data.model

import com.google.gson.JsonObject
import com.nelu.scrapper.utils.extractIdFromFacebookUrl
import com.nelu.scrapper.utils.extractVideoIdFromTiktokUrl

data class ModelFacebook(
    val id: String,
    val resolution: String,
    val thumbnail: String,
    val url: String
) {

    companion object {
        fun ModelFacebook.toModelDownload(): ModelDownload {
            return ModelDownload(
                id = id,
                name = "",
                image = "",
                thumbnail = thumbnail,
                description = "",
                type = TypeVideo.FACEBOOK,
                url = url
            )
        }

        fun JsonObject.toModelFacebook(): ModelFacebook {
            get("data").asJsonArray.get(0).asJsonObject.run {
                return ModelFacebook(
                    id  = System.currentTimeMillis().toString(),
                    resolution = get("resolution").asString,
                    thumbnail = get("thumbnail").asString,
                    url = get("url").asString
                )
            }
        }
    }
}