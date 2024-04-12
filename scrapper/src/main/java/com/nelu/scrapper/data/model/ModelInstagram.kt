package com.nelu.scrapper.data.model

import com.google.gson.JsonObject

data class ModelInstagram(
    val id: String,
    val thumbnail: String,
    val url: String
) {
    companion object {

        fun ModelInstagram.toModelDownload(): ModelDownload {
            return ModelDownload(
                id = id,
                name = "",
                image = "",
                thumbnail = thumbnail,
                description = "",
                type = TypeVideo.INSTAGRAM,
                url = url
            )
        }

        fun JsonObject.toModelInstagram(): ModelInstagram {
            get("data").asJsonArray.get(0).asJsonObject.run {
                return ModelInstagram(
                    id  = System.currentTimeMillis().toString(),
                    thumbnail = get("thumbnail").asString,
                    url = get("url").asString
                )
            }
        }
    }
}