package com.azadevs.deepfocus.domain.timer

import kotlinx.coroutines.*
/**
 * Created by : Azamat Kalmurzaev
 * 21/02/2026
 */
class TimerEngine {
    private var job: Job? = null

    fun start(
        scope: CoroutineScope,
        endTimeMillis: Long,
        onTick: (Long) -> Unit,
        onFinish: () -> Unit
    ) {
        job?.cancel()

        job = scope.launch {
            while (true) {
                val remaining = endTimeMillis - System.currentTimeMillis()
                if (remaining <= 0) {
                    onFinish()
                    break
                }
                onTick(remaining)
                delay(1000L)
            }
        }
    }

    fun cancel() {
        job?.cancel()
    }
}