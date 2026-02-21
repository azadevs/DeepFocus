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
    }

    override suspend fun saveEndTime(endTimeMillis: Long) {
        context.timerDataStore.edit {
            it[Keys.END_TIME] = endTimeMillis
            it[Keys.IS_RUNNING] = true
        }
    }

    override suspend fun clear() {
        context.timerDataStore.edit {
            it.remove(Keys.END_TIME)
            it.remove(Keys.IS_RUNNING)
        }
    }

    override suspend fun getSavedEndTime(): Long? {
        val prefs = context.timerDataStore.data.first()
        return prefs[Keys.END_TIME]
    }

    override suspend fun isRunning(): Boolean {
        val prefs = context.timerDataStore.data.first()
        return prefs[Keys.IS_RUNNING] ?: false
    }
}