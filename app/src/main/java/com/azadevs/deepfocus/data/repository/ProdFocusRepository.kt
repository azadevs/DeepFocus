package com.azadevs.deepfocus.data.repository

import com.azadevs.deepfocus.data.local.dao.FocusSessionDao
import com.azadevs.deepfocus.data.mapper.toDomain
import com.azadevs.deepfocus.data.mapper.toEntity
import com.azadevs.deepfocus.domain.model.FocusSession
import com.azadevs.deepfocus.domain.repository.FocusRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Created by : Azamat Kalmurzaev
 * 20/02/2026
 */
class ProdFocusRepository @Inject constructor(
    private val focusSessionDao: FocusSessionDao
) : FocusRepository {

    override suspend fun upsertSession(session: FocusSession) {
        focusSessionDao.upsert(session.toEntity())
    }

    override fun getAllSessions(): Flow<List<FocusSession>> =
        focusSessionDao.getAllSessions()
            .map { entities -> entities.map { it.toDomain() } }

    override fun getTotalFocusMinutes(): Flow<Int> =
        focusSessionDao.getTotalFocusDuration()
            .map { total -> total ?: 0 }

    override fun getSessionsBetween(start: Long, end: Long): Flow<List<FocusSession>> =
        focusSessionDao.getSessionsBetween(start, end)
            .map { entities -> entities.map { it.toDomain() } }
}