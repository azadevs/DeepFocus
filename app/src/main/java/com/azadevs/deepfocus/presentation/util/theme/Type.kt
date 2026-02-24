package com.azadevs.deepfocus.presentation.util.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val DeepFocusTypography = Typography(
    headlineLarge = TextStyle(
        fontSize = 40.sp,
        fontWeight = FontWeight.Bold
    ),
    titleLarge = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold
    ),
    bodyLarge = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal
    )
)