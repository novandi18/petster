package com.novandiramadhan.petster.domain.model

data class PetImage(
    val imageCoverUrl: String? = null,
    val deleteImageCoverUrl: String? = null,
    val imageUrls: List<String>? = null,
    val deleteImageUrls: List<String>? = null
)