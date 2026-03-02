package com.azadevs.deepfocus.presentation.util.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.azadevs.deepfocus.R

val PoppinsFontFamily = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_bold, FontWeight.Bold)
)

val DeepFocusTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = PoppinsFontFamily,
        fontSize = 57.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    headlineLarge = TextStyle(
        fontFamily = PoppinsFontFamily,
        fontSize = 40.sp,
        fontWeight = FontWeight.Bold
    ),
    titleMedium = TextStyle( // Added for subtitles
        fontFamily = PoppinsFontFamily,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleLarge = TextStyle(
        fontFamily = PoppinsFontFamily,
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold
    ),
    bodyLarge = TextStyle(
        fontFamily = PoppinsFontFamily,
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal
    ),
    labelMedium = TextStyle( // Added for cycle badge
        fontFamily = PoppinsFontFamily,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)