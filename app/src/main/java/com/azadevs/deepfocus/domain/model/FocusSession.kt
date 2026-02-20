package com.azadevs.deepfocus.domain.model

/**
 * Created by : Azamat Kalmurzaev
 * 20/02/2026
 */
data class FocusSession(
    val id: Long,
    val startTime: Long,
    val endTime: Long,
    val durationMinutes: Int,
    val type: SessionType
)

enum class SessionType {
    FOCUS,
    SHORT_BREAK,
    LONG_BREAK
}