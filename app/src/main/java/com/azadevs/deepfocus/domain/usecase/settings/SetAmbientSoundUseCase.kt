package com.azadevs.deepfocus.domain.usecase.settings

import com.azadevs.deepfocus.domain.repository.SettingsRepository
import javax.inject.Inject

class SetAmbientSoundUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(mode: String) {
        repository.setAmbientSoundMode(mode)
    }
}
