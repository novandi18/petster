package com.novandiramadhan.petster.common.states

import androidx.compose.runtime.Immutable

@Immutable
data class PetFilterState(
    val selectedAdoptionFeeRange: String? = null,
    val selectedCategory: String? = null,
    val selectedGender: String? = null,
    val selectedVacinated: String? = null,
) {
    val isNotEmpty: Boolean
        get() = selectedAdoptionFeeRange != null ||
                selectedCategory != null ||
                selectedGender != null ||
                selectedVacinated != null
}