package com.azadevs.deepfocus.domain.repository

import com.azadevs.deepfocus.domain.model.FocusSession
import kotlinx.coroutines.flow.Flow

/**
 * Created by : Azamat Kalmurzaev
 * 20/02/2026
 */
interface FocusRepository {

    suspend fun upsertSession(session: FocusSession)

    fun getAllSessions(): Flow<List<FocusSession>>

    fun getTotalFocusMinutes(): Flow<Int>

    fun getSessionsBetween(start: Long, end: Long): Flow<List<FocusSession>>
}