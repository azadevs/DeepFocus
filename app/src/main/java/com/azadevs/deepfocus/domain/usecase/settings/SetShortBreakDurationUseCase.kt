package com.azadevs.deepfocus.domain.usecase.settings

import com.azadevs.deepfocus.domain.repository.SettingsRepository
import jakarta.inject.Inject

class SetShortBreakDurationUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(duration: Int) {
        repository.setShortBreakDuration(duration)
    }
}