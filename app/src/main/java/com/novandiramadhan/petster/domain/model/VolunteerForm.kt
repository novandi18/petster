package com.novandiramadhan.petster.domain.model

    data class VolunteerForm(
        val name: String,
        val email: String,
        val password: String,
        val phoneNumber: String,
        val address: String,
    )