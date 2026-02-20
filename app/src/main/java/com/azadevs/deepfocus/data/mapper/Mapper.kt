package com.azadevs.deepfocus.data.mapper

import com.azadevs.deepfocus.data.local.entity.FocusSessionEntity
import com.azadevs.deepfocus.domain.model.FocusSession
import com.azadevs.deepfocus.domain.model.SessionType

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
        type = SessionType.valueOf(typeSession)
    )
}

fun FocusSession.toEntity(): FocusSessionEntity {
    return FocusSessionEntity(
        id = id,
        startTime = startTime,
        endTime = endTime,
        durationMinutes = durationMinutes,
        typeSession = type.name
    )
}