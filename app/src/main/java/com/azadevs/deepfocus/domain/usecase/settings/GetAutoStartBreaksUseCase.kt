package com.azadevs.deepfocus.domain.usecase.settings

import com.azadevs.deepfocus.domain.repository.SettingsRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetAutoStartBreaksUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return repository.isAutoStartBreaks()
    }
}
