package com.azadevs.deepfocus.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.gamificationDataStore: DataStore<Preferences> by preferencesDataStore(name = "gamification_prefs")

class GamificationDataStore(private val context: Context) {

    private object PreferencesKeys {
        val STARDUST = intPreferencesKey("stardust")
        val CURRENT_STREAK = intPreferencesKey("current_streak")
        val BEST_STREAK = intPreferencesKey("best_streak")
        val LAST_SESSION_DATE = longPreferencesKey("last_session_date")
        val UNLOCKED_MODULE_IDS = stringSetPreferencesKey("unlocked_module_ids")
    }

    val stardustFlow: Flow<Int> = context.gamificationDataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }.map { preferences ->
            preferences[PreferencesKeys.STARDUST] ?: 50000
        }

    val currentStreakFlow: Flow<Int> = context.gamificationDataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }.map { preferences ->
            preferences[PreferencesKeys.CURRENT_STREAK] ?: 0
        }

    val bestStreakFlow: Flow<Int> = context.gamificationDataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }.map { preferences ->
            preferences[PreferencesKeys.BEST_STREAK] ?: 0
        }

    val lastSessionDateFlow: Flow<Long> = context.gamificationDataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }.map { preferences ->
            preferences[PreferencesKeys.LAST_SESSION_DATE] ?: 0L
        }

    suspend fun updateStardust(amount: Int) {
        context.gamificationDataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.STARDUST] ?: 0
            preferences[PreferencesKeys.STARDUST] = current + amount
        }
    }

    suspend fun updateStreak(streak: Int) {
        context.gamificationDataStore.edit { preferences ->
            preferences[PreferencesKeys.CURRENT_STREAK] = streak
            val best = preferences[PreferencesKeys.BEST_STREAK] ?: 0
            if (streak > best) {
                preferences[PreferencesKeys.BEST_STREAK] = streak
            }
        }
    }

    val unlockedModuleIdsFlow: Flow<Set<Int>> = context.gamificationDataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }.map { preferences ->
            preferences[PreferencesKeys.UNLOCKED_MODULE_IDS]
                ?.mapNotNull { it.toIntOrNull() }
                ?.toSet()
                ?: emptySet()
        }

    suspend fun unlockModule(moduleId: Int) {
        context.gamificationDataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.UNLOCKED_MODULE_IDS] ?: emptySet()
            preferences[PreferencesKeys.UNLOCKED_MODULE_IDS] = current + moduleId.toString()
        }
    }

    suspend fun spendStardust(amount: Int) {
        context.gamificationDataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.STARDUST] ?: 0
            preferences[PreferencesKeys.STARDUST] = (current - amount).coerceAtLeast(0)
        }
    }

    suspend fun updateLastSessionDate(date: Long) {
        context.gamificationDataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_SESSION_DATE] = date
        }
    }
}
