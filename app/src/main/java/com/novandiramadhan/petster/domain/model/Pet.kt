package com.novandiramadhan.petster.domain.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

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

    @field:JvmField
    @field:PropertyName("adopted")
    val isAdopted: Boolean = false,

    @field:JvmField
    @field:PropertyName("vaccinated")
    val isVaccinated: Boolean = false,

    @field:JvmField
    @field:PropertyName("favorite")
    val isFavorite: Boolean = false,

    val volunteer: String? = null,
    val createdAt: Timestamp? = null,
    val image: PetImage? = null,
    val viewCount: Int? = null,
)