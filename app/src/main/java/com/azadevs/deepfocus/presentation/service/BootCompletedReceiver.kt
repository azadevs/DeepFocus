package com.azadevs.deepfocus.presentation.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.azadevs.deepfocus.domain.pomodoro.PomodoroController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Created by : Azamat Kalmurzaev
 * Restores the Pomodoro timer after device reboot.
 *
 * When the device boots, Hilt injects PomodoroController whose init block
 * automatically calls restoreTimerState(). If a timer was running before
 * the reboot and time hasn't expired, the timer and foreground service
 * are seamlessly resumed.
 */
@AndroidEntryPoint
class BootCompletedReceiver : BroadcastReceiver() {

    @Inject
    lateinit var controller: PomodoroController

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // PomodoroController is @Singleton — injecting it here triggers
            // its init block which calls restoreTimerState().
            // No additional logic needed; the controller handles everything.
            controller.state.value
        }
    }
}
