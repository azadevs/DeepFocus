package com.azadevs.deepfocus.domain.pomodoro

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import androidx.core.content.ContextCompat
import com.azadevs.deepfocus.domain.model.FocusSession
import com.azadevs.deepfocus.domain.model.PomodoroConfig
import com.azadevs.deepfocus.domain.model.PomodoroPhase
import com.azadevs.deepfocus.domain.model.PomodoroState
import com.azadevs.deepfocus.domain.model.SessionType
import com.azadevs.deepfocus.domain.model.TimerEvent
import com.azadevs.deepfocus.domain.model.TimerState
import com.azadevs.deepfocus.domain.repository.FocusRepository
import com.azadevs.deepfocus.domain.timer.TimerManager
import com.azadevs.deepfocus.domain.usecase.DeepFocusUseCases
import com.azadevs.deepfocus.presentation.service.FocusForegroundService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by : Azamat Kalmurzaev
 * 21/02/2026
 */
@Singleton
class PomodoroController @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val timerManager: TimerManager,
    private val focusRepository: FocusRepository,
    private val useCases: DeepFocusUseCases
) {
    private var config = PomodoroConfig()

    private val _state = MutableStateFlow(PomodoroState())
    val state: StateFlow<PomodoroState> = _state.asStateFlow()

    private var mediaPlayer: MediaPlayer? = null

    private var observeJob: Job? = null
    private var phaseStartTime: Long = 0L

    private val controllerScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    init {
        observeTimer()
        observeSettings()
    }

    private fun observeSettings() {
        controllerScope.launch {
            launch {
                useCases.getFocusDuration().collect { duration ->
                    duration.let { config = config.copy(focusMinutes = it) }
                }
            }
            launch {
                useCases.getShortBreakDuration().collect { duration ->
                    duration.let { config = config.copy(shortBreakMinutes = it) }
                }
            }
            launch {
                useCases.getLongBreakDuration().collect { duration ->
                    duration.let { config = config.copy(longBreakMinutes = it) }
                }
            }
        }
    }

    fun start() {
        val duration = durationFor(_state.value.phase)
        phaseStartTime = System.currentTimeMillis()

        _state.value = _state.value.copy(
            remainingMillis = duration,
            phaseDurationMillis = duration,
            isRunning = true
        )

        timerManager.start(controllerScope, duration)

        val intent = Intent(context, FocusForegroundService::class.java).apply {
            action = FocusForegroundService.ACTION_START
            putExtra(FocusForegroundService.EXTRA_DURATION, duration)
        }
        ContextCompat.startForegroundService(context, intent)
    }

    fun pause() {
        timerManager.pause()
        _state.value = _state.value.copy(isRunning = false)
    }

    fun resume() {
        timerManager.resume(controllerScope)
        _state.value = _state.value.copy(isRunning = true)
    }

    fun stop() {
        timerManager.stop(controllerScope)
        _state.value = PomodoroState()
    }

    private fun observeTimer() {
        observeJob?.cancel()

        observeJob = controllerScope.launch {

            launch {
                timerManager.timerState.collect { timerState ->
                    when (timerState) {
                        is TimerState.Running -> {
                            _state.value = _state.value.copy(
                                remainingMillis = timerState.remainingMillis,
                                isRunning = true
                            )
                        }

                        is TimerState.Paused -> {
                            _state.value = _state.value.copy(
                                remainingMillis = timerState.remainingMillis,
                                isRunning = false
                            )
                        }

                        is TimerState.Finished -> {

                        }

                        else -> {}
                    }
                }
            }

            launch {
                timerManager.events.collect { event ->
                    if (event is TimerEvent.Finished) {
                        handlePhaseFinished()
                    }
                }
            }
        }
    }

    private fun handlePhaseFinished() {
        val currentPhase = _state.value.phase

        if (currentPhase == PomodoroPhase.FOCUS) {
            val endTime = System.currentTimeMillis()
            val startTime = phaseStartTime

            controllerScope.launch {
                focusRepository.upsertSession(
                    FocusSession(
                        id = 0L,
                        startTime = startTime,
                        endTime = endTime,
                        durationMinutes = config.focusMinutes,
                        type = SessionType.FOCUS
                    )
                )
            }
        }
        playAlarmSound()
        moveToNextPhase()
    }

    private fun moveToNextPhase() {
        val current = _state.value

        val nextPhase = when (current.phase) {
            PomodoroPhase.FOCUS -> {
                if (current.cycleIndex % config.cyclesBeforeLongBreak == 0)
                    PomodoroPhase.LONG_BREAK
                else
                    PomodoroPhase.SHORT_BREAK
            }

            PomodoroPhase.SHORT_BREAK,
            PomodoroPhase.LONG_BREAK -> PomodoroPhase.FOCUS
        }

        val nextCycle =
            if (nextPhase == PomodoroPhase.FOCUS)
                current.cycleIndex + 1
            else
                current.cycleIndex

        val nextDuration = durationFor(nextPhase)

        _state.value = current.copy(
            phase = nextPhase,
            cycleIndex = nextCycle,
            remainingMillis = nextDuration,
            phaseDurationMillis = nextDuration,
            isRunning = false
        )
    }

    private fun playAlarmSound() {
        try {
            val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            mediaPlayer = MediaPlayer.create(context, alarmUri).apply {
                isLooping = true
                start()
            }
            _state.value = _state.value.copy(isRinging = true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopAlarm() {
        mediaPlayer?.let {
            if (it.isPlaying) it.stop()
            it.release()
        }
        mediaPlayer = null
        if (_state.value.isRinging) {
            _state.value = _state.value.copy(isRinging = false)
        }
    }

    private fun durationFor(phase: PomodoroPhase): Long {
        return when (phase) {
            PomodoroPhase.FOCUS ->
                config.focusMinutes * 60_000L

            PomodoroPhase.SHORT_BREAK ->
                config.shortBreakMinutes * 60_000L

            PomodoroPhase.LONG_BREAK ->
                config.longBreakMinutes * 60_000L
        }
    }

    fun skip() {
        stopAlarm()
        timerManager.stop(controllerScope)
        moveToNextPhase()
    }
}