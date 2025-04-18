package com.novandiramadhan.petster.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.novandiramadhan.petster.R

object AlbertSans {
    val AlbertSans = FontFamily(
        Font(R.font.albert_sans, FontWeight.Normal),
        Font(R.font.albert_sans_light, FontWeight.Light),
        Font(R.font.albert_sans_bold, FontWeight.Bold)
    )
}

private val defaultTypography = Typography()
val Typography = Typography(
    bodyLarge = defaultTypography.bodyLarge.copy(
        fontFamily = AlbertSans.AlbertSans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyMedium = defaultTypography.bodyMedium.copy(
        fontFamily = AlbertSans.AlbertSans,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    bodySmall = defaultTypography.bodySmall.copy(
        fontFamily = AlbertSans.AlbertSans,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    titleLarge = defaultTypography.titleLarge.copy(
        fontFamily = AlbertSans.AlbertSans,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),
    titleMedium = defaultTypography.titleMedium.copy(
        fontFamily = AlbertSans.AlbertSans,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    ),
    titleSmall = defaultTypography.titleSmall.copy(
        fontFamily = AlbertSans.AlbertSans,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    )
)