package com.azadevs.deepfocus.domain.model

/**
 * Created by : Azamat Kalmurzaev
 * 21/02/2026
 */
sealed class TimerEvent {
    data object Finished : TimerEvent()
}