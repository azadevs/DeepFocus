package com.azadevs.deepfocus.data.mapper

import com.azadevs.deepfocus.data.local.entity.FocusSessionEntity
import com.azadevs.deepfocus.data.local.entity.TaskEntity
import com.azadevs.deepfocus.domain.model.FocusSession
import com.azadevs.deepfocus.domain.model.SessionType
import com.azadevs.deepfocus.domain.model.Task

/**
 * Created by : Azamat Kalmurzaev
 * 20/02/2026
 */
fun FocusSessionEntity.toDomain(): FocusSession {
    return FocusSession(
        id = id,
        startTime = startTime,
        endTime = endTime,
        durationMinutes = durationMinutes,
        type = SessionType.valueOf(typeSession),
        taskId = taskId,
        taskTitle = taskTitle
    )
}

fun FocusSession.toEntity(): FocusSessionEntity {
    return FocusSessionEntity(
        id = id,
        startTime = startTime,
        endTime = endTime,
        durationMinutes = durationMinutes,
        typeSession = type.name,
        taskId = taskId,
        taskTitle = taskTitle
    )
}

fun TaskEntity.toDomain(): Task {
    return Task(
        id = id,
        title = title,
        iconName = iconName,
        colorHex = colorHex,
        totalFocusMinutes = totalFocusMinutes,
        createdAt = createdAt
    )
}

fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        iconName = iconName,
        colorHex = colorHex,
        totalFocusMinutes = totalFocusMinutes,
        createdAt = createdAt
    )
}