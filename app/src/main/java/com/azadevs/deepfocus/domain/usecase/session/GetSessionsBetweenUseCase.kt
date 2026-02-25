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
class GetSessionsBetweenUseCase(
    private val repository: FocusRepository
) {
    operator fun invoke(
        start: Long,
        end: Long
    ): Flow<Resource<List<FocusSession>>> {
        return repository.getSessionsBetween(start, end)
            .map<List<FocusSession>, Resource<List<FocusSession>>> {
                Resource.Success(it)
            }.catch {
            emit(Resource.Error(it.message ?: "Unknown error"))
        }
    }
}