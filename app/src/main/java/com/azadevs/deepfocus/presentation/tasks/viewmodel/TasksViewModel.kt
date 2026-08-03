package com.azadevs.deepfocus.presentation.tasks.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azadevs.deepfocus.domain.model.Task
import com.azadevs.deepfocus.domain.model.TaskCategory
import com.azadevs.deepfocus.domain.usecase.DeepFocusUseCases
import com.azadevs.deepfocus.domain.pomodoro.PomodoroController
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Created by Azamat Kalmurzaev
 * 03/08/2026
 */
@HiltViewModel
class TasksViewModel @Inject constructor(
    private val useCases: DeepFocusUseCases,
    private val pomodoroController: PomodoroController
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow(TaskCategory.ALL)
    val selectedCategory: StateFlow<TaskCategory> = _selectedCategory.asStateFlow()

    val allTasks: StateFlow<List<Task>> = useCases.getTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val filteredTasks: StateFlow<List<Task>> = combine(allTasks, _selectedCategory) { tasks, category ->
        if (category == TaskCategory.ALL) {
            tasks
        } else {
            tasks.filter { it.categoryName.equals(category.name, ignoreCase = true) }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun selectCategory(category: TaskCategory) {
        _selectedCategory.value = category
    }

    fun createTask(title: String, category: TaskCategory, colorHex: String, targetGoalHours: Int) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val task = Task(
                title = title.trim(),
                categoryName = category.name,
                colorHex = colorHex,
                targetGoalMinutes = targetGoalHours * 60
            )
            useCases.upsertTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            useCases.deleteTask(task)
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            useCases.upsertTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun selectTaskForFocus(task: Task) {
        pomodoroController.selectTask(task)
    }
}
