package com.novandiramadhan.petster.domain.model

data class VolunteerAuthResult(
    val volunteer: Volunteer? = null,
    val isLoginType: Boolean,
)