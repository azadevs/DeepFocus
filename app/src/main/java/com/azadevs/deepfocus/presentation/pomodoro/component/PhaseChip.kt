package com.azadevs.deepfocus.presentation.pomodoro.component

import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.azadevs.deepfocus.domain.model.PomodoroPhase

/**
 * Created by : Azamat Kalmurzaev
 * 24/02/2026
 */
@Composable
fun PhaseChip(phase: PomodoroPhase) {
    val label = when (phase) {
        PomodoroPhase.FOCUS -> "Focus"
        PomodoroPhase.SHORT_BREAK -> "Short Break"
        PomodoroPhase.LONG_BREAK -> "Long Break"
    }

    AssistChip(
        onClick = {},
        label = { Text(label) }
    )
}
