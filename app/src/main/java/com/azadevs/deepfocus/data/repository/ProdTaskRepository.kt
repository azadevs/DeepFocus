package com.azadevs.deepfocus.data.repository

import com.azadevs.deepfocus.data.local.dao.TaskDao
import com.azadevs.deepfocus.data.mapper.toDomain
import com.azadevs.deepfocus.data.mapper.toEntity
import com.azadevs.deepfocus.domain.model.Task
import com.azadevs.deepfocus.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Created by Azamat Kalmurzaev
 * 02/08/2026
 */
class ProdTaskRepository @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {

    override fun getAllTasks(): Flow<List<Task>> =
        taskDao.getAllTasks().map { entities -> entities.map { it.toDomain() } }

    override suspend fun upsertTask(task: Task): Long =
        taskDao.upsert(task.toEntity())

    override suspend fun deleteTask(task: Task) =
        taskDao.delete(task.toEntity())

    override suspend fun incrementTaskMinutes(taskId: Long, minutes: Int) =
        taskDao.incrementTaskMinutes(taskId, minutes)
}
