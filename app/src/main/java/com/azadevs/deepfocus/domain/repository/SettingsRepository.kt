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

}