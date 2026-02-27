package com.azadevs.deepfocus.presentation.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azadevs.deepfocus.domain.usecase.DeepFocusUseCases
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

    init {
        viewModelScope.launch {
            useCases.getFocusDuration().collect { duration ->
                duration.let { _focusMinutes.value = it }
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
}