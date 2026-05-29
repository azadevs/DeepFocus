package com.azadevs.deepfocus.domain.usecase.settings

import com.azadevs.deepfocus.domain.repository.SettingsRepository
import javax.inject.Inject

/**
 * Created by Azamat on 29/05/2026.
 */
class SetOnboardingCompletedUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(completed: Boolean) {
        repository.setOnboardingCompleted(completed)
    }
}
