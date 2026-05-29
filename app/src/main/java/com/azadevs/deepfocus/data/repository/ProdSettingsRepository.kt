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

}