package com.novandiramadhan.petster.common

object PetFilterOptions {
    val adoptionFeeRanges = listOf(
        "Free",
        "< Rp 500rb",
        "Rp 500rb - 1jt",
        "> Rp 1jt"
    )
    val categories = listOf("Dog", "Cat", "Other")
    val genders = listOf("Male", "Female")
    val vaccinated = listOf("Yes", "No")
}