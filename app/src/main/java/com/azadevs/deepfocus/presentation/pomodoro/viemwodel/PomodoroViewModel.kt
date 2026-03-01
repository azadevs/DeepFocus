package com.azadevs.deepfocus.presentation.pomodoro.viemwodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azadevs.deepfocus.domain.model.PomodoroState
import com.azadevs.deepfocus.domain.pomodoro.PomodoroController
import com.azadevs.deepfocus.domain.usecase.DeepFocusUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * Created by : Azamat Kalmurzaev
 * 24/02/2026
 */
@HiltViewModel
class PomodoroViewModel @Inject constructor(
    private val controller: PomodoroController,
    useCases: DeepFocusUseCases
) : ViewModel() {

    val focusDuration: StateFlow<Int> = useCases.getFocusDuration()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 25)

    val shortBreakDuration: StateFlow<Int> = useCases.getShortBreakDuration()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 5)

    val longBreakDuration: StateFlow<Int> = useCases.getLongBreakDuration()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 15)

    val state: StateFlow<PomodoroState> = controller.state

    fun onPauseClick() {
        controller.pause()
    }

    fun onResumeClick() {
        controller.resume()
    }

    fun onStopClick() {
        controller.stop()
    }

    fun onStartClick() {
        controller.start()
    }

    fun onStopAlarmClick() {
        controller.stopAlarm()
    }

    fun onSkipClick() {
        controller.skip()
    }
}