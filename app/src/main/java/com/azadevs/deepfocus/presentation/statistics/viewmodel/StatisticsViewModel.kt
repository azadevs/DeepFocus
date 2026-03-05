package com.azadevs.deepfocus.presentation.statistics.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azadevs.deepfocus.domain.model.FocusSession
import com.azadevs.deepfocus.domain.model.UserRank
import com.azadevs.deepfocus.domain.usecase.DeepFocusUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

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

    val stardust: StateFlow<Int> = useCases.getStardust()
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

    val userRank: StateFlow<UserRank> = totalFocusMinutes.map { minutes ->
        UserRank.fromMinutes(minutes)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserRank.STARGAZER
    )

    val allSessions: StateFlow<List<FocusSession>> = useCases.getAllSessions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val weeklyStats: StateFlow<List<DailyStat>> = useCases.getAllSessions().map { sessions ->
        calculateWeeklyStats(sessions)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private fun calculateWeeklyStats(sessions: List<FocusSession>): List<DailyStat> {
        val stats = mutableListOf<DailyStat>()
        for (i in 6 downTo 0) {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -i)

            val startOfDay = getStartOfDay(cal.timeInMillis)
            val endOfDay = startOfDay + 24 * 60 * 60 * 1000L - 1

            val minutes = sessions.filter {
                it.startTime in startOfDay..endOfDay && it.type.name == "FOCUS"
            }.sumOf { it.durationMinutes }

            val dayName = getDayName(cal.get(Calendar.DAY_OF_WEEK))
            stats.add(DailyStat(dayName, minutes))
        }
        return stats
    }

    private fun getStartOfDay(timeMs: Long): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timeMs
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
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
