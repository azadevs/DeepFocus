package com.azadevs.deepfocus.presentation.pomodoro.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.azadevs.deepfocus.presentation.util.rememberDeviceTilt
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun FlowOrb(
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 12.dp,
    glowRadius: Float = 50f,
    isPulsing: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb_transition")

    val phase1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "phase1"
    )

    val phase2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(5500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "phase2"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isPulsing) 1.05f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val deviceTilt = rememberDeviceTilt()

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = (size.minDimension - strokeWidth.toPx() * 2) / 2f

            val tiltOffset = Offset(
                x = deviceTilt.x * (radius * 0.15f),
                y = deviceTilt.y * (radius * 0.15f)
            )

            drawFluidOrb(
                center = center + tiltOffset,
                radius = radius * pulseScale,
                color = color,
                phase1 = phase1,
                phase2 = phase2,
                glowRadius = glowRadius
            )

            drawProgressRing(
                progress = progress,
                color = color,
                strokeWidth = strokeWidth.toPx(),
                radius = radius,
                center = center,
            )
        }
    }
}

private fun DrawScope.drawFluidOrb(
    center: Offset,
    radius: Float,
    color: Color,
    phase1: Float,
    phase2: Float,
    glowRadius: Float
) {
    val path = Path()
    val pointsCount = 8
    val angleStep = (2 * PI) / pointsCount

    val points = List(pointsCount) { i ->
        val angle = i * angleStep
        
        val noise = sin(angle * 2 + phase1) * cos(angle * 3 + phase2)
        val perturbedRadius = radius + (radius * 0.1f * noise).toFloat()
        
        val x = center.x + perturbedRadius * cos(angle).toFloat()
        val y = center.y + perturbedRadius * sin(angle).toFloat()
        Offset(x, y)
    }

    path.moveTo(points.first().x, points.first().y)
    for (i in points.indices) {
        val current = points[i]
        val next = points[(i + 1) % points.size]
        
        val midPointX = (current.x + next.x) / 2
        val midPointY = (current.y + next.y) / 2
        
        path.quadraticTo(current.x, current.y, midPointX, midPointY)
    }
    path.close()

    drawCircle(
        color = color.copy(alpha = 0.15f),
        radius = radius + glowRadius,
        center = center
    )

    drawPath(
        path = path,
        brush = Brush.radialGradient(
            colors = listOf(color.copy(alpha = 0.8f), color.copy(alpha = 0.4f)),
            center = center,
            radius = radius
        )
    )
}

private fun DrawScope.drawProgressRing(
    progress: Float,
    color: Color,
    strokeWidth: Float,
    radius: Float,
    center: Offset,
) {
    val sweepAngle = progress * 360f

    drawArc(
        color = color.copy(alpha = 0.1f),
        startAngle = -90f,
        sweepAngle = 360f,
        useCenter = false,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
        size = Size(radius * 2, radius * 2),
        topLeft = Offset(center.x - radius, center.y - radius)
    )

    if (progress > 0) {
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            size = Size(radius * 2, radius * 2),
            topLeft = Offset(center.x - radius, center.y - radius)
        )
    }
}
