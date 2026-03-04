package com.azadevs.deepfocus.presentation.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext

/**
 * Remembers the device tilt as an [Offset] ranging from roughly -1f to 1f
 * depending on how far the device is tilted horizontally and vertically.
 * 
 * Uses the Accelerometer sensor to detect gravity/tilt.
 */
@Composable
fun rememberDeviceTilt(): Offset {
    val context = LocalContext.current
    var tilt by remember { mutableStateOf(Offset.Zero) }

    DisposableEffect(context) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                    val x = event.values[0]
                    val y = event.values[1]
                    val normalizedX = (x / -9.8f).coerceIn(-1f, 1f)
                    val normalizedY = (y / 9.8f).coerceIn(-1f, 1f)
                    
                    tilt = Offset(x = normalizedX, y = normalizedY)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            }
        }

        accelerometer?.let {
            sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI)
        }

        onDispose {
            sensorManager?.unregisterListener(listener)
        }
    }

    return tilt
}
