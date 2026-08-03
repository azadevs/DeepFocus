package com.azadevs.deepfocus.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.azadevs.deepfocus.data.local.dao.FocusSessionDao
import com.azadevs.deepfocus.data.local.dao.TaskDao
import com.azadevs.deepfocus.data.local.entity.FocusSessionEntity
import com.azadevs.deepfocus.data.local.entity.TaskEntity

/**
 * Created by : Azamat Kalmurzaev
 * 20/02/2026
 */
@Database(
    entities = [FocusSessionEntity::class, TaskEntity::class],
    version = 3,
    exportSchema = false
)
abstract class FocusDatabase : RoomDatabase() {

    abstract fun focusSessionDao(): FocusSessionDao

    abstract fun taskDao(): TaskDao

}