package com.azadevs.deepfocus.presentation.statistics.component

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.azadevs.deepfocus.R
import com.azadevs.deepfocus.presentation.statistics.viewmodel.HeatmapDay

/**
 * Created by : Azamat Kalmurzaev
 * 25/05/2026
 */
@Composable
fun FocusHeatmapCard(
    stats: List<HeatmapDay>,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    LaunchedEffect(stats) {
        if (stats.isNotEmpty()) {
            scrollState.scrollTo(scrollState.maxValue)
        }
    }

    val weeks = remember(stats) { stats.chunked(7) }

    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant
    val heatmapColors = remember(primaryColor, surfaceVariantColor) {
        List(5) { level ->
            when (level) {
                0 -> surfaceVariantColor.copy(alpha = 0.4f)
                1 -> primaryColor.copy(alpha = 0.25f)
                2 -> primaryColor.copy(alpha = 0.5f)
                3 -> primaryColor.copy(alpha = 0.75f)
                else -> primaryColor
            }
        }
    }

    val contentDesc = stringResource(R.string.heatmap_content_desc)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = contentDesc },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header
            Text(
                text = stringResource(R.string.heatmap_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(R.string.heatmap_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.padding(end = 6.dp)
                ) {
                    Text(
                        text = stringResource(R.string.heatmap_day_mon),
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.height(10.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp)) // skips Tuesday label for clean look
                    Text(
                        text = stringResource(R.string.heatmap_day_wed),
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.height(10.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp)) // skips Thursday label for clean look
                    Text(
                        text = stringResource(R.string.heatmap_day_fri),
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.height(10.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp)) // skips Saturday label for clean look
                    Text(
                        text = stringResource(R.string.heatmap_day_sun),
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.height(10.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(scrollState),
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    weeks.forEach { week ->
                        Column(
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            week.forEach { day ->
                                val color = heatmapColors.getOrElse(day.level) { Color.Transparent }
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(color)
                                )
                            }
                            // Fill remaining days of the week if it's incomplete
                            if (week.size < 7) {
                                repeat(7 - week.size) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(Color.Transparent)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.heatmap_less),
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                repeat(5) { level ->
                    val color = heatmapColors.getOrElse(level) { Color.Transparent }
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .size(10.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(color)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.heatmap_more),
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
