package com.azadevs.deepfocus.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.azadevs.deepfocus.domain.repository.TimerRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

private val Context.timerDataStore by preferencesDataStore("timer_store")

@Singleton
class TimerPersistenceDataStore @Inject constructor(
    private val context: Context
) : TimerRepository {

    private object Keys {
        val END_TIME = longPreferencesKey("end_time")
        val IS_RUNNING = booleanPreferencesKey("is_running")
        val PHASE = stringPreferencesKey("phase")
        val CYCLE_INDEX = intPreferencesKey("cycle_index")
        val PAUSED_TIME = longPreferencesKey("paused_time")
    }

    override suspend fun saveEndTime(endTimeMillis: Long) {
        context.timerDataStore.edit {
            it[Keys.END_TIME] = endTimeMillis
            it[Keys.IS_RUNNING] = true
            it.remove(Keys.PAUSED_TIME)
        }
    }

    override suspend fun savePhase(phase: String) {
        context.timerDataStore.edit {
            it[Keys.PHASE] = phase
        }
    }

    override suspend fun saveCycleIndex(cycleIndex: Int) {
        context.timerDataStore.edit {
            it[Keys.CYCLE_INDEX] = cycleIndex
        }
    }

    override suspend fun clear() {
        context.timerDataStore.edit {
            it.remove(Keys.END_TIME)
            it.remove(Keys.IS_RUNNING)
            it.remove(Keys.PHASE)
            it.remove(Keys.CYCLE_INDEX)
            it.remove(Keys.PAUSED_TIME)
        }
    }

    override suspend fun getSavedEndTime(): Long? {
        val prefs = context.timerDataStore.data.first()
        return prefs[Keys.END_TIME]
    }

    override suspend fun getSavedPhase(): String? {
        val prefs = context.timerDataStore.data.first()
        return prefs[Keys.PHASE]
    }

    override suspend fun getSavedCycleIndex(): Int? {
        val prefs = context.timerDataStore.data.first()
        return prefs[Keys.CYCLE_INDEX]
    }

    override suspend fun isRunning(): Boolean {
        val prefs = context.timerDataStore.data.first()
        return prefs[Keys.IS_RUNNING] ?: false
    }

    override suspend fun hasSavedState(): Boolean {
        val prefs = context.timerDataStore.data.first()
        return prefs.contains(Keys.END_TIME) || prefs.contains(Keys.PAUSED_TIME)
    }

    override suspend fun savePausedTime(remainingMillis: Long) {
        context.timerDataStore.edit {
            it[Keys.PAUSED_TIME] = remainingMillis
            it[Keys.IS_RUNNING] = false
            it.remove(Keys.END_TIME)
        }
    }

    override suspend fun getPausedTime(): Long? {
        val prefs = context.timerDataStore.data.first()
        return prefs[Keys.PAUSED_TIME]
    }
}