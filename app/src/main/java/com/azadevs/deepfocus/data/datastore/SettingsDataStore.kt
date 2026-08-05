package com.azadevs.deepfocus.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
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
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val AUTO_START_BREAKS = booleanPreferencesKey("auto_start_breaks")
        val THEME_MODE = stringPreferencesKey("theme_mode")
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

    val isSoundEnabled = context.dataStore.data.map { preferences ->
        preferences[Keys.SOUND_ENABLED] ?: true
    }

    suspend fun setSoundEnabled(value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[Keys.SOUND_ENABLED] = value
        }
    }

    val isVibrationEnabled = context.dataStore.data.map { preferences ->
        preferences[Keys.VIBRATION_ENABLED] ?: true
    }

    suspend fun setVibrationEnabled(value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[Keys.VIBRATION_ENABLED] = value
        }
    }

    val autoStartBreaks = context.dataStore.data.map { preferences ->
        preferences[Keys.AUTO_START_BREAKS] ?: false
    }

    suspend fun setAutoStartBreaks(value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[Keys.AUTO_START_BREAKS] = value
        }
    }

    val themeMode = context.dataStore.data.map { preferences ->
        preferences[Keys.THEME_MODE] ?: "SYSTEM"
    }

    suspend fun setThemeMode(value: String) {
        context.dataStore.edit { preferences ->
            preferences[Keys.THEME_MODE] = value
        }
    }

}