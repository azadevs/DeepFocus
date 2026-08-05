package com.azadevs.deepfocus.presentation.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azadevs.deepfocus.domain.usecase.DeepFocusUseCases
import com.azadevs.deepfocus.presentation.settings.component.AppThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by : Azamat Kalmurzaev
 * 27/02/26
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val useCases: DeepFocusUseCases
) : ViewModel() {

    private val _focusMinutes = MutableStateFlow(25)
    val focusMinutes: StateFlow<Int> = _focusMinutes.asStateFlow()

    private val _shortBreakMinutes = MutableStateFlow(5)
    val shortBreakMinutes: StateFlow<Int> = _shortBreakMinutes.asStateFlow()

    private val _longBreakMinutes = MutableStateFlow(15)
    val longBreakMinutes: StateFlow<Int> = _longBreakMinutes.asStateFlow()

    private val _soundEnabled = MutableStateFlow(true)
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    private val _vibrationEnabled = MutableStateFlow(true)
    val vibrationEnabled: StateFlow<Boolean> = _vibrationEnabled.asStateFlow()

    private val _autoStartBreaks = MutableStateFlow(false)
    val autoStartBreaks: StateFlow<Boolean> = _autoStartBreaks.asStateFlow()

    private val _themeMode = MutableStateFlow(AppThemeMode.SYSTEM)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    init {
        viewModelScope.launch {
            useCases.getFocusDuration().collect { duration ->
                _focusMinutes.value = duration
            }
        }
        viewModelScope.launch {
            useCases.getShortBreakDuration().collect { duration ->
                _shortBreakMinutes.value = duration
            }
        }
        viewModelScope.launch {
            useCases.getLongBreakDuration().collect { duration ->
                _longBreakMinutes.value = duration
            }
        }
        viewModelScope.launch {
            useCases.getSoundEnabled().collect { enabled ->
                _soundEnabled.value = enabled
            }
        }
        viewModelScope.launch {
            useCases.getVibrationEnabled().collect { enabled ->
                _vibrationEnabled.value = enabled
            }
        }
        viewModelScope.launch {
            useCases.getAutoStartBreaks().collect { enabled ->
                _autoStartBreaks.value = enabled
            }
        }
        viewModelScope.launch {
            useCases.getThemeMode().collect { modeStr ->
                _themeMode.value = try {
                    AppThemeMode.valueOf(modeStr)
                } catch (e: Exception) {
                    AppThemeMode.SYSTEM
                }
            }
        }
    }

    fun updateFocusDuration(minutes: Int) {
        _focusMinutes.value = minutes
        viewModelScope.launch {
            useCases.setFocusDuration(minutes)
        }
    }

    fun updateShortBreakDuration(minutes: Int) {
        _shortBreakMinutes.value = minutes
        viewModelScope.launch {
            useCases.setShortBreakDuration(minutes)
        }
    }

    fun updateLongBreakDuration(minutes: Int) {
        _longBreakMinutes.value = minutes
        viewModelScope.launch {
            useCases.setLongBreakDuration(minutes)
        }
    }

    fun toggleSound(enabled: Boolean) {
        _soundEnabled.value = enabled
        viewModelScope.launch {
            useCases.setSoundEnabled(enabled)
        }
    }

    fun toggleVibration(enabled: Boolean) {
        _vibrationEnabled.value = enabled
        viewModelScope.launch {
            useCases.setVibrationEnabled(enabled)
        }
    }

    fun toggleAutoStartBreaks(enabled: Boolean) {
        _autoStartBreaks.value = enabled
        viewModelScope.launch {
            useCases.setAutoStartBreaks(enabled)
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
        viewModelScope.launch {
            useCases.setThemeMode(mode.name)
        }
    }

    fun resetToDefaults() {
        updateFocusDuration(25)
        updateShortBreakDuration(5)
        updateLongBreakDuration(15)
        toggleSound(true)
        toggleVibration(true)
        toggleAutoStartBreaks(false)
        setThemeMode(AppThemeMode.SYSTEM)
    }
}