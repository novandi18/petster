package com.novandiramadhan.petster.domain.model

sealed class UserResult {
    data class VolunteerResult(val volunteer: Volunteer) : UserResult()
    data class ShelterResult(val shelter: Shelter) : UserResult()
}