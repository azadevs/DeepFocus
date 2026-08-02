package com.azadevs.deepfocus.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.azadevs.deepfocus.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by Azamat Kalmurzaev
 * 02/08/2026
 */
@Dao
interface TaskDao {

    @Upsert
    suspend fun upsert(taskEntity: TaskEntity): Long

    @Delete
    suspend fun delete(taskEntity: TaskEntity)

    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("UPDATE tasks SET totalFocusMinutes = totalFocusMinutes + :minutes WHERE id = :taskId")
    suspend fun incrementTaskMinutes(taskId: Long, minutes: Int)

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Long): TaskEntity?
}
