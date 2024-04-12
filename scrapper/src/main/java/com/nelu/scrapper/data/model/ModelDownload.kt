package com.nelu.scrapper.data.model

import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ModelDownload(
    @PrimaryKey
    val id: String,
    val name: String,
    val image: String,
    val thumbnail: String,
    val description: String,
    val type: TypeVideo,
    val url: String,

    val audio: Boolean = false,
    val progress: Int = -1,
    val addedDate: Long = System.currentTimeMillis()
)