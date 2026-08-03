package com.azadevs.deepfocus.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.azadevs.deepfocus.domain.model.Task

/**
 * Created by Azamat Kalmurzaev
 * 02/08/2026
 */
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
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

fun TaskEntity.toDomain(): Task {
    return Task(
        id = id,
        title = title,
        categoryName = categoryName,
        iconName = iconName,
        colorHex = colorHex,
        totalFocusMinutes = totalFocusMinutes,
        targetGoalMinutes = targetGoalMinutes,
        isCompleted = isCompleted,
        createdAt = createdAt
    )
}

fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        categoryName = categoryName,
        iconName = iconName,
        colorHex = colorHex,
        totalFocusMinutes = totalFocusMinutes,
        targetGoalMinutes = targetGoalMinutes,
        isCompleted = isCompleted,
        createdAt = createdAt
    )
}
