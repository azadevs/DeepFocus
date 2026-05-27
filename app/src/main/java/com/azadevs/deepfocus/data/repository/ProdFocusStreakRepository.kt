package com.azadevs.deepfocus.data.repository

import com.azadevs.deepfocus.data.datastore.GamificationDataStore
import com.azadevs.deepfocus.domain.repository.FocusStreakRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.Calendar
import java.util.concurrent.TimeUnit

class ProdFocusStreakRepository @Inject constructor(
    private val dataStore: GamificationDataStore
) : FocusStreakRepository {

    override fun getStardust(): Flow<Int> = dataStore.stardustFlow

    override fun getCurrentStreak(): Flow<Int> = dataStore.currentStreakFlow

    override fun getBestStreak(): Flow<Int> = dataStore.bestStreakFlow

    override suspend fun addStardust(amount: Int) {
        dataStore.updateStardust(amount)
    }

    override suspend fun spendStardust(amount: Int) {
        dataStore.spendStardust(amount)
    }

    override suspend fun updateStreak(lastSessionTime: Long) {
        val lastDate = dataStore.lastSessionDateFlow.first()
        val currentStreak = dataStore.currentStreakFlow.first()

        val lastCalendar = Calendar.getInstance().apply { timeInMillis = lastDate }
        val currentCalendar = Calendar.getInstance().apply { timeInMillis = lastSessionTime }

        lastCalendar.set(Calendar.HOUR_OF_DAY, 0)
        lastCalendar.set(Calendar.MINUTE, 0)
        lastCalendar.set(Calendar.SECOND, 0)
        lastCalendar.set(Calendar.MILLISECOND, 0)

        currentCalendar.set(Calendar.HOUR_OF_DAY, 0)
        currentCalendar.set(Calendar.MINUTE, 0)
        currentCalendar.set(Calendar.SECOND, 0)
        currentCalendar.set(Calendar.MILLISECOND, 0)

        val diffInMs = currentCalendar.timeInMillis - lastCalendar.timeInMillis
        val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMs)

        when {
            diffInDays == 1L -> {
                dataStore.updateStreak(currentStreak + 1)
            }

            diffInDays > 1L -> {
                dataStore.updateStreak(1)
            }

            lastDate == 0L -> {
                dataStore.updateStreak(1)
            }
        }

        dataStore.updateLastSessionDate(lastSessionTime)
    }
}
