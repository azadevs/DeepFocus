package com.azadevs.deepfocus.data.repository

import com.azadevs.deepfocus.core.model.DataError
import com.azadevs.deepfocus.core.model.Resource
import com.azadevs.deepfocus.data.local.dao.FocusSessionDao
import com.azadevs.deepfocus.data.local.entity.FocusSessionEntity
import com.azadevs.deepfocus.data.mapper.toDomain
import com.azadevs.deepfocus.data.mapper.toEntity
import com.azadevs.deepfocus.domain.model.FocusSession
import com.azadevs.deepfocus.domain.repository.FocusRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * Created by : Azamat Kalmurzaev
 * 20/02/2026
 */
class ProdFocusRepository(
    private val focusSessionDao: FocusSessionDao
) : FocusRepository {

    override suspend fun upsertSession(
        session: FocusSession
    ): Resource<Unit> {
        return try {
            focusSessionDao.upsert(session.toEntity())
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(DataError.DatabaseError)
        }
    }

    override fun getAllSessions(): Flow<Resource<List<FocusSession>>> =
        focusSessionDao.getAllSessions()
            .map<List<FocusSessionEntity>, Resource<List<FocusSession>>> { entities ->
                Resource.Success(entities.map { it.toDomain() })
            }.catch { emit(Resource.Error(DataError.DatabaseError)) }

    override fun getTotalFocusMinutes(): Flow<Resource<Int>> =
        focusSessionDao.getTotalFocusDuration().map<Int?, Resource<Int>> { total ->
            Resource.Success(total ?: 0)
        }.catch {
            emit(Resource.Error(DataError.DatabaseError))
        }

    override fun getSessionsBetween(start: Long, end: Long): Flow<Resource<List<FocusSession>>> =
        focusSessionDao.getSessionsBetween(
            start = start,
            end = end
        ).map<List<FocusSessionEntity>, Resource<List<FocusSession>>> { entities ->
            Resource.Success(entities.map { it.toDomain() })
        }.catch {
            emit(Resource.Error(DataError.DatabaseError))
        }
}