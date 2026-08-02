package com.azadevs.deepfocus.domain.pomodoro

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
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
import kotlinx.coroutines.flow.combine
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

    private val _state = MutableStateFlow(
        PomodoroState(
            phase = PomodoroPhase.FOCUS,
            remainingMillis = config.focusMinutes * 60_000L,
            phaseDurationMillis = config.focusMinutes * 60_000L
        )
    )
    val state: StateFlow<PomodoroState> = _state.asStateFlow()

    private val _selectedTask = MutableStateFlow<com.azadevs.deepfocus.domain.model.Task?>(null)
    val selectedTask: StateFlow<com.azadevs.deepfocus.domain.model.Task?> = _selectedTask.asStateFlow()

    fun selectTask(task: com.azadevs.deepfocus.domain.model.Task?) {
        _selectedTask.value = task
    }

    private var mediaPlayer: MediaPlayer? = null

    private var observeJob: Job? = null
    private var phaseStartTime: Long = 0L

    private val controllerScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var audioFocusRequest: AudioFocusRequest? = null

    init {
        observeTimer()
        observeSettings()
    }

    private fun observeSettings() {
        controllerScope.launch {
            combine(
                useCases.getFocusDuration(),
                useCases.getShortBreakDuration(),
                useCases.getLongBreakDuration()
            ) { focus, short, long ->
                PomodoroConfig(
                    focusMinutes = focus,
                    shortBreakMinutes = short,
                    longBreakMinutes = long,
                    cyclesBeforeLongBreak = config.cyclesBeforeLongBreak
                )
            }.collect { newConfig ->
                config = newConfig

                val current = _state.value
                if (!current.isRunning && current.remainingMillis == current.phaseDurationMillis) {
                    val idleDuration = durationFor(current.phase)
                    _state.value = current.copy(
                        remainingMillis = idleDuration,
                        phaseDurationMillis = idleDuration
                    )
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
        stopAlarm()
        timerManager.stop(controllerScope)
        val idleDuration = durationFor(_state.value.phase)
        _state.value = PomodoroState(
            phase = _state.value.phase,
            cycleIndex = _state.value.cycleIndex,
            remainingMillis = idleDuration,
            phaseDurationMillis = idleDuration,
            isRunning = false,
            isRinging = false
        )
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
            val activeTask = _selectedTask.value

            controllerScope.launch {
                focusRepository.upsertSession(
                    FocusSession(
                        id = 0L,
                        startTime = startTime,
                        endTime = endTime,
                        durationMinutes = config.focusMinutes,
                        type = SessionType.FOCUS,
                        taskId = activeTask?.id,
                        taskTitle = activeTask?.title
                    )
                )
                if (activeTask != null) {
                    useCases.upsertTask(
                        activeTask.copy(totalFocusMinutes = activeTask.totalFocusMinutes + config.focusMinutes)
                    )
                }
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
            var alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            if (alarmUri == null) {
                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()

                audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                    .setAudioAttributes(audioAttributes)
                    .build()

                audioManager.requestAudioFocus(audioFocusRequest!!)
            }

            mediaPlayer = MediaPlayer.create(context, alarmUri).apply {
                isLooping = true
                start()
            }

            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

            if (vibrator.hasVibrator()) {
                val pattern = longArrayOf(0, 500, 500)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(pattern, 0)
                }
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && audioFocusRequest != null) {
            audioManager.abandonAudioFocusRequest(audioFocusRequest!!)
        }

        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        vibrator.cancel()

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