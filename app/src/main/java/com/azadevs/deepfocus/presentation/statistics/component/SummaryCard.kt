package com.azadevs.deepfocus.presentation.statistics.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Created by : Azamat Kalmurzaev
 * 27/02/26
 */

@Composable
fun SummaryCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Box(
        modifier = modifier
             .fillMaxWidth()
             .shadow(
                 elevation = 12.dp,
                 shape = RoundedCornerShape(20.dp),
                 spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                 ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
             )
             .clip(RoundedCornerShape(20.dp))
             .background(
                 brush = Brush.linearGradient(
                     colors = listOf(
                         MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                         MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                     )
                 )
             )
             .border(
                 width = 1.dp,
                 brush = Brush.linearGradient(
                     colors = listOf(
                         Color.White.copy(alpha = 0.4f),
                         Color.White.copy(alpha = 0.0f),
                         MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                     )
                 ),
                 shape = RoundedCornerShape(20.dp)
             )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }
    }
}