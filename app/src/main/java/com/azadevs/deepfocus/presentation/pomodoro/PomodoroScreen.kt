package com.azadevs.deepfocus.presentation.pomodoro

import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.azadevs.deepfocus.R
import com.azadevs.deepfocus.domain.model.PomodoroPhase
import com.azadevs.deepfocus.presentation.pomodoro.component.FlowOrb
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
    val focusTime by viewModel.focusDuration.collectAsState()
    val shortTime by viewModel.shortBreakDuration.collectAsState()
    val longTime by viewModel.longBreakDuration.collectAsState()

    val total = max(1L, state.phaseDurationMillis)
    val remaining = min(total, max(0L, state.remainingMillis))
    val rawProgress = 1f - (remaining.toFloat() / total.toFloat())
    val progress by animateFloatAsState(
        targetValue = rawProgress.coerceIn(0f, 1f),
        label = "progress"
    )
    val scale by animateFloatAsState(
        targetValue = if (state.isRunning) 1.05f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f),
        label = "scale"
    )
    val phaseColor by animateColorAsState(
        targetValue = when (state.phase) {
            PomodoroPhase.FOCUS -> MaterialTheme.colorScheme.primary
            PomodoroPhase.SHORT_BREAK -> MaterialTheme.colorScheme.secondary
            PomodoroPhase.LONG_BREAK -> MaterialTheme.colorScheme.tertiary
        }, label = "phaseColor"
    )

    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    LaunchedEffect(state.phase) {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name), fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                actions = {
                    Surface(
                        modifier = Modifier.padding(end = 12.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    ) {
                        Text(
                            text = "Cycle ${state.cycleIndex}",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(padding)
                    .padding(bottom = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                PhaseChip(phase = state.phase, color = phaseColor)

                Spacer(modifier = Modifier.height(48.dp))

                Box(contentAlignment = Alignment.Center) {
                    FlowOrb(
                        progress = progress,
                        color = phaseColor,
                        modifier = Modifier.size(280.dp),
                        strokeWidth = 12.dp,
                        glowRadius = 50f,
                        isPulsing = state.isRunning
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = formatTime(state.remainingMillis),
                            modifier = Modifier.scale(scale),
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = phaseSubtitle(state.phase, context),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (state.isRinging) {
                        ExtendedFloatingActionButton(
                            onClick = { viewModel.onStopAlarmClick() },
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                            icon = {
                                Icon(
                                    Icons.Default.NotificationsOff,
                                    contentDescription = null,
                                    modifier = Modifier.size(28.dp)
                                )
                            },
                            text = {
                                Text(
                                    stringResource(R.string.turn_off_alarm),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            },
                            modifier = Modifier.height(64.dp)
                        )
                    } else {
                        val canStart =
                            !state.isRunning && state.remainingMillis == state.phaseDurationMillis
                        val showPause = state.isRunning
                        val showResume =
                            !state.isRunning && state.remainingMillis > 0L && state.remainingMillis < state.phaseDurationMillis

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            FilledIconButton(
                                onClick = { viewModel.onStopClick() },
                                modifier = Modifier.size(56.dp),
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Icon(
                                    Icons.Default.Stop,
                                    contentDescription = stringResource(R.string.stop),
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            FloatingActionButton(
                                onClick = {
                                    if (canStart) viewModel.onStartClick()
                                    else if (showPause) viewModel.onPauseClick()
                                    else if (showResume) viewModel.onResumeClick()
                                },
                                containerColor = phaseColor,
                                modifier = Modifier.size(80.dp),
                                shape = CircleShape,
                                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp)
                            ) {
                                Icon(
                                    imageVector = if (showPause) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp)
                                )
                            }

                            FilledIconButton(
                                onClick = { viewModel.onSkipClick() },
                                modifier = Modifier.size(56.dp),
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Icon(
                                    Icons.Default.SkipNext,
                                    contentDescription = stringResource(R.string.skip),
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        InfoPill(stringResource(R.string.focus), "${focusTime}m")
                        InfoPill(stringResource(R.string.shortBreak), "${shortTime}m")
                        InfoPill(stringResource(R.string.longBreak), "${longTime}m")
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

