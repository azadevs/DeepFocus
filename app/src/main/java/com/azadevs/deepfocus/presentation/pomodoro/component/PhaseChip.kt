package com.azadevs.deepfocus.presentation.pomodoro.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.azadevs.deepfocus.domain.model.PomodoroPhase

/**
 * Created by : Azamat Kalmurzaev
 * 24/02/2026
 */
@Composable
fun PhaseChip(phase: PomodoroPhase, color: androidx.compose.ui.graphics.Color) {
    val label = when (phase) {
        PomodoroPhase.FOCUS -> "🎯 Focus"
        PomodoroPhase.SHORT_BREAK -> "☕ Short Break"
        PomodoroPhase.LONG_BREAK -> "🌿 Long Break"
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.22f),
        contentColor = color,
        border = BorderStroke(1.dp, color.copy(alpha = 0.45f))
    ) {
        Text(
            text = label.uppercase(),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold
        )
    }
}
