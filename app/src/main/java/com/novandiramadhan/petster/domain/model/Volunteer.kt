package com.novandiramadhan.petster.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Volunteer(
    val uuid: String? = null,
    val name: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val address: String? = null,
    val location: VolunteerLocation? = null,
)

@Serializable
data class VolunteerLocation(
    val latitude: Double? = null,
    val longitude: Double? = null,
)