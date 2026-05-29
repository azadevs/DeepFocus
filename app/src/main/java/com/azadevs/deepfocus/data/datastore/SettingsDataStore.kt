package com.azadevs.deepfocus.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

/**
 * Created by : Azamat Kalmurzaev
 * 20/02/2026
 */
private val Context.dataStore by preferencesDataStore(name = "deepfocus_settings")

class SettingsDataStore(
    private val context: Context
) {

    private object Keys {
        val FOCUS_DURATION = intPreferencesKey("focus_duration")
        val SHORT_BREAK = intPreferencesKey("short_break")
        val LONG_BREAK = intPreferencesKey("long_break")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }

    val focusDuration = context.dataStore.data.map { preferences ->
        preferences[Keys.FOCUS_DURATION] ?: 25
    }

    suspend fun setFocusDuration(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[Keys.FOCUS_DURATION] = value
        }
    }

    val shortBreakDuration = context.dataStore.data.map { preferences ->
        preferences[Keys.SHORT_BREAK] ?: 5
    }

    suspend fun setShortBreakDuration(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[Keys.SHORT_BREAK] = value
        }
    }

    val longBreakDuration = context.dataStore.data.map { preferences ->
        preferences[Keys.LONG_BREAK] ?: 15
    }

    suspend fun setLongBreakDuration(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[Keys.LONG_BREAK] = value
        }
    }

    val isOnboardingCompleted = context.dataStore.data.map { preferences ->
        preferences[Keys.ONBOARDING_COMPLETED] ?: false
    }

    suspend fun setOnboardingCompleted(value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[Keys.ONBOARDING_COMPLETED] = value
        }
    }

}