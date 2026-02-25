package com.azadevs.deepfocus.domain.usecase.session

import com.azadevs.deepfocus.core.model.Resource
import com.azadevs.deepfocus.domain.model.FocusSession
import com.azadevs.deepfocus.domain.repository.FocusRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * Created by : Azamat Kalmurzaev
 * 21/02/2026
 */
class GetAllSessionUseCase(
    private val repository: FocusRepository
) {
    operator fun invoke(): Flow<Resource<List<FocusSession>>> {
        return repository.getAllSessions()
            .map<List<FocusSession>, Resource<List<FocusSession>>> { sessions ->
                Resource.Success(sessions)
            }
            .catch { e ->
                emit(Resource.Error(e.message ?: "Unknown error"))
            }
    }
}