package com.azadevs.deepfocus.domain.usecase.settings

import com.azadevs.deepfocus.data.datastore.SettingsDataStore

/**
 * Created by : Azamat Kalmurzaev
 * 21/02/2026
 */
class SetFocusDurationUseCase(
    private val settingsDataStore: SettingsDataStore
) {
    suspend operator fun invoke(value: Int) {
        settingsDataStore.setFocusDuration(value)
    }
}