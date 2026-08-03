package com.azadevs.deepfocus.presentation.statistics.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azadevs.deepfocus.domain.model.FocusSession
import com.azadevs.deepfocus.domain.model.SessionType
import com.azadevs.deepfocus.domain.model.Task
import com.azadevs.deepfocus.domain.usecase.DeepFocusUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

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

    val tasks: StateFlow<List<Task>> = useCases.getTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val zoneId: ZoneId = ZoneId.systemDefault()
    private val heatmapStartDate: LocalDate = calculateHeatmapStartDate(zoneId)
    private val heatmapStartMillis: Long = heatmapStartDate.atStartOfDay(zoneId).toInstant().toEpochMilli()

    // Bounded query: loads only sessions within the 53-week heatmap window from Room DB
    // preventing massive unbounded memory allocation and DB reads for old historical records.
    private val sessionsFlow = useCases.getSessionsBetween(heatmapStartMillis, Long.MAX_VALUE)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allSessions: StateFlow<List<FocusSession>> = sessionsFlow

    private val _timelineFilter = MutableStateFlow(TaskTimelineFilter.TODAY)
    val timelineFilter: StateFlow<TaskTimelineFilter> = _timelineFilter.asStateFlow()

    fun setTimelineFilter(filter: TaskTimelineFilter) {
        _timelineFilter.value = filter
    }

    val taskTimelineStats: StateFlow<List<TaskTimelineItem>> = combine(sessionsFlow, tasks, _timelineFilter) { sessions, taskList, filter ->
        calculateTaskTimelineStats(sessions, taskList, filter, zoneId)
    }
    .flowOn(Dispatchers.Default)
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val weeklyStats: StateFlow<List<DailyStat>> = sessionsFlow
        .map { sessions -> calculateWeeklyStats(sessions, zoneId) }
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val heatmapStats: StateFlow<List<HeatmapDay>> = sessionsFlow
        .map { sessions -> calculateHeatmapStats(sessions, heatmapStartDate, zoneId) }
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private fun calculateTaskTimelineStats(
        sessions: List<FocusSession>,
        tasks: List<Task>,
        filter: TaskTimelineFilter,
        zone: ZoneId
    ): List<TaskTimelineItem> {
        val today = LocalDate.now(zone)
        val yesterday = today.minusDays(1)
        val mondayThisWeek = today.minusDays(today.dayOfWeek.value.toLong() - 1)

        val taskTitleToHex = tasks.associate { it.title to it.colorHex }

        val filteredSessions = sessions.filter { session ->
            if (session.type != SessionType.FOCUS) return@filter false
            val sessionDate = Instant.ofEpochMilli(session.startTime).atZone(zone).toLocalDate()
            when (filter) {
                TaskTimelineFilter.TODAY -> sessionDate == today
                TaskTimelineFilter.YESTERDAY -> sessionDate == yesterday
                TaskTimelineFilter.THIS_WEEK -> !sessionDate.isBefore(mondayThisWeek)
                TaskTimelineFilter.ALL_TIME -> true
            }
        }

        val grouped = filteredSessions.groupBy { session ->
            session.taskTitle ?: "General Focus"
        }

        return grouped.map { (title, sessionList) ->
            val totalMinutes = sessionList.sumOf { it.durationMinutes }
            val count = sessionList.size
            val colorHex = taskTitleToHex[title] ?: "#FF5252"
            TaskTimelineItem(
                taskTitle = title,
                colorHex = colorHex,
                durationMinutes = totalMinutes,
                sessionCount = count
            )
        }.sortedByDescending { it.durationMinutes }
    }

    private fun calculateHeatmapStats(
        sessions: List<FocusSession>,
        startDate: LocalDate,
        zone: ZoneId
    ): List<HeatmapDay> {
        val daysList = ArrayList<HeatmapDay>(53 * 7)

        val minutesByDate: Map<LocalDate, Int> = sessions
            .filter { it.type == SessionType.FOCUS }
            .groupBy { Instant.ofEpochMilli(it.startTime).atZone(zone).toLocalDate() }
            .mapValues { (_, list) -> list.sumOf { it.durationMinutes } }

        var currentDate = startDate
        repeat(53 * 7) {
            val minutes = minutesByDate[currentDate] ?: 0

            val level = when {
                minutes == 0 -> 0
                minutes < 25 -> 1
                minutes < 50 -> 2
                minutes < 100 -> 3
                else -> 4
            }

            val epochMillis = currentDate.atStartOfDay(zone).toInstant().toEpochMilli()
            daysList.add(HeatmapDay(date = epochMillis, minutes = minutes, level = level))
            currentDate = currentDate.plusDays(1)
        }

        return daysList
    }

    private fun calculateWeeklyStats(sessions: List<FocusSession>, zone: ZoneId): List<DailyStat> {
        val stats = ArrayList<DailyStat>(7)

        val minutesByDate: Map<LocalDate, Int> = sessions
            .filter { it.type == SessionType.FOCUS }
            .groupBy { Instant.ofEpochMilli(it.startTime).atZone(zone).toLocalDate() }
            .mapValues { (_, list) -> list.sumOf { it.durationMinutes } }

        val today = LocalDate.now(zone)

        for (i in 6 downTo 0) {
            val date = today.minusDays(i.toLong())
            val minutes = minutesByDate[date] ?: 0
            stats.add(DailyStat(getDayName(date.dayOfWeek), minutes))
        }
        return stats
    }

    private fun calculateHeatmapStartDate(zone: ZoneId): LocalDate {
        var date = LocalDate.now(zone).minusWeeks(52)
        while (date.dayOfWeek != DayOfWeek.MONDAY) {
            date = date.minusDays(1)
        }
        return date
    }

    private fun getDayName(dayOfWeek: DayOfWeek): String {
        return when (dayOfWeek) {
            DayOfWeek.MONDAY -> "Mo"
            DayOfWeek.TUESDAY -> "Tu"
            DayOfWeek.WEDNESDAY -> "We"
            DayOfWeek.THURSDAY -> "Thu"
            DayOfWeek.FRIDAY -> "Fr"
            DayOfWeek.SATURDAY -> "Sat"
            DayOfWeek.SUNDAY -> "Sun"
        }
    }
}

enum class TaskTimelineFilter {
    TODAY,
    YESTERDAY,
    THIS_WEEK,
    ALL_TIME
}

data class TaskTimelineItem(
    val taskTitle: String,
    val colorHex: String,
    val durationMinutes: Int,
    val sessionCount: Int
)

data class DailyStat(val dayName: String, val minutes: Int)
