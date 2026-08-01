package com.azadevs.deepfocus.data.repository

import com.azadevs.deepfocus.data.datastore.GamificationDataStore
import com.azadevs.deepfocus.domain.repository.FocusStreakRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class ProdFocusStreakRepository @Inject constructor(
    private val dataStore: GamificationDataStore
) : FocusStreakRepository {

    override fun getStardust(): Flow<Int> = dataStore.stardustFlow

    override fun getCurrentStreak(): Flow<Int> = dataStore.currentStreakFlow

    override fun getBestStreak(): Flow<Int> = dataStore.bestStreakFlow

    override suspend fun addStardust(amount: Int) {
        dataStore.updateStardust(amount)
    }

    override suspend fun updateStreak(lastSessionTime: Long) {
        val lastDateMs = dataStore.lastSessionDateFlow.first()
        val currentStreak = dataStore.currentStreakFlow.first()

        if (lastDateMs == 0L) {
            dataStore.updateStreak(1)
            dataStore.updateLastSessionDate(lastSessionTime)
            return
        }

        val zoneId = ZoneId.systemDefault()
        val lastLocalDate = Instant.ofEpochMilli(lastDateMs).atZone(zoneId).toLocalDate()
        val currentLocalDate = Instant.ofEpochMilli(lastSessionTime).atZone(zoneId).toLocalDate()

        val diffInDays = ChronoUnit.DAYS.between(lastLocalDate, currentLocalDate)

        when {
            diffInDays == 1L -> {
                dataStore.updateStreak(currentStreak + 1)
            }
            diffInDays > 1L -> {
                dataStore.updateStreak(1)
            }
        }

        dataStore.updateLastSessionDate(lastSessionTime)
    }
}
