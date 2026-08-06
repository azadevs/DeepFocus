package com.azadevs.deepfocus.presentation.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo

import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.azadevs.deepfocus.R
import com.azadevs.deepfocus.domain.model.PomodoroPhase
import com.azadevs.deepfocus.domain.model.PomodoroState
import com.azadevs.deepfocus.domain.pomodoro.PomodoroController
import com.azadevs.deepfocus.presentation.MainActivity
import com.azadevs.deepfocus.presentation.util.DeepFocusUtils
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
    private var lastRingingState = false

    companion object {
        private const val CHANNEL_ID = "focus_channel"
        private const val ALARM_CHANNEL_ID = "alarm_channel"
        private const val NOTIFICATION_ID = 1
        private const val ALARM_NOTIFICATION_ID = 2

        const val ACTION_FOREGROUND = "ACTION_FOREGROUND"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_RESUME = "ACTION_RESUME"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_START = "ACTION_START"
        const val EXTRA_DURATION = "EXTRA_DURATION"
        const val ACTION_STOP_ALARM = "ACTION_STOP_ALARM"
    }

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
        observeTimer()
    }



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent?.action == null) {
            stopSelf()
            return START_NOT_STICKY
        }

        when (intent.action) {
            ACTION_START, ACTION_FOREGROUND -> {
                if (!isForegroundStarted) {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                            startForeground(
                                NOTIFICATION_ID,
                                buildNotification(controller.state.value),
                                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                            )
                        } else {
                            startForeground(
                                NOTIFICATION_ID,
                                buildNotification(controller.state.value)
                            )
                        }
                        isForegroundStarted = true
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            ACTION_STOP_ALARM -> {
                controller.stopAlarm()
            }

            ACTION_PAUSE -> {
                controller.pause()
            }

            ACTION_RESUME -> {
                controller.resume()
            }

            ACTION_STOP -> {
                controller.stopAlarm()
                controller.stop()

                val manager = getSystemService(NotificationManager::class.java)
                manager.cancel(NOTIFICATION_ID)
                manager.cancel(ALARM_NOTIFICATION_ID)

                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                isForegroundStarted = false
            }
        }

        return START_NOT_STICKY
    }



    private fun observeTimer() {
        serviceScope.launch {
            controller.state.collect { state ->
                if (isForegroundStarted) {
                    val isReady =
                        !state.isRunning && !state.isRinging && state.remainingMillis == state.phaseDurationMillis

                    if (isReady) {
                        val manager = getSystemService(NotificationManager::class.java)
                        manager.cancel(NOTIFICATION_ID)
                        manager.cancel(ALARM_NOTIFICATION_ID)

                        stopForeground(STOP_FOREGROUND_REMOVE)
                        stopSelf()
                        isForegroundStarted = false
                    } else {
                        updateNotification(state)
                    }
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
        val deleteIntent = Intent(this, FocusForegroundService::class.java).apply {
            action = ACTION_STOP_ALARM
        }
        val deletePendingIntent = PendingIntent.getService(
            this, 1, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = if (state.isRinging) ALARM_CHANNEL_ID else CHANNEL_ID
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(getString(R.string.notification_timer_title))
            .setOngoing(true)
            .setContentIntent(activityPendingIntent)
            .setDeleteIntent(deletePendingIntent)
            .setAutoCancel(false)
            .setCategory(if (state.isRinging) NotificationCompat.CATEGORY_ALARM else NotificationCompat.CATEGORY_PROGRESS)

        if (state.isRinging) {
            val fullScreenIntent = Intent(this, TimerExpiredActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val fullScreenPendingIntent = PendingIntent.getActivity(
                this,
                1001,
                fullScreenIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            builder.setFullScreenIntent(fullScreenPendingIntent, true)
            builder.setPriority(NotificationCompat.PRIORITY_MAX)
            builder.setOngoing(true)
        }

        when {
            state.isRinging -> {
                val title = when (state.phase) {
                    PomodoroPhase.FOCUS -> getString(R.string.notification_break_over)
                    PomodoroPhase.SHORT_BREAK, PomodoroPhase.LONG_BREAK -> getString(R.string.notification_focus_finished)
                }

                val text = when (state.phase) {
                    PomodoroPhase.FOCUS -> getString(R.string.notification_time_to_focus)
                    PomodoroPhase.SHORT_BREAK -> getString(R.string.notification_time_short_break)
                    PomodoroPhase.LONG_BREAK -> getString(R.string.notification_time_long_break)
                }

                builder
                    .setContentTitle(title)
                    .setContentText(text)
                    .addAction(
                        R.drawable.ic_stop,
                        getString(R.string.turn_off_alarm),
                        pendingIntent(ACTION_STOP_ALARM)
                    )
            }

            state.isRunning -> {
                builder
                    .setContentText(DeepFocusUtils.formatTime(state.remainingMillis))
                    .setOngoing(true)
                    .addAction(
                        R.drawable.ic_pause,
                        getString(R.string.pause),
                        pendingIntent(ACTION_PAUSE)
                    )
            }

            !state.isRunning && state.remainingMillis > 0L -> {
                builder
                    .setContentText(
                        getString(
                            R.string.notification_paused,
                            DeepFocusUtils.formatTime(state.remainingMillis)
                        )
                    )
                    .setOngoing(true)
                    .addAction(
                        R.drawable.ic_play,
                        getString(R.string.resume),
                        pendingIntent(ACTION_RESUME)
                    )
            }

            else -> {
                builder.setContentText(getString(R.string.notification_ready)).setOngoing(true)
            }
        }

        if (!state.isRinging) {
            builder.addAction(
                R.drawable.ic_stop,
                getString(R.string.stop),
                pendingIntent(ACTION_STOP)
            )
        }
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

        if (state.isRinging) {
            if (!lastRingingState) {
                // Cancel the old timer notification first to clear channel association cache in system
                manager.cancel(NOTIFICATION_ID)

                val alarmNotification = buildNotification(state)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    startForeground(
                        ALARM_NOTIFICATION_ID,
                        alarmNotification,
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                    )
                } else {
                    startForeground(
                        ALARM_NOTIFICATION_ID,
                        alarmNotification
                    )
                }
                lastRingingState = true
            } else {
                manager.notify(ALARM_NOTIFICATION_ID, buildNotification(state))
            }
        } else {
            if (lastRingingState) {
                manager.cancel(ALARM_NOTIFICATION_ID)

                val timerNotification = buildNotification(state)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    startForeground(
                        NOTIFICATION_ID,
                        timerNotification,
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                    )
                } else {
                    startForeground(
                        NOTIFICATION_ID,
                        timerNotification
                    )
                }
                lastRingingState = false
            } else {
                manager.notify(NOTIFICATION_ID, buildNotification(state))
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val manager = getSystemService(NotificationManager::class.java)

        val timerChannel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.notification_channel_timer),
            NotificationManager.IMPORTANCE_LOW
        )
        manager.createNotificationChannel(timerChannel)

        val alarmChannel = NotificationChannel(
            ALARM_CHANNEL_ID,
            getString(R.string.notification_channel_alarm),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            enableVibration(true)
            enableLights(true)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
        manager.createNotificationChannel(alarmChannel)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}