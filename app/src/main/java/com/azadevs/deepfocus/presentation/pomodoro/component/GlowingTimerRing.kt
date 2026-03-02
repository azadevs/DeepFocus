package com.azadevs.deepfocus.presentation.pomodoro.component

import android.graphics.BlurMaskFilter
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Created by : Azamat Kalmurzaev
 * Custom timer ring with a glowing shadow effect.
 */
@Composable
fun GlowingTimerRing(
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.DarkGray.copy(alpha = 0.3f),
    strokeWidth: Dp = 12.dp,
    glowRadius: Float = 40f
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000),
        label = "timer_progress"
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(280.dp)) {
            val strokeWidthPx = strokeWidth.toPx()
            val sizeValue = size.minDimension - strokeWidthPx
            val arcSize = Size(sizeValue, sizeValue)
            val offset = Offset((size.width - sizeValue) / 2, (size.height - sizeValue) / 2)

            // Draw Background Track
            drawArc(
                color = backgroundColor,
                startAngle = 270f,
                sweepAngle = 360f,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = strokeWidthPx,
                    cap = StrokeCap.Round
                ),
                topLeft = offset,
                size = arcSize
            )

            // Calculate progress angle
            val sweepAngle = 360f * animatedProgress
            
            // Draw Glow (only if we have progress > 0)
            if (animatedProgress > 0.01f) {
                drawIntoCanvas { canvas ->
                    val paint = Paint().apply {
                        this.style = PaintingStyle.Stroke
                        this.strokeWidth = strokeWidthPx
                        this.strokeCap = StrokeCap.Round
                        this.color = color
                        val frameworkPaint = this.asFrameworkPaint()
                        frameworkPaint.maskFilter = BlurMaskFilter(glowRadius, BlurMaskFilter.Blur.NORMAL)
                    }
                    canvas.drawArc(
                        rect = androidx.compose.ui.geometry.Rect(offset, arcSize),
                        startAngle = 270f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        paint = paint
                    )
                }
            }

            // Draw solid foreground arc
            drawArc(
                color = color,
                startAngle = 270f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = strokeWidthPx,
                    cap = StrokeCap.Round
                ),
                topLeft = offset,
                size = arcSize
            )
        }
    }
}
