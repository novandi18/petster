package com.novandiramadhan.petster.domain.model

data class PetHome(
    val dog: List<Pet>,
    val cat: List<Pet>,
    val other: List<Pet>
)
