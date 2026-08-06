package com.azadevs.deepfocus.domain.repository

interface TimerRepository {
    suspend fun saveEndTime(endTimeMillis: Long)
    suspend fun savePhase(phase: String)
    suspend fun saveCycleIndex(cycleIndex: Int)
    suspend fun clear()
    suspend fun getSavedEndTime(): Long?
    suspend fun getSavedPhase(): String?
    suspend fun getSavedCycleIndex(): Int?
    suspend fun isRunning(): Boolean
}