package com.azadevs.deepfocus.domain.repository

import com.azadevs.deepfocus.core.model.Resource
import com.azadevs.deepfocus.domain.model.FocusSession
import kotlinx.coroutines.flow.Flow

/**
 * Created by : Azamat Kalmurzaev
 * 20/02/2026
 */
interface FocusRepository {

    suspend fun upsertSession(session: FocusSession): Resource<Unit>

    fun getAllSessions(): Flow<Resource<List<FocusSession>>>

    fun getTotalFocusMinutes(): Flow<Resource<Int>>

    fun getSessionsBetween(start: Long, end: Long): Flow<Resource<List<FocusSession>>>

}