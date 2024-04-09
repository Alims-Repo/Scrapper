package com.nelu.scrapper.data.model

import com.google.gson.JsonObject
import com.nelu.scrapper.config.App.DOWNLOAD_AUDIO_LINK
import com.nelu.scrapper.config.App.DOWNLOAD_ORIGINAL_VIDEO_LINK
import com.nelu.scrapper.config.App.DOWNLOAD_VIDEO_WITHOUT_WATERMARK
import com.nelu.scrapper.config.App.DOWNLOAD_VIDEO_WITHOUT_WATERMARK_HD
import com.nelu.scrapper.utils.extractVideoIdFromUrl
import org.json.JSONArray

data class ModelTiktok(
    val id: String,
    val name: String,
    val profileImage: String,
    var thumbnail: String,
    val title: String,
    val music: String,
    val water: String,
    val noWaterSD: String,
    val noWaterHD: String?
) {
    companion object {
        fun ModelTiktok.toModelDownload(downloadURL: String): ModelDownload {
            return ModelDownload(
                id = id,
                name = name,
                image = profileImage,
                thumbnail = thumbnail,
                description = title,
                type = TypeVideo.TIKTOK,
                url = downloadURL
            )
        }


        suspend fun JsonObject.toModelTiktok(url: String): ModelTiktok {
            get("data").asJsonObject.get("result").asJsonObject.run {
                return ModelTiktok(
                    id  = extractVideoIdFromUrl(url),
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

        fun String.toTiktokArray(profileID: String): ArrayList<ModelTiktok> {
            val final = this.replace("\\","")
            val arrayData = JSONArray(final.substring(1, final.length-1))

            return ArrayList<ModelTiktok>().also {
                for (x in 0 until arrayData.length()) {
                    val obj = arrayData.getJSONObject(x)
                    val videoID = extractVideoIdFromUrl(obj.getString("href"))
                    it.add(
                        ModelTiktok(
                            id = videoID ?: System.currentTimeMillis().toString(),
                            name = profileID,
                            profileImage = "",
                            thumbnail = obj.getString("src"),
                            title = obj.getString("alt"),
                            music = "$DOWNLOAD_AUDIO_LINK$videoID.mp3",
                            water = "$DOWNLOAD_ORIGINAL_VIDEO_LINK$videoID.mp4",
                            noWaterSD = "$DOWNLOAD_VIDEO_WITHOUT_WATERMARK$videoID.mp4",
                            noWaterHD = "$DOWNLOAD_VIDEO_WITHOUT_WATERMARK_HD$videoID.mp4"
                        )
                    )
                }
            }
        }
    }
}