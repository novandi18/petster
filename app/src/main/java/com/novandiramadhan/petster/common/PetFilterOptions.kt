package com.novandiramadhan.petster.common

import android.content.Context
import com.novandiramadhan.petster.R

class PetFilterOptions(context: Context) {
    val adoptionFeeRanges = listOf(
        "Free",
        "< Rp 500rb",
        "Rp 500rb - 1jt",
        "> Rp 1jt"
    )
    val categories = context.resources.getStringArray(R.array.pet_categories).toList()
    val genders = context.resources.getStringArray(R.array.pet_gender).toList()
    val vaccinated = listOf("Yes", "No")
    val size = context.resources.getStringArray(R.array.pet_size).toList()
}