package com.azadevs.deepfocus.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.azadevs.deepfocus.data.local.dao.FocusSessionDao
import com.azadevs.deepfocus.data.local.entity.FocusSessionEntity

/**
 * Created by : Azamat Kalmurzaev
 * 20/02/2026
 */
@Database(
    entities = [FocusSessionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FocusDatabase : RoomDatabase() {

    abstract fun focusSessionDao(): FocusSessionDao

}