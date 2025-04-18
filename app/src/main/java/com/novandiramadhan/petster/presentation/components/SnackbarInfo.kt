package com.novandiramadhan.petster.presentation.components

import androidx.annotation.StringRes

sealed interface SnackbarInfo {
    data class Info(val message: String) : SnackbarInfo

    data class ResourceError(
        @StringRes val messageResId: Int,
        val fallbackMessage: String
    ) : SnackbarInfo

    data class ResourceSuccess(
        @StringRes val messageResId: Int,
        val fallbackMessage: String
    ) : SnackbarInfo
}