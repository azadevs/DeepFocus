package com.azadevs.deepfocus.presentation.pomodoro.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.azadevs.deepfocus.domain.model.PomodoroPhase
import kotlin.math.cos
import kotlin.math.sin

/**
 * Created by : Azamat Kalmurzaev
 * Represents an animated mesh gradient background for the Pomodoro Screen.
 */
@Composable
fun AnimatedMeshBackground(
    phase: PomodoroPhase,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "mesh_transition")

    // Slow, breathable animations for x and y positions
    val anim1 by transition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "anim1"
    )

    val anim2 by transition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "anim2"
    )

    val anim3 by transition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(25000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "anim3"
    )

    // Base background color
    val backgroundColor by animateColorAsState(
        targetValue = MaterialTheme.colorScheme.background,
        animationSpec = tween(800),
        label = "bg_color"
    )

    // Primary mesh color (adapts to phase)
    val phaseColor by animateColorAsState(
        targetValue = when (phase) {
            PomodoroPhase.FOCUS -> MaterialTheme.colorScheme.primary
            PomodoroPhase.SHORT_BREAK -> MaterialTheme.colorScheme.secondary
            PomodoroPhase.LONG_BREAK -> MaterialTheme.colorScheme.tertiary
        },
        animationSpec = tween(800),
        label = "phase_color"
    )
    
    // Derived mesh colors for complexity
    val color1 = phaseColor.copy(alpha = 0.25f)
    val color2 = phaseColor.copy(alpha = 0.15f)
    val color3 = phaseColor.copy(alpha = 0.08f)

    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        val width = size.width
        val height = size.height
        
        // Draw base background
        drawRect(color = backgroundColor)

        // Draw blurry moving orbs (mesh effect is created by drawing large blurred circles)
        // Note: Using an exact RenderEffect or blur modifier on Android can be performance heavy
        // or require API > 31. We simulate it here by drawing massive circles that blend together,
        // but if on newer APIs, `Modifier.blur` on Box elements is cleaner. Canvas is safer across APIs.
        // We'll use simple Canvas circles for widespread compatibility without performance tanks.

        val cx = width / 2
        val cy = height / 2

        // Orb 1: Upper Right to Lower Left
        drawOrb(
            color = color1,
            x = cx + (cos(anim1) * width * 0.4f),
            y = cy + (sin(anim1) * height * 0.3f),
            radius = width * 0.7f
        )

        // Orb 2: Lower Right to Upper Left
        drawOrb(
            color = color2,
            x = cx - (sin(anim2) * width * 0.4f),
            y = cy + (cos(anim2) * height * 0.4f),
            radius = width * 0.8f
        )

        // Orb 3: Center drifting
        drawOrb(
            color = color3,
            x = cx + (cos(anim3) * width * 0.2f),
            y = cy - (sin(anim3) * height * 0.2f),
            radius = width * 0.9f
        )
    }
}

private fun DrawScope.drawOrb(color: Color, x: Float, y: Float, radius: Float) {
    drawCircle(
        color = color,
        center = Offset(x, y),
        radius = radius
    )
}
