package com.nelu.scrapper.data.model

import com.google.gson.JsonObject

data class ModelTwitter(
    val id: String,
    val thumbnail: String,
    val url: String
) {
    companion object {
        fun ModelTwitter.toModelDownload(): ModelDownload {
            return ModelDownload(
                id = id,
                name = "",
                image = "",
                thumbnail = thumbnail,
                description = "",
                type = TypeVideo.TWITTER,
                url = url
            )
        }

        fun JsonObject.toModelTwitter(): ModelTwitter {
            get("data").asJsonObject.run {
                return ModelTwitter(
                    id  = System.currentTimeMillis().toString(),
                    thumbnail = get("HD").asString,
                    url = get("HD").asString
                )
            }
        }
    }
}