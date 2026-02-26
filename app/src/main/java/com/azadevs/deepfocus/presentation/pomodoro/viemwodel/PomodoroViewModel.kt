package com.azadevs.deepfocus.presentation.pomodoro.viemwodel

import android.content.Context
import androidx.lifecycle.ViewModel
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

    fun onPauseClick() {
        controller.pause()
    }

    fun onResumeClick() {
        controller.resume()
    }

    fun onStopClick() {
        controller.stop()
    }

    fun onStartClick(context: Context) {
        controller.start(context)
    }
}