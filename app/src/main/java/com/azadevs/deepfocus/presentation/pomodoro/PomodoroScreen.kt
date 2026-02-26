package com.azadevs.deepfocus.presentation.pomodoro

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Stop
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.azadevs.deepfocus.R
import com.azadevs.deepfocus.domain.model.PomodoroPhase
import com.azadevs.deepfocus.presentation.pomodoro.component.InfoPill
import com.azadevs.deepfocus.presentation.pomodoro.component.PhaseChip
import com.azadevs.deepfocus.presentation.pomodoro.viemwodel.PomodoroViewModel
import com.azadevs.deepfocus.presentation.util.DeepFocusUtils.formatTime
import kotlin.math.max
import kotlin.math.min

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
    val scale by animateFloatAsState(
        targetValue = if (state.isRunning) 1f else 0.95f,
        animationSpec = spring(),
        label = "scale"
    )
    val phaseColor = when (state.phase) {
        PomodoroPhase.FOCUS -> MaterialTheme.colorScheme.primary
        PomodoroPhase.SHORT_BREAK -> MaterialTheme.colorScheme.secondary
        PomodoroPhase.LONG_BREAK -> MaterialTheme.colorScheme.error
    }
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    LaunchedEffect(state.phase) {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(padding)
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
                                color = phaseColor,
                                trackColor = phaseColor.copy(alpha = 0.15f),
                                strokeWidth = 10.dp,
                                modifier = Modifier.size(220.dp)
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                AnimatedContent(
                                    targetState = formatTime(state.remainingMillis),
                                    label = stringResource(R.string.time)
                                ) { formatted ->
                                    Text(
                                        text = formatted,
                                        modifier = Modifier.scale(scale),
                                        style = MaterialTheme.typography.displayMedium
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = phaseSubtitle(state.phase, context),
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
                                        onClick = { viewModel.onStartClick(context) }
                                    ) {
                                        Icon(Icons.Outlined.PlayArrow, contentDescription = null)
                                        Spacer(Modifier.width(8.dp))
                                        Text(stringResource(R.string.start))
                                    }
                                }

                                showPause -> {
                                    Button(
                                        modifier = Modifier.weight(1f),
                                        onClick = { viewModel.onPauseClick() }
                                    ) {
                                        Icon(Icons.Outlined.Pause, contentDescription = null)
                                        Spacer(Modifier.width(8.dp))
                                        Text(stringResource(R.string.pause))
                                    }
                                }

                                showResume -> {
                                    Button(
                                        modifier = Modifier.weight(1f),
                                        onClick = { viewModel.onResumeClick() }
                                    ) {
                                        Icon(Icons.Outlined.PlayArrow, contentDescription = null)
                                        Spacer(Modifier.width(8.dp))
                                        Text(stringResource(R.string.resume))
                                    }
                                }
                            }

                            OutlinedButton(
                                modifier = Modifier.weight(1f),
                                onClick = { viewModel.onStopClick() }
                            ) {
                                Icon(Icons.Outlined.Stop, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text(stringResource(R.string.pause))
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
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        InfoPill(
                            title = stringResource(R.string.focus),
                            value = stringResource(R.string._25m)
                        )
                        InfoPill(
                            title = stringResource(R.string.shortBreak),
                            value = stringResource(R.string._5m)
                        )
                        InfoPill(
                            title = stringResource(R.string.longBreak),
                            value = stringResource(R.string._15m)
                        )
                    }
                }
            }
        }
    }
}

private fun phaseSubtitle(phase: PomodoroPhase, context: Context): String =
    when (phase) {
        PomodoroPhase.FOCUS -> context.getString(R.string.deep_work_time)
        PomodoroPhase.SHORT_BREAK -> context.getString(R.string.recharge_briefly)
        PomodoroPhase.LONG_BREAK -> context.getString(R.string.full_reset_break)
    }

