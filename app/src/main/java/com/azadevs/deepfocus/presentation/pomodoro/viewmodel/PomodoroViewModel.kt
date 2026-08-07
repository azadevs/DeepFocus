package com.azadevs.deepfocus.presentation.pomodoro.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azadevs.deepfocus.domain.model.PomodoroState
import com.azadevs.deepfocus.domain.model.Task
import com.azadevs.deepfocus.domain.pomodoro.PomodoroController
import com.azadevs.deepfocus.domain.usecase.DeepFocusUseCases
import com.azadevs.deepfocus.domain.model.AmbientSoundMode
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Created by : Azamat Kalmurzaev
 * 24/02/2026
 */
@HiltViewModel
class PomodoroViewModel @Inject constructor(
    private val controller: PomodoroController,
    private val useCases: DeepFocusUseCases
) : ViewModel() {

    val focusDuration: StateFlow<Int> = useCases.getFocusDuration()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 25)

    val shortBreakDuration: StateFlow<Int> = useCases.getShortBreakDuration()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 5)

    val longBreakDuration: StateFlow<Int> = useCases.getLongBreakDuration()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 15)
        
    val ambientSoundMode: StateFlow<AmbientSoundMode> = useCases.getAmbientSoundMode()
        .map { AmbientSoundMode.fromString(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AmbientSoundMode.NONE)

    val state: StateFlow<PomodoroState> = controller.state

    val tasks: StateFlow<List<Task>> = useCases.getTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedTask: StateFlow<Task?> = controller.selectedTask

    fun selectTask(task: Task?) {
        controller.selectTask(task)
    }

    fun setAmbientSoundMode(mode: AmbientSoundMode) {
        viewModelScope.launch {
            useCases.setAmbientSoundMode(mode.name)
        }
    }

    fun addNewTask(title: String, colorHex: String = "#FF5252") {
        if (title.isBlank()) return
        viewModelScope.launch {
            val newTask = Task(title = title.trim(), colorHex = colorHex)
            val newId = useCases.upsertTask(newTask)
            selectTask(newTask.copy(id = newId))
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            if (selectedTask.value?.id == task.id) {
                selectTask(null)
            }
            useCases.deleteTask(task)
        }
    }

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
