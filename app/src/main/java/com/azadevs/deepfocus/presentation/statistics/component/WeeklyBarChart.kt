package com.azadevs.deepfocus.presentation.statistics.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.azadevs.deepfocus.R
import com.azadevs.deepfocus.presentation.statistics.viewmodel.DailyStat

@Composable
fun WeeklyBarChart(stats: List<DailyStat>) {
    val maxMinutes = stats.maxOfOrNull { it.minutes }?.coerceAtLeast(1) ?: 1

    var animationPlayed by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) { animationPlayed = true }

    val animationProgress by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(durationMillis = 1500, delayMillis = 200),
        label = "chart_anim"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant
    val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)

    val textMeasurer = rememberTextMeasurer()
    val textStyle = MaterialTheme.typography.labelMedium.copy(color = textColor, fontWeight = FontWeight.Medium)
    val valStyle = textStyle.copy(fontWeight = FontWeight.Bold, color = primaryColor)

    val dayLayouts = remember(stats, textStyle) {
        stats.map { textMeasurer.measure(it.dayName, textStyle) }
    }
    val valLayouts = remember(stats, valStyle) {
        stats.map { textMeasurer.measure("${it.minutes}", valStyle) }
    }

    val chartContentDesc = stringResource(R.string.chart_content_desc)


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .semantics { contentDescription = chartContentDesc },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface // Toza solid fon
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp, bottom = 20.dp, start = 20.dp, end = 20.dp)
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val textSpace = 24.dp.toPx()
            val chartHeight = canvasHeight - textSpace

            val calculatedBarWidth = canvasWidth / (stats.size * 2f)
            val barWidth = calculatedBarWidth.coerceAtMost(40.dp.toPx())

            val steps = 3
            for (i in 0..steps) {
                val y = chartHeight - (chartHeight * (i.toFloat() / steps))
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(canvasWidth, y),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                )
            }

            stats.forEachIndexed { index, stat ->
                val sectionWidth = canvasWidth / stats.size
                val xPos = (index * sectionWidth) + (sectionWidth / 2) - (barWidth / 2)

                // Orqa fon (track)
                drawRoundRect(
                    color = trackColor.copy(alpha = 0.4f),
                    topLeft = Offset(xPos, 0f),
                    size = Size(barWidth, chartHeight),
                    cornerRadius = CornerRadius(50f, 50f)
                )

                val fillHeight = (stat.minutes.toFloat() / maxMinutes) * chartHeight * animationProgress
                val yPos = chartHeight - fillHeight

                // Asosiy Ustun (Gradient color)
                if (fillHeight > 0) {
                    val gradientBrush = Brush.verticalGradient(
                        colors = listOf(primaryColor, primaryContainer),
                        startY = yPos,
                        endY = yPos + fillHeight
                    )
                    drawRoundRect(
                        brush = gradientBrush,
                        topLeft = Offset(xPos, yPos),
                        size = Size(barWidth, fillHeight),
                        cornerRadius = CornerRadius(50f, 50f)
                    )

                    // Ustun ustidagi kichik oq chiziqcha (yarqirash effekti)
                    drawRoundRect(
                        color = Color.White.copy(alpha = 0.4f),
                        topLeft = Offset(xPos + (barWidth * 0.2f), yPos + 2.dp.toPx()),
                        size = Size(barWidth * 0.6f, 4.dp.toPx()),
                        cornerRadius = CornerRadius(50f, 50f)
                    )
                }

                val dayLayoutResult = dayLayouts[index]
                val textX = xPos + (barWidth - dayLayoutResult.size.width) / 2
                val textY = chartHeight + 8.dp.toPx()

                drawText(
                    textLayoutResult = dayLayoutResult,
                    topLeft = Offset(textX, textY)
                )

                if (stat.minutes > 0) {
                    val valLayoutResult = valLayouts[index]
                    val valX = xPos + (barWidth - valLayoutResult.size.width) / 2
                    val valY = yPos - valLayoutResult.size.height - 4.dp.toPx()

                    drawText(
                        textLayoutResult = valLayoutResult,
                        topLeft = Offset(valX, valY)
                    )
                }
            }
        }
    }
}