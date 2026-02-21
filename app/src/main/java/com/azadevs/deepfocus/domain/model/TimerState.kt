package com.azadevs.deepfocus.domain.model

/**
 * Created by : Azamat Kalmurzaev
 * 21/02/2026
 */
sealed class TimerState {
    data object Idle : TimerState()

    data class Running(
        val remainingMillis: Long
    ) : TimerState()

    data class Paused(
        val remainingMillis: Long
    ) : TimerState()

    data object Finished : TimerState()
}