package com.azadevs.deepfocus.presentation.statistics.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azadevs.deepfocus.domain.model.FocusSession
import com.azadevs.deepfocus.domain.model.SessionType
import com.azadevs.deepfocus.domain.usecase.DeepFocusUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Created by : Azamat Kalmurzaev
 * 27/02/26
 */
@HiltViewModel
class StatisticsViewModel @Inject constructor(
    useCases: DeepFocusUseCases
) : ViewModel() {

    val totalFocusMinutes: StateFlow<Int> = useCases.getTotalFocusMinutes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val currentStreak: StateFlow<Int> = useCases.getCurrentStreak()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val bestStreak: StateFlow<Int> = useCases.getBestStreak()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    private val heatmapStartMillis: Long = calculateHeatmapStartMillis()

    // Bounded query: loads only sessions within the 53-week heatmap window from Room DB
    // preventing massive unbounded memory allocation and DB reads for old historical records.
    private val sessionsFlow = useCases.getSessionsBetween(heatmapStartMillis, Long.MAX_VALUE)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allSessions: StateFlow<List<FocusSession>> = sessionsFlow

    val weeklyStats: StateFlow<List<DailyStat>> = sessionsFlow
        .map { sessions -> calculateWeeklyStats(sessions) }
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val heatmapStats: StateFlow<List<HeatmapDay>> = sessionsFlow
        .map { sessions -> calculateHeatmapStats(sessions, heatmapStartMillis) }
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private fun calculateHeatmapStats(
        sessions: List<FocusSession>,
        startMillis: Long
    ): List<HeatmapDay> {
        val daysList = ArrayList<HeatmapDay>(53 * 7)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        // Single O(N) pass aggregation by formatted date
        val minutesByDate = sessions
            .filter { it.type == SessionType.FOCUS }
            .groupBy { sdf.format(Date(it.startTime)) }
            .mapValues { (_, list) -> list.sumOf { it.durationMinutes } }

        val calendar = Calendar.getInstance().apply {
            timeInMillis = startMillis
        }

        repeat(53 * 7) {
            val dateStr = sdf.format(calendar.time)
            val minutes = minutesByDate[dateStr] ?: 0

            val level = when {
                minutes == 0 -> 0
                minutes < 25 -> 1
                minutes < 50 -> 2
                minutes < 100 -> 3
                else -> 4
            }

            daysList.add(HeatmapDay(date = calendar.timeInMillis, minutes = minutes, level = level))
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return daysList
    }

    private fun calculateWeeklyStats(sessions: List<FocusSession>): List<DailyStat> {
        val stats = ArrayList<DailyStat>(7)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        // Single O(N) pass aggregation
        val minutesByDate = sessions
            .filter { it.type == SessionType.FOCUS }
            .groupBy { sdf.format(Date(it.startTime)) }
            .mapValues { (_, list) -> list.sumOf { it.durationMinutes } }

        val calendar = Calendar.getInstance()

        for (i in 6 downTo 0) {
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.add(Calendar.DAY_OF_YEAR, -i)

            val dateStr = sdf.format(calendar.time)
            val minutes = minutesByDate[dateStr] ?: 0

            stats.add(DailyStat(getDayName(calendar.get(Calendar.DAY_OF_WEEK)), minutes))
        }
        return stats
    }

    private fun calculateHeatmapStartMillis(): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.WEEK_OF_YEAR, -52)
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getDayName(dayOfWeek: Int): String {
        return when (dayOfWeek) {
            Calendar.MONDAY -> "Mo"
            Calendar.TUESDAY -> "Tu"
            Calendar.WEDNESDAY -> "We"
            Calendar.THURSDAY -> "Thu"
            Calendar.FRIDAY -> "Fr"
            Calendar.SATURDAY -> "Sat"
            Calendar.SUNDAY -> "Sun"
            else -> ""
        }
    }
}

data class DailyStat(val dayName: String, val minutes: Int)
