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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.azadevs.deepfocus.domain.model.PomodoroState
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
        },
        label = "phaseColor"
    )

    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    LaunchedEffect(state.phase) {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    Scaffold(
        topBar = {
            PomodoroTopBar(cycleIndex = state.cycleIndex)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(bottom = 110.dp)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            TopSection(
                phase = state.phase,
                phaseColor = phaseColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            TimerSection(
                state = state,
                progress = progress,
                scale = scale,
                phaseColor = phaseColor,
                context = context,
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.78f)
                    .aspectRatio(1f)
            )

            BottomSection(
                state = state,
                focusTime = focusTime,
                shortTime = shortTime,
                longTime = longTime,
                phaseColor = phaseColor,
                onStartClick = viewModel::onStartClick,
                onPauseClick = viewModel::onPauseClick,
                onResumeClick = viewModel::onResumeClick,
                onStopClick = viewModel::onStopClick,
                onSkipClick = viewModel::onSkipClick,
                onStopAlarmClick = viewModel::onStopAlarmClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PomodoroTopBar(
    cycleIndex: Int,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = stringResource(R.string.app_name),
                fontWeight = FontWeight.Bold
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ),
        actions = {
            Surface(
                modifier = Modifier.padding(end = 12.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 2.dp
            ) {
                Text(
                    text = "Cycle $cycleIndex",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}

@Composable
private fun TopSection(
    phase: PomodoroPhase,
    phaseColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        PhaseChip(phase = phase, color = phaseColor)
    }
}

@Composable
private fun TimerSection(
    state: PomodoroState,
    progress: Float,
    scale: Float,
    phaseColor: Color,
    context: Context,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        FlowOrb(
            progress = progress,
            color = phaseColor,
            modifier = Modifier.fillMaxSize(),
            strokeWidth = 12.dp,
            glowRadius = 50f,
            isPulsing = state.isRunning
        )

        // Countdown text overlay
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = formatTime(state.remainingMillis),
                modifier = Modifier.scale(scale),
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = phaseSubtitle(state.phase, context),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun BottomSection(
    state: PomodoroState,
    focusTime: Int,
    shortTime: Int,
    longTime: Int,
    phaseColor: Color,
    onStartClick: () -> Unit,
    onPauseClick: () -> Unit,
    onResumeClick: () -> Unit,
    onStopClick: () -> Unit,
    onSkipClick: () -> Unit,
    onStopAlarmClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        ControlsSection(
            state = state,
            phaseColor = phaseColor,
            onStartClick = onStartClick,
            onPauseClick = onPauseClick,
            onResumeClick = onResumeClick,
            onStopClick = onStopClick,
            onSkipClick = onSkipClick,
            onStopAlarmClick = onStopAlarmClick
        )

        StatisticsPills(
            focusTime = focusTime,
            shortTime = shortTime,
            longTime = longTime,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ControlsSection(
    state: PomodoroState,
    phaseColor: Color,
    onStartClick: () -> Unit,
    onPauseClick: () -> Unit,
    onResumeClick: () -> Unit,
    onStopClick: () -> Unit,
    onSkipClick: () -> Unit,
    onStopAlarmClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(88.dp),
        contentAlignment = Alignment.Center
    ) {
        if (state.isRinging) {
            ExtendedFloatingActionButton(
                onClick = onStopAlarmClick,
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                icon = {
                    Icon(
                        Icons.Default.NotificationsOff,
                        contentDescription = null,
                        modifier = Modifier.size(26.dp)
                    )
                },
                text = {
                    Text(
                        text = stringResource(R.string.turn_off_alarm),
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                modifier = Modifier.height(60.dp)
            )
        } else {
            val canStart = !state.isRunning && state.remainingMillis == state.phaseDurationMillis
            val showPause = state.isRunning
            val showResume = !state.isRunning
                    && state.remainingMillis > 0L
                    && state.remainingMillis < state.phaseDurationMillis

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                FilledIconButton(
                    onClick = onStopClick,
                    modifier = Modifier.size(52.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Icon(
                        Icons.Default.Stop,
                        contentDescription = stringResource(R.string.stop),
                        modifier = Modifier.size(26.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                FloatingActionButton(
                    onClick = {
                        when {
                            canStart -> onStartClick()
                            showPause -> onPauseClick()
                            showResume -> onResumeClick()
                        }
                    },
                    containerColor = phaseColor,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(76.dp),
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
                ) {
                    Icon(
                        imageVector = if (showPause) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(38.dp)
                    )
                }

                FilledIconButton(
                    onClick = onSkipClick,
                    modifier = Modifier.size(52.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Icon(
                        Icons.Default.SkipNext,
                        contentDescription = stringResource(R.string.skip),
                        modifier = Modifier.size(26.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun StatisticsPills(
    focusTime: Int,
    shortTime: Int,
    longTime: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            InfoPill(
                title = stringResource(R.string.focus),
                value = "${focusTime}m"
            )
            Spacer(
                modifier = Modifier
                    .width(1.dp)
                    .height(32.dp)
            )
            InfoPill(
                title = stringResource(R.string.shortBreak),
                value = "${shortTime}m"
            )
            Spacer(
                modifier = Modifier
                    .width(1.dp)
                    .height(32.dp)
            )
            InfoPill(
                title = stringResource(R.string.longBreak),
                value = "${longTime}m"
            )
        }
    }
}

private fun phaseSubtitle(phase: PomodoroPhase, context: Context): String =
    when (phase) {
        PomodoroPhase.FOCUS -> context.getString(R.string.deep_work_time)
        PomodoroPhase.SHORT_BREAK -> context.getString(R.string.recharge_briefly)
        PomodoroPhase.LONG_BREAK -> context.getString(R.string.full_reset_break)
    }
