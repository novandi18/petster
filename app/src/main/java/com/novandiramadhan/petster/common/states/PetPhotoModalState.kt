package com.novandiramadhan.petster.common.states

data class PetPhotoModalState(
    val isShowed: Boolean = false,
    val state: PetPhotoState = PetPhotoState.ADD,
    val photoUriIndex: Int = -1,
    val originalIndex: Int = -1,
    val isPhotoCover: Boolean = false,
    val isUrl: Boolean = false,
)

enum class PetPhotoState {
    ADD,
    UPDATE
}