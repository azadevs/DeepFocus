package com.azadevs.deepfocus.presentation.pomodoro

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.azadevs.deepfocus.domain.model.PomodoroPhase
import com.azadevs.deepfocus.presentation.pomodoro.component.InfoPill
import com.azadevs.deepfocus.presentation.pomodoro.component.PhaseChip
import com.azadevs.deepfocus.presentation.pomodoro.viemwodel.PomodoroViewModel
import kotlin.math.max
import kotlin.math.min
import com.azadevs.deepfocus.presentation.util.DeepFocusUtils.formatTime

/**
 * Created by : Azamat Kalmurzaev
 * 24/02/2026
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroScreen(
    viewModel: PomodoroViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    val total = max(1L, state.phaseDurationMillis)
    val remaining = min(total, max(0L, state.remainingMillis))
    val rawProgress = 1f - (remaining.toFloat() / total.toFloat())
    val progress by animateFloatAsState(
        targetValue = rawProgress.coerceIn(0f, 1f),
        label = "progress"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DeepFocus") },
                actions = {
                    AssistChip(
                        onClick = {},
                        label = { Text("Cycle ${state.cycleIndex}") }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            PhaseChip(phase = state.phase)

            Spacer(modifier = Modifier.height(18.dp))

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box(contentAlignment = Alignment.Center) {

                        CircularProgressIndicator(
                            progress = { progress },
                            strokeWidth = 10.dp,
                            modifier = Modifier.size(220.dp)
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {

                            AnimatedContent(
                                targetState = formatTime(state.remainingMillis),
                                label = "time"
                            ) { formatted ->
                                Text(
                                    text = formatted,
                                    style = MaterialTheme.typography.displayMedium,
                                    textAlign = TextAlign.Center
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = phaseSubtitle(state.phase),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        val canStart = !state.isRunning && state.remainingMillis == 0L
                        val showPause = state.isRunning
                        val showResume = !state.isRunning && state.remainingMillis > 0L

                        when {
                            canStart -> {
                                Button(
                                    modifier = Modifier.weight(1f),
                                    onClick = { viewModel.onStartClick() }
                                ) {
                                    Icon(Icons.Outlined.PlayArrow, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Start")
                                }
                            }

                            showPause -> {
                                Button(
                                    modifier = Modifier.weight(1f),
                                    onClick = { viewModel.onPauseClick() }
                                ) {
                                    Icon(Icons.Outlined.Pause, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Pause")
                                }
                            }

                            showResume -> {
                                Button(
                                    modifier = Modifier.weight(1f),
                                    onClick = { viewModel.onResumeClick() }
                                ) {
                                    Icon(Icons.Outlined.PlayArrow, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Resume")
                                }
                            }
                        }

                        OutlinedButton(
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.onStopClick() }
                        ) {
                            Icon(Icons.Outlined.Stop, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Stop")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    InfoPill(title = "Focus", value = "25m")
                    InfoPill(title = "Short", value = "5m")
                    InfoPill(title = "Long", value = "15m")
                }
            }
        }
    }
}
private fun phaseSubtitle(phase: PomodoroPhase): String =
    when (phase) {
        PomodoroPhase.FOCUS -> "Stay focused"
        PomodoroPhase.SHORT_BREAK -> "Take a short break"
        PomodoroPhase.LONG_BREAK -> "Take a long break"
    }

