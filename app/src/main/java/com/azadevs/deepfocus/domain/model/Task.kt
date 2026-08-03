package com.azadevs.deepfocus.domain.model

/**
 * Created by Azamat Kalmurzaev
 * 02/08/2026
 */
data class Task(
    val id: Long = 0,
    val title: String,
    val categoryName: String = "WORK",
    val iconName: String = "Bookmark",
    val colorHex: String = "#FF5252",
    val totalFocusMinutes: Int = 0,
    val targetGoalMinutes: Int = 0,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

enum class TaskCategory(val displayName: String, val iconEmoji: String) {
    ALL("All", "⚡"),
    WORK("Work", "💼"),
    STUDY("Study", "📚"),
    PERSONAL("Personal", "🏠"),
    HEALTH("Health", "🏃"),
    OTHER("Other", "🎯")
}
