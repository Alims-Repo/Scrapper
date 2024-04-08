package com.nelu.scrapper.data.model

import com.google.gson.JsonObject

data class ModelTiktok(
    val id: String,
    val name: String,
    val profileImage: String,
    val thumbnail: String,
    val title: String,
    val music: String,
    val water: String,
    val noWaterSD: String,
    val noWaterHD: String?
) {
    companion object {
        fun JsonObject.toModelTiktok(): ModelTiktok {
            get("data").asJsonObject.get("result").asJsonObject.run {
                return ModelTiktok(
                    id  = "",
                    name = get("author").asJsonObject.get("nickname").asString,
                    profileImage = get("author").asJsonObject.get("avatar").asString,
                    thumbnail = "",
                    title = get("desc").asString,
                    music =  get("music").asString,
                    water = get("video_watermark").asString,
                    noWaterSD =  get("video1").asString,
                    noWaterHD = get("video_hd")?.asString,
                )
            }
        }
    }
}