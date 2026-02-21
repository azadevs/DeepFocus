package com.azadevs.deepfocus.domain.repository

interface TimerRepository {
    suspend fun saveEndTime(endTimeMillis: Long)
    suspend fun clear()
    suspend fun getSavedEndTime(): Long?
    suspend fun isRunning(): Boolean
}