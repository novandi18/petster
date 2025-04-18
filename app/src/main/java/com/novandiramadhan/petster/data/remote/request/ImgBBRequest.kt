package com.novandiramadhan.petster.data.remote.request

import com.novandiramadhan.petster.BuildConfig

data class ImgBBRequest(
    val key: String = BuildConfig.IMGBB_API_KEY,
    val image: String
)