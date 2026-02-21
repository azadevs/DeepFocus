package com.azadevs.deepfocus.domain.usecase.session

import com.azadevs.deepfocus.core.model.Resource
import com.azadevs.deepfocus.domain.repository.FocusRepository
import kotlinx.coroutines.flow.Flow

/**
 * Created by : Azamat Kalmurzaev
 * 21/02/2026
 */
class GetTotalFocusMinutesUseCase(
    private val repository: FocusRepository
) {
    operator fun invoke(): Flow<Resource<Int>> {
        return repository.getTotalFocusMinutes()
    }
}