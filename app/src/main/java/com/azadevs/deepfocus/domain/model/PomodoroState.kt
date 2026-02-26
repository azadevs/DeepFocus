package com.azadevs.deepfocus.domain.model

/**
 * Created by : Azamat Kalmurzaev
 * 21/02/2026
 */
data class PomodoroState(
    val phase: PomodoroPhase = PomodoroPhase.FOCUS,
    val cycleIndex: Int = 1,
    val remainingMillis: Long = 0L,
    val phaseDurationMillis: Long = 0L,
    val isRunning: Boolean = false,
    val isRinging: Boolean = false
)