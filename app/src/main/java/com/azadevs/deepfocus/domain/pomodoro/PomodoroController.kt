package com.azadevs.deepfocus.domain.pomodoro

import com.azadevs.deepfocus.domain.model.FocusSession
import com.azadevs.deepfocus.domain.model.PomodoroConfig
import com.azadevs.deepfocus.domain.model.PomodoroPhase
import com.azadevs.deepfocus.domain.model.PomodoroState
import com.azadevs.deepfocus.domain.model.SessionType
import com.azadevs.deepfocus.domain.model.TimerEvent
import com.azadevs.deepfocus.domain.model.TimerState
import com.azadevs.deepfocus.domain.repository.FocusRepository
import com.azadevs.deepfocus.domain.timer.TimerManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
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
    private val timerManager: TimerManager,
    private val focusRepository: FocusRepository
) {

    private val config = PomodoroConfig()

    private val _state = MutableStateFlow(PomodoroState())
    val state: StateFlow<PomodoroState> = _state.asStateFlow()

    private var observeJob: Job? = null
    private var phaseStartTime: Long = 0L

    fun start(scope: CoroutineScope) {
        val duration = durationFor(_state.value.phase)

        phaseStartTime = System.currentTimeMillis()

        _state.value = _state.value.copy(
            remainingMillis = duration,
            phaseDurationMillis = duration,
            isRunning = true
        )

        timerManager.start(scope, duration)
        observeTimer(scope)
    }

    fun pause() {
        timerManager.pause()
        _state.value = _state.value.copy(isRunning = false)
    }

    fun resume(scope: CoroutineScope) {
        timerManager.resume(scope)
        _state.value = _state.value.copy(isRunning = true)
    }

    fun stop(scope: CoroutineScope) {
        timerManager.stop(scope)
        _state.value = PomodoroState()
    }

    private fun observeTimer(scope: CoroutineScope) {
        observeJob?.cancel()

        observeJob = scope.launch {

            launch {
                timerManager.timerState.collect { timerState ->
                    when (timerState) {
                        is TimerState.Running -> {
                            _state.value = _state.value.copy(
                                remainingMillis = timerState.remainingMillis
                            )
                        }

                        is TimerState.Paused -> {
                            _state.value = _state.value.copy(
                                remainingMillis = timerState.remainingMillis,
                                isRunning = false
                            )
                        }

                        else -> Unit
                    }
                }
            }

            launch {
                timerManager.events.collect { event ->
                    if (event is TimerEvent.Finished) {
                        handlePhaseFinished(scope)
                    }
                }
            }
        }
    }

    private fun handlePhaseFinished(scope: CoroutineScope) {

        val currentPhase = _state.value.phase

        if (currentPhase == PomodoroPhase.FOCUS) {

            val endTime = System.currentTimeMillis()
            val startTime = phaseStartTime

            scope.launch {
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
        moveToNextPhase(scope)
    }
    private fun moveToNextPhase(scope: CoroutineScope) {
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

        _state.value = PomodoroState(
            phase = nextPhase,
            cycleIndex = nextCycle
        )

        start(scope)
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

    fun selectPhase(phase: PomodoroPhase) {
        _state.value = PomodoroState(
            phase = phase,
            cycleIndex = _state.value.cycleIndex
        )
    }
}