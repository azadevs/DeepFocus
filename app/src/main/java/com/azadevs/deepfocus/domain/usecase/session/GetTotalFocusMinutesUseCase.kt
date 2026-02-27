package com.azadevs.deepfocus.domain.usecase.session

import com.azadevs.deepfocus.core.model.Resource
import com.azadevs.deepfocus.domain.repository.FocusRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Created by : Azamat Kalmurzaev
 * 21/02/2026
 */
class GetTotalFocusMinutesUseCase @Inject constructor(
    private val repository: FocusRepository
) {
    operator fun invoke(): Flow<Resource<Int>> {
        return repository.getTotalFocusMinutes()
            .map<Int, Resource<Int>> {
                Resource.Success(it)
            }.catch {
                emit(Resource.Error(it.message ?: "Unknown error"))
            }
    }
}