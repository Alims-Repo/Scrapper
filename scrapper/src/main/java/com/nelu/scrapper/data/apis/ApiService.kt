package com.nelu.scrapper.data.apis

import com.google.gson.JsonObject
import com.nelu.scrapper.data.model.ModelRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Streaming
import retrofit2.http.Url

interface ApiService {
    @POST("download/tiktok")
    fun getTiktokList(@Body request: ModelRequest): Call<JsonObject>

    @Streaming
    @GET
    fun downloadFile(@Url fileUrl: String?): Call<ResponseBody?>
}