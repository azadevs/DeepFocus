package com.azadevs.deepfocus.domain.usecase.task

import com.azadevs.deepfocus.domain.model.Task
import com.azadevs.deepfocus.domain.repository.TaskRepository
import javax.inject.Inject

/**
 * Created by Azamat Kalmurzaev
 * 02/08/2026
 */
class UpsertTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: Task): Long {
        return repository.upsertTask(task)
    }
}
