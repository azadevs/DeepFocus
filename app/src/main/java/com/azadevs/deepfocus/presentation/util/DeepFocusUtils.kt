package com.azadevs.deepfocus.presentation.util

import kotlin.math.max

/**
 * Created by : Azamat Kalmurzaev
 * 24/02/2026
 */
object DeepFocusUtils {
    fun formatTime(millis: Long): String {
        val safe = max(0L, millis)
        val totalSeconds = safe / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%02d:%02d".format(minutes, seconds)
    }
}