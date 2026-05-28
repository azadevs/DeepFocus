package com.azadevs.deepfocus.domain.repository

import kotlinx.coroutines.flow.Flow

interface FocusStreakRepository {
    fun getStardust(): Flow<Int>
    fun getCurrentStreak(): Flow<Int>
    fun getBestStreak(): Flow<Int>
    suspend fun addStardust(amount: Int)
    suspend fun updateStreak(lastSessionTime: Long)
}
