package com.azadevs.deepfocus.presentation.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.azadevs.deepfocus.R
import com.azadevs.deepfocus.core.util.TimeFormatter
import com.azadevs.deepfocus.domain.model.PomodoroState
import com.azadevs.deepfocus.domain.pomodoro.PomodoroController
import com.azadevs.deepfocus.presentation.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by : Azamat Kalmurzaev
 * 21/02/2026
 */
@AndroidEntryPoint
class FocusForegroundService : Service() {

    @Inject
    lateinit var controller: PomodoroController

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var isForegroundStarted = false

    companion object {
        private const val CHANNEL_ID = "focus_channel"
        private const val NOTIFICATION_ID = 1

        const val ACTION_FOREGROUND = "ACTION_FOREGROUND"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_RESUME = "ACTION_RESUME"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_START = "ACTION_START"
        const val EXTRA_DURATION = "EXTRA_DURATION"
    }

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        observeTimer()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when (intent?.action) {
            ACTION_START -> {
                if (!isForegroundStarted) {
                    startForeground(
                        NOTIFICATION_ID,
                        buildNotification(controller.state.value)
                    )
                    isForegroundStarted = true
                }
            }

            ACTION_FOREGROUND -> {
                if (!isForegroundStarted) {
                    startForeground(
                        NOTIFICATION_ID,
                        buildNotification(controller.state.value)
                    )
                    isForegroundStarted = true
                }
            }

            ACTION_PAUSE -> controller.pause()

            ACTION_RESUME -> controller.resume()

            ACTION_STOP -> {
                controller.stop()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }

        return START_STICKY
    }

    private fun observeTimer() {
        serviceScope.launch {
            controller.state.collect { state ->
                if (isForegroundStarted) {
                    updateNotification(state)
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildNotification(state: PomodoroState): Notification {

        val activityIntent = Intent(this, MainActivity::class.java).apply {
            this.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val activityPendingIntent = PendingIntent.getActivity(
            this,
            0,
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("DeepFocus")
            .setSmallIcon(R.drawable.ic_notification)
            .setOngoing(true)
            .setContentIntent(activityPendingIntent)
            .setAutoCancel(false)

        when {
            state.isRunning -> {
                builder
                    .setContentText(TimeFormatter.format(state.remainingMillis))
                    .addAction(R.drawable.ic_pause, "Pause", pendingIntent(ACTION_PAUSE))
            }

            !state.isRunning && state.remainingMillis > 0L -> {
                builder
                    .setContentText("Paused • ${TimeFormatter.format(state.remainingMillis)}")
                    .addAction(R.drawable.ic_play, "Resume", pendingIntent(ACTION_RESUME))
            }

            else -> builder.setContentText("Ready")
        }

        builder.addAction(R.drawable.ic_stop, "Stop", pendingIntent(ACTION_STOP))

        return builder.build()
    }

    private fun pendingIntent(action: String): PendingIntent {
        val intent = Intent(this, FocusForegroundService::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(
            this,
            action.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun updateNotification(state: PomodoroState) {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, buildNotification(state))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "DeepFocus Timer",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}