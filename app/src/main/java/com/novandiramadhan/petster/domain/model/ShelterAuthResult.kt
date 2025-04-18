package com.novandiramadhan.petster.domain.model

data class ShelterAuthResult(
    val shelter: Shelter? = null,
    val isLoginType: Boolean,
)