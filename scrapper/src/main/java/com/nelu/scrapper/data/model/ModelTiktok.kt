package com.nelu.scrapper.data.model

import com.google.gson.JsonObject
import com.nelu.scrapper.config.App.DOWNLOAD_AUDIO_LINK
import com.nelu.scrapper.config.App.DOWNLOAD_ORIGINAL_VIDEO_LINK
import com.nelu.scrapper.config.App.DOWNLOAD_VIDEO_WITHOUT_WATERMARK
import com.nelu.scrapper.config.App.DOWNLOAD_VIDEO_WITHOUT_WATERMARK_HD
import com.nelu.scrapper.utils.extractVideoIdFromTiktokUrl
import org.json.JSONArray

data class ModelTiktok(
    var id: String,
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
        fun ModelTiktok.toModelDownload(downloadURL: String, audio: Boolean = false): ModelDownload {
            return ModelDownload(
                id = id,
                name = name,
                image = profileImage,
                thumbnail = thumbnail,
                description = title,
                type = TypeVideo.TIKTOK,
                url = downloadURL,
                audio = audio
            )
        }

        suspend fun JsonObject.toModelTiktok(url: String): ModelTiktok {
            get("data").asJsonObject.get("result").asJsonObject.run {
                return ModelTiktok(
                    id  = extractVideoIdFromTiktokUrl(url),
                    name = try { get("author").asJsonObject.get("nickname").asString } catch (e: Exception) { "" },
                    profileImage = try { get("author").asJsonObject.get("avatar").asString } catch (e: Exception) { "" },
                    thumbnail = "",
                    title = if (has("desc")) get("desc").asString else "",
                    music = if (has("music")) get("music").asString else "",
                    water = if (has("video_watermark")) get("video_watermark").asString else "",
                    noWaterSD = if (has("video1")) get("video1").asString else "",
                    noWaterHD = if (has("video_hd")) get("video_hd")?.asString else "",
                )
            }
        }

        fun String.toTiktokArray(profileID: String): ArrayList<ModelTiktok> {
//            val final = this.replace("\\","")
            val arrayData = JSONArray(this)

            return ArrayList<ModelTiktok>().also {
                for (x in 0 until arrayData.length()) {
                    val obj = arrayData.getJSONObject(x)
                    val videoID = extractVideoIdFromTiktokUrl(obj.getString("href"))
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