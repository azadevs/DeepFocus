package com.azadevs.deepfocus.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Created by : Azamat Kalmurzaev
 * 27/02/26
 */
interface SettingsRepository {

    fun getFocusDuration(): Flow<Int>

    suspend fun setFocusDuration(duration: Int)

    fun getShortBreakDuration(): Flow<Int>

    suspend fun setShortBreakDuration(duration: Int)

    fun getLongBreakDuration(): Flow<Int>

    suspend fun setLongBreakDuration(duration: Int)

    fun isOnboardingCompleted(): Flow<Boolean>

    suspend fun setOnboardingCompleted(completed: Boolean)

    fun isSoundEnabled(): Flow<Boolean>

    suspend fun setSoundEnabled(enabled: Boolean)

    fun isVibrationEnabled(): Flow<Boolean>

    suspend fun setVibrationEnabled(enabled: Boolean)

    fun isAutoStartBreaks(): Flow<Boolean>

    suspend fun setAutoStartBreaks(enabled: Boolean)

    fun getThemeMode(): Flow<String>

    suspend fun setThemeMode(mode: String)

    fun getAmbientSoundMode(): Flow<String>

    suspend fun setAmbientSoundMode(mode: String)

}