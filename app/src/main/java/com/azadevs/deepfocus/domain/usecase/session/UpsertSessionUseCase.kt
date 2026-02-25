package com.azadevs.deepfocus.domain.usecase.session

import com.azadevs.deepfocus.core.model.Resource
import com.azadevs.deepfocus.domain.model.FocusSession
import com.azadevs.deepfocus.domain.repository.FocusRepository

/**
 * Created by : Azamat Kalmurzaev
 * 21/02/2026
 */
class UpsertSessionUseCase(
    private val repository: FocusRepository
) {
    suspend operator fun invoke(
        session: FocusSession
    ): Resource<Unit> {
        if (session.durationMinutes <= 0) {
            return Resource.Error(IllegalArgumentException("Invalid duration"))
        }
        return Resource.Success(repository.upsertSession(session))
    }
}