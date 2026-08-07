package com.azadevs.deepfocus.data.repository

import com.azadevs.deepfocus.data.datastore.SettingsDataStore
import com.azadevs.deepfocus.domain.repository.SettingsRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * Created by : Azamat Kalmurzaev
 * 27/02/26
 */
class ProdSettingsRepository @Inject constructor(
    private val dataStore: SettingsDataStore
) : SettingsRepository {

    override fun getFocusDuration(): Flow<Int> = dataStore.focusDuration

    override suspend fun setFocusDuration(duration: Int) = dataStore.setFocusDuration(duration)

    override fun getShortBreakDuration(): Flow<Int> = dataStore.shortBreakDuration

    override suspend fun setShortBreakDuration(duration: Int) =
        dataStore.setShortBreakDuration(duration)

    override fun getLongBreakDuration(): Flow<Int> = dataStore.longBreakDuration

    override suspend fun setLongBreakDuration(duration: Int) =
        dataStore.setLongBreakDuration(duration)

    override fun isOnboardingCompleted(): Flow<Boolean> = dataStore.isOnboardingCompleted

    override suspend fun setOnboardingCompleted(completed: Boolean) =
        dataStore.setOnboardingCompleted(completed)

    override fun isSoundEnabled(): Flow<Boolean> = dataStore.isSoundEnabled

    override suspend fun setSoundEnabled(enabled: Boolean) = dataStore.setSoundEnabled(enabled)

    override fun isVibrationEnabled(): Flow<Boolean> = dataStore.isVibrationEnabled

    override suspend fun setVibrationEnabled(enabled: Boolean) = dataStore.setVibrationEnabled(enabled)

    override fun isAutoStartBreaks(): Flow<Boolean> = dataStore.autoStartBreaks

    override suspend fun setAutoStartBreaks(enabled: Boolean) = dataStore.setAutoStartBreaks(enabled)

    override fun getThemeMode(): Flow<String> = dataStore.themeMode

    override suspend fun setThemeMode(mode: String) {
        dataStore.setThemeMode(mode)
    }

    override fun getAmbientSoundMode(): Flow<String> {
        return dataStore.ambientSoundMode
    }

    override suspend fun setAmbientSoundMode(mode: String) {
        dataStore.setAmbientSoundMode(mode)
    }
}