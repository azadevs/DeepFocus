package com.azadevs.deepfocus.domain.model

/**
 * Created by : Azamat Kalmurzaev
 * 21/02/2026
 */
data class PomodoroConfig(
    val focusMinutes: Int = 25,
    val shortBreakMinutes: Int = 5,
    val longBreakMinutes: Int = 15,
    val cyclesBeforeLongBreak: Int = 4
)