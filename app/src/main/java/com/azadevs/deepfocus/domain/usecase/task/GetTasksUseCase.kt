package com.azadevs.deepfocus.domain.usecase.task

import com.azadevs.deepfocus.domain.model.Task
import com.azadevs.deepfocus.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Created by Azamat Kalmurzaev
 * 02/08/2026
 */
class GetTasksUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    operator fun invoke(): Flow<List<Task>> {
        return repository.getAllTasks()
    }
}
