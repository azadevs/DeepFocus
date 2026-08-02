package com.azadevs.deepfocus.presentation.util.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimary,
    primaryContainer = Color(0xFFFFEBEE),
    onPrimaryContainer = Color(0xFFC62828),

    secondary = SecondaryLight,
    onSecondary = OnPrimary,
    secondaryContainer = Color(0xFFE0F7FA),
    onSecondaryContainer = Color(0xFF006064),

    tertiary = TertiaryLight,
    onTertiary = OnPrimary,
    tertiaryContainer = Color(0xFFF3E5F5),
    onTertiaryContainer = Color(0xFF4A148C),

    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnBackgroundLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight,
    outlineVariant = Color(0xFFE2E8F0),
    error = Error
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = Color(0xFF0F172A),
    primaryContainer = Color(0xFF3B1A1A),
    onPrimaryContainer = Color(0xFFFFCDD2),

    secondary = SecondaryDark,
    onSecondary = Color(0xFF0F172A),
    secondaryContainer = Color(0xFF0D3340),
    onSecondaryContainer = Color(0xFFB2EBF2),

    tertiary = TertiaryDark,
    onTertiary = Color(0xFF0F172A),
    tertiaryContainer = Color(0xFF2D1642),
    onTertiaryContainer = Color(0xFFE1BEE7),

    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnBackgroundDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark,
    outlineVariant = Color(0xFF334155),
    error = Error
)

@Composable
fun DeepFocusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val activity = context as Activity
            if (darkTheme) dynamicDarkColorScheme(activity)
            else dynamicLightColorScheme(activity)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = DeepFocusTypography,
        shapes = DeepFocusShapes,
        content = content
    )
}