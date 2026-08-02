package com.azadevs.deepfocus.domain.repository

import com.azadevs.deepfocus.domain.model.Task
import kotlinx.coroutines.flow.Flow

/**
 * Created by Azamat Kalmurzaev
 * 02/08/2026
 */
interface TaskRepository {
    fun getAllTasks(): Flow<List<Task>>
    suspend fun upsertTask(task: Task): Long
    suspend fun deleteTask(task: Task)
    suspend fun incrementTaskMinutes(taskId: Long, minutes: Int)
}
