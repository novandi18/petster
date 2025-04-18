package com.novandiramadhan.petster.data.remote

import android.util.Base64
import android.util.Log
import com.google.gson.Gson
import com.novandiramadhan.petster.data.remote.api.ImgBBApiService
import com.novandiramadhan.petster.data.remote.request.ImgBBRequest
import com.novandiramadhan.petster.data.remote.response.ImgBBErrorResponse
import com.novandiramadhan.petster.data.remote.response.ImgBBResponse
import com.novandiramadhan.petster.data.resource.ApiResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteDataSource @Inject constructor(
    private val imgBBApiService: ImgBBApiService,
) {
    fun uploadPetImage(
        request: ImgBBRequest
    ): Flow<ApiResource<ImgBBResponse>> = channelFlow {
        try {
            val decodedBytes = Base64.decode(request.image, Base64.NO_WRAP)
            val requestBody = decodedBytes.toRequestBody("image/png".toMediaTypeOrNull())

            val image = MultipartBody.Part.createFormData(
                "image", "${System.currentTimeMillis()}.png", requestBody
            )
            val name = MultipartBody.Part.createFormData("name", System.currentTimeMillis().toString())
            val response = imgBBApiService.uploadImage(request.key, name, image)

            if (response.success) {
                trySend(ApiResource.Success(response))
            } else {
                trySend(ApiResource.Error("Upload failed"))
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = try {
                Gson().fromJson(errorBody, ImgBBErrorResponse::class.java)?.error?.message
            } catch (e: Exception) { null }
            trySend(ApiResource.Error(errorMessage ?: "Unknown error"))
            Log.e("RemoteDataSource", errorMessage ?: "Unknown error")
        } catch (e: Exception) {
            trySend(ApiResource.Error(e.toString()))
            Log.e("RemoteDataSource", e.toString())
        }
    }
}