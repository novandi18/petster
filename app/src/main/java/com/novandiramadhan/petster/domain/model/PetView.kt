package com.novandiramadhan.petster.domain.model

import com.google.firebase.Timestamp

data class PetView(
    val petId: String? = null,
    val shelterId: String? = null,
    val timestamp: Timestamp? = null
)
