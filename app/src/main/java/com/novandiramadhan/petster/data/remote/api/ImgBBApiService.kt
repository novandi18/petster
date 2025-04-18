package com.novandiramadhan.petster.data.remote.api

import com.novandiramadhan.petster.data.remote.response.ImgBBResponse
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ImgBBApiService {
    @Multipart
    @POST("upload")
    suspend fun uploadImage(
        @Query("key") apiKey: String,
        @Part name: MultipartBody.Part,
        @Part image: MultipartBody.Part,
    ): ImgBBResponse
}