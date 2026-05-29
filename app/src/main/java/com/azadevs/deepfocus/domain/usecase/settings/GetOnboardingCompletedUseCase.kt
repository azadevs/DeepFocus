package com.azadevs.deepfocus.domain.usecase.settings

import com.azadevs.deepfocus.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Created by Azamat on 29/05/2026.
 */
class GetOnboardingCompletedUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    operator fun invoke(): Flow<Boolean> = repository.isOnboardingCompleted()
}
