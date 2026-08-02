package com.azadevs.deepfocus.domain.model

/**
 * Created by Azamat Kalmurzaev
 * 02/08/2026
 */
data class Task(
    val id: Long = 0,
    val title: String,
    val iconName: String = "Bookmark",
    val colorHex: String = "#FF5252",
    val totalFocusMinutes: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
