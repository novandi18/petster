package com.novandiramadhan.petster.domain.model

import com.google.firebase.Timestamp

data class Pet(
    val id: String? = null,
    val name: String? = null,
    val category: String? = null,
    val color: String? = null,
    val age: Int? = null,
    val ageUnit: String? = null,
    val gender: String? = null,
    val weight: String? = null,
    val weightUnit: String? = null,
    val behaviours: List<String>? = null,
    val size: String? = null,
    val adoptionFee: Int? = null,
    val specialDiet: String? = null,
    val disabilities: List<String>? = null,
    val breed: String? = null,
    val isAdopted: Boolean = false,
    val isVaccinated: Boolean = false,
    val isFavorite: Boolean = false,
    val volunteer: String? = null,
    val createdAt: Timestamp? = null,
    val image: PetImage? = null,
    val viewCount: Int? = null,
)