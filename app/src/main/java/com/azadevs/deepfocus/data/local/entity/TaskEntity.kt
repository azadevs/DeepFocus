package com.azadevs.deepfocus.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Azamat Kalmurzaev
 * 02/08/2026
 */
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val iconName: String = "Bookmark",
    val colorHex: String = "#FF5252",
    val totalFocusMinutes: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
