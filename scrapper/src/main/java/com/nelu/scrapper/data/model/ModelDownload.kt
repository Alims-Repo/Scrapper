package com.nelu.scrapper.data.model

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
    val type: TypeVideo
)