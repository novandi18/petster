package com.novandiramadhan.petster.data.remote.response

import com.google.gson.annotations.SerializedName

data class ImgBBErrorResponse(
    @SerializedName("status_code")
    val statusCode: Int,

    @SerializedName("error")
    val error: ImgBBErrorResponseDetail
)

data class ImgBBErrorResponseDetail(
    @SerializedName("message")
    val message: String,

    @SerializedName("code")
    val code: Int,
)