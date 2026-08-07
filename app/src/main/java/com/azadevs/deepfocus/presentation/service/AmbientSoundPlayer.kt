package com.azadevs.deepfocus.presentation.service

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import android.os.Handler
import android.os.Looper
import com.azadevs.deepfocus.R
import com.azadevs.deepfocus.domain.model.AmbientSoundMode

/**
 * Created by : Azamat Kalmurzaev
 * 07/08/2026
 */
class AmbientSoundPlayer(private val context: Context) {

    private var exoPlayer: ExoPlayer? = null
    private var currentMode: AmbientSoundMode = AmbientSoundMode.NONE
    private val mainHandler = Handler(Looper.getMainLooper())

    init {
        mainHandler.post {
            exoPlayer = ExoPlayer.Builder(context).build().apply {
                repeatMode = Player.REPEAT_MODE_ONE
                volume = 0.5f
            }
        }
    }

    fun play(mode: AmbientSoundMode) {
        mainHandler.post {
            playInternal(mode)
        }
    }
    
    private fun playInternal(mode: AmbientSoundMode) {
        if (mode == AmbientSoundMode.NONE) {
            stopInternal()
            return
        }

        if (mode == currentMode && exoPlayer?.isPlaying == true) {
            return
        }

        val resId = when (mode) {
            AmbientSoundMode.RAIN -> R.raw.rain
            AmbientSoundMode.FOREST -> R.raw.forest
            AmbientSoundMode.CAFE -> R.raw.cafe
            AmbientSoundMode.WHITE_NOISE -> R.raw.white_noise
            AmbientSoundMode.NONE -> return
        }

        try {
            val uri = "android.resource://${context.packageName}/$resId"
            val mediaItem = MediaItem.fromUri(uri)
            
            exoPlayer?.apply {
                stop()
                setMediaItem(mediaItem)
                prepare()
                play()
            }
            currentMode = mode
        } catch (e: Exception) {
            e.printStackTrace()
            stopInternal()
        }
    }

    fun pause() {
        mainHandler.post {
            if (exoPlayer?.isPlaying == true) {
                exoPlayer?.pause()
            }
        }
    }

    fun resume() {
        mainHandler.post {
            if (exoPlayer != null && !exoPlayer!!.isPlaying) {
                exoPlayer?.play()
            } else if (exoPlayer == null && currentMode != AmbientSoundMode.NONE) {
                playInternal(currentMode)
            }
        }
    }

    fun stop() {
        mainHandler.post {
            stopInternal()
        }
    }

    private fun stopInternal() {
        try {
            exoPlayer?.stop()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            currentMode = AmbientSoundMode.NONE
        }
    }
}
