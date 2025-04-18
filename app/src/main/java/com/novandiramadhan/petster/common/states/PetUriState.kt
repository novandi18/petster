package com.novandiramadhan.petster.common.states

data class PetUriState(
    val uri: String = "",
    val base64Data: String = "",
    var isCover: Boolean = false
)