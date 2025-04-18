package com.novandiramadhan.petster.domain.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class Article(
    val id: Int,
    @StringRes val titleResId: Int,
    @StringRes val contentResId: Int,
    @DrawableRes val imageResId: Int
)