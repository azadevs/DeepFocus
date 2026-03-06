package com.azadevs.deepfocus.presentation.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
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

    private var wakeLock: PowerManager.WakeLock? = null

    private var currentPlayer: MediaPlayer? = null
    private var nextPlayer: MediaPlayer? = null
    private var currentSoundResId: Int? = null

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
        const val ACTION_STOP_ALARM = "ACTION_STOP_ALARM"

        const val ACTION_PLAY_AMBIENT = "ACTION_PLAY_AMBIENT"
        const val ACTION_STOP_AMBIENT = "ACTION_STOP_AMBIENT"
        const val EXTRA_SOUND_RES_ID = "EXTRA_SOUND_RES_ID"
    }

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
        acquireWakeLock()
        observeTimer()
    }

    private fun acquireWakeLock() {
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "DeepFocus::TimerWakeLock"
        )
        wakeLock?.acquire()
    }

    private fun releaseWakeLock() {
        if (wakeLock?.isHeld == true) {
            wakeLock?.release()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when (intent?.action) {
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
                pauseAmbientSound()
            }

            ACTION_RESUME -> {
                controller.resume()
                resumeAmbientSound()
            }

            ACTION_STOP -> {
                controller.stop()
                stopAmbientSound()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                isForegroundStarted = false
            }

            ACTION_PLAY_AMBIENT -> {
                val soundResId = intent.getIntExtra(EXTRA_SOUND_RES_ID, -1)
                if (soundResId != -1) {
                    playAmbientSound(soundResId)
                }
            }

            ACTION_STOP_AMBIENT -> {
                stopAmbientSound()
            }
        }

        return START_STICKY
    }

    private fun createPlayer(resId: Int): MediaPlayer? {
        return MediaPlayer.create(this, resId)?.apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
        }
    }

    private val completionListener = object : MediaPlayer.OnCompletionListener {
        override fun onCompletion(mp: MediaPlayer) {
            mp.release()
            currentPlayer = nextPlayer

            val resId = currentSoundResId
            if (resId != null) {
                try {
                    nextPlayer = createPlayer(resId)
                    currentPlayer?.setNextMediaPlayer(nextPlayer)
                    currentPlayer?.setOnCompletionListener(this)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun playAmbientSound(resId: Int) {
        if (currentSoundResId == resId && currentPlayer?.isPlaying == true) return

        stopAmbientSound()

        try {
            currentSoundResId = resId
            currentPlayer = createPlayer(resId)
            nextPlayer = createPlayer(resId)

            currentPlayer?.setNextMediaPlayer(nextPlayer)
            currentPlayer?.setOnCompletionListener(completionListener)

            if (controller.state.value.isRunning) {
                currentPlayer?.start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            stopAmbientSound()
        }
    }

    private fun pauseAmbientSound() {
        if (currentPlayer?.isPlaying == true) {
            currentPlayer?.pause()
        }
    }

    private fun resumeAmbientSound() {
        if (currentPlayer != null && !currentPlayer!!.isPlaying) {
            currentPlayer?.start()
        }
    }

    private fun stopAmbientSound() {
        currentPlayer?.let {
            if (it.isPlaying) it.stop()
            it.release()
        }
        nextPlayer?.release()

        currentPlayer = null
        nextPlayer = null
        currentSoundResId = null
    }

    private fun observeTimer() {
        serviceScope.launch {
            controller.state.collect { state ->
                if (isForegroundStarted) {
                    val isReady =
                        !state.isRunning && !state.isRinging && state.remainingMillis == state.phaseDurationMillis

                    if (isReady) {
                        stopAmbientSound()
                        stopForeground(STOP_FOREGROUND_REMOVE)
                        stopSelf()
                        isForegroundStarted = false
                    } else {
                        updateNotification(state)

                        if (state.isRinging || !state.isRunning) {
                            pauseAmbientSound()
                        } else if (currentPlayer != null && !currentPlayer!!.isPlaying) {
                            resumeAmbientSound()
                        }
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

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("DeepFocus Timer")
            .setOngoing(true)
            .setContentIntent(activityPendingIntent)
            .setDeleteIntent(deletePendingIntent)
            .setAutoCancel(false)

        when {
            state.isRinging -> {
                val title = when (state.phase) {
                    PomodoroPhase.FOCUS -> "Break is over! \u23F0"
                    PomodoroPhase.SHORT_BREAK, PomodoroPhase.LONG_BREAK -> "Focus finished! \u23F0"
                }

                val text = when (state.phase) {
                    PomodoroPhase.FOCUS -> "Time to focus."
                    PomodoroPhase.SHORT_BREAK -> "Time for a short break."
                    PomodoroPhase.LONG_BREAK -> "Time for a long break."
                }

                builder
                    .setContentTitle(title)
                    .setContentText(text)
                    .setOngoing(false)
                    .addAction(
                        R.drawable.ic_stop,
                        "Turn off alarm",
                        pendingIntent(ACTION_STOP_ALARM)
                    )
            }

            state.isRunning -> {
                builder
                    .setContentText(DeepFocusUtils.formatTime(state.remainingMillis))
                    .setOngoing(true)
                    .addAction(R.drawable.ic_pause, "Pause", pendingIntent(ACTION_PAUSE))
            }

            !state.isRunning && state.remainingMillis > 0L -> {
                builder
                    .setContentText("Paused • ${DeepFocusUtils.formatTime(state.remainingMillis)}")
                    .setOngoing(true)
                    .addAction(R.drawable.ic_play, "Resume", pendingIntent(ACTION_RESUME))
            }

            else -> {
                builder.setContentText("Ready").setOngoing(true)
            }
        }

        if (!state.isRinging) {
            builder.addAction(R.drawable.ic_stop, "Stop", pendingIntent(ACTION_STOP))
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
        stopAmbientSound()
        serviceScope.cancel()
        releaseWakeLock()
    }
}