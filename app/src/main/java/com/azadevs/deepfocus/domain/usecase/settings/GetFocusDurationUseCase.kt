package com.azadevs.deepfocus.domain.usecase.settings

import com.azadevs.deepfocus.data.datastore.SettingsDataStore
import javax.inject.Inject

/**
 * Created by : Azamat Kalmurzaev
 * 21/02/2026
 */
class GetFocusDurationUseCase @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) {
    operator fun invoke() = settingsDataStore.focusDuration
}