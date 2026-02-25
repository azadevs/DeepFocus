package com.azadevs.deepfocus.presentation.pomodoro.viemwodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azadevs.deepfocus.domain.model.PomodoroPhase
import com.azadevs.deepfocus.domain.model.PomodoroState
import com.azadevs.deepfocus.domain.pomodoro.PomodoroController
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.StateFlow

/**
 * Created by : Azamat Kalmurzaev
 * 24/02/2026
 */
@HiltViewModel
class PomodoroViewModel @Inject constructor(
    private val controller: PomodoroController
) : ViewModel() {

    val state: StateFlow<PomodoroState> = controller.state

    fun onStartClick() {
        controller.start(viewModelScope)
    }

    fun onPauseClick() {
        controller.pause()
    }

    fun onResumeClick() {
        controller.resume(viewModelScope)
    }

    fun onStopClick() {
        controller.stop(viewModelScope)
    }

    fun onPhaseSelected(phase: PomodoroPhase) {
        controller.selectPhase(phase)
    }
}