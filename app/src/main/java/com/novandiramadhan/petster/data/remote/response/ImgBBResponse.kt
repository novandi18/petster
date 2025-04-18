package com.novandiramadhan.petster.data.remote.response

import com.google.gson.annotations.SerializedName

data class ImgBBResponse(
    @SerializedName("data")
    val data: ImgBBResponseData,

    @SerializedName("success")
    val success: Boolean,

    @SerializedName("status")
    val status: Int
)

data class ImgBBResponseData(
    @SerializedName("id")
    val id: String,

    @SerializedName("display_url")
    val url: String,
)