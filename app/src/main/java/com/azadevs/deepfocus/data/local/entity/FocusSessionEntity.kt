package com.azadevs.deepfocus.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by : Azamat Kalmurzaev
 * 20/02/2026
 */
@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startTime: Long,
    val endTime: Long,
    val durationMinutes: Int,
    val typeSession: String,
    val taskId: Long? = null,
    val taskTitle: String? = null
)
