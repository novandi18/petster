package com.novandiramadhan.petster.common

import com.novandiramadhan.petster.common.states.PetFilterState
import com.novandiramadhan.petster.domain.model.AuthState
import com.novandiramadhan.petster.domain.model.ShelterLocation

data class LocationFilterParams(
    val state: AuthState?,
    val filter: PetFilterState?,
    val locationActive: Boolean,
    val location: ShelterLocation?,
    val radius: Double = 10.0
)
