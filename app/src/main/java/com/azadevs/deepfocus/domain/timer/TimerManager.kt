package com.azadevs.deepfocus.domain.timer

import com.azadevs.deepfocus.domain.model.TimerEvent
import com.azadevs.deepfocus.domain.model.TimerState
import com.azadevs.deepfocus.domain.repository.TimerRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton
/**
 * Created by : Azamat Kalmurzaev
 * 21/02/2026
 */
@Singleton
class TimerManager @Inject constructor(
    private val repo: TimerRepository
) {
    private val engine = TimerEngine()

    private val _timerState = MutableStateFlow<TimerState>(TimerState.Idle)
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    private val _events = MutableSharedFlow<TimerEvent>()
    val events: SharedFlow<TimerEvent> = _events.asSharedFlow()

    private var endTime: Long = 0L
    private var remainingAfterPause: Long = 0L

    fun start(scope: CoroutineScope, durationMillis: Long) {
        endTime = System.currentTimeMillis() + durationMillis

        scope.launch(Dispatchers.IO) {
            repo.saveEndTime(endTime)
        }

        run(scope)
    }

    private fun run(scope: CoroutineScope) {
        engine.start(
            scope = scope,
            endTimeMillis = endTime,
            onTick = { remaining ->
                _timerState.value = TimerState.Running(remaining)
            },
            onFinish = {
                finish(scope)
            }
        )
    }

    fun pause() {
        engine.cancel()
        remainingAfterPause = endTime - System.currentTimeMillis()
        _timerState.value = TimerState.Paused(remainingAfterPause)
    }

    fun resume(scope: CoroutineScope) {
        endTime = System.currentTimeMillis() + remainingAfterPause
        run(scope)
    }

    fun stop(scope: CoroutineScope) {
        engine.cancel()
        _timerState.value = TimerState.Idle
        clearPersistence(scope)
    }

    private fun finish(scope: CoroutineScope) {
        _timerState.value = TimerState.Finished
        scope.launch { _events.emit(TimerEvent.Finished) }
        clearPersistence(scope)
    }

    private fun clearPersistence(scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) { repo.clear() }
    }

    suspend fun restoreIfNeeded(scope: CoroutineScope) {
        if (!repo.isRunning()) return
        val savedEndTime = repo.getSavedEndTime() ?: return
        endTime = savedEndTime
        run(scope)
    }
}