package com.azadevs.deepfocus.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.azadevs.deepfocus.data.local.entity.FocusSessionEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by : Azamat Kalmurzaev
 * 20/02/2026
 */
@Dao
interface FocusSessionDao {

    @Upsert
    suspend fun upsert(focusSessionEntity: FocusSessionEntity)

    @Query("SELECT * FROM focus_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<FocusSessionEntity>>

    @Query("SELECT SUM(durationMinutes) FROM focus_sessions WHERE typeSession = 'FOCUS'")
    fun getTotalFocusDuration(): Flow<Int?>

    @Query(
        """
        SELECT * FROM focus_sessions 
        WHERE startTime BETWEEN :start AND :end
        ORDER BY startTime ASC
    """
    )
    fun getSessionsBetween(start: Long, end: Long): Flow<List<FocusSessionEntity>>

    @Query("DELETE FROM focus_sessions")
    suspend fun clearAll()

}