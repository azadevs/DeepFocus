package com.azadevs.deepfocus.presentation.pomodoro.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Forest
import androidx.compose.material.icons.outlined.Headphones
import androidx.compose.material.icons.outlined.LocalCafe
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.azadevs.deepfocus.domain.model.AmbientSoundMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmbientSoundBottomSheet(
    sheetState: SheetState,
    selectedMode: AmbientSoundMode,
    onModeSelect: (AmbientSoundMode) -> Unit,
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Ambient Sounds",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "Play relaxing background sounds during Focus.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            AmbientSoundItem(
                mode = AmbientSoundMode.NONE,
                icon = Icons.Outlined.Headphones,
                selected = selectedMode == AmbientSoundMode.NONE,
                onClick = { onModeSelect(AmbientSoundMode.NONE) }
            )
            
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            
            AmbientSoundItem(
                mode = AmbientSoundMode.RAIN,
                icon = Icons.Outlined.WaterDrop,
                selected = selectedMode == AmbientSoundMode.RAIN,
                onClick = { onModeSelect(AmbientSoundMode.RAIN) }
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            AmbientSoundItem(
                mode = AmbientSoundMode.FOREST,
                icon = Icons.Outlined.Forest,
                selected = selectedMode == AmbientSoundMode.FOREST,
                onClick = { onModeSelect(AmbientSoundMode.FOREST) }
            )
            
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            AmbientSoundItem(
                mode = AmbientSoundMode.CAFE,
                icon = Icons.Outlined.LocalCafe,
                selected = selectedMode == AmbientSoundMode.CAFE,
                onClick = { onModeSelect(AmbientSoundMode.CAFE) }
            )
            
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            AmbientSoundItem(
                mode = AmbientSoundMode.WHITE_NOISE,
                icon = Icons.Outlined.Cloud,
                selected = selectedMode == AmbientSoundMode.WHITE_NOISE,
                onClick = { onModeSelect(AmbientSoundMode.WHITE_NOISE) },
                showDivider = false
            )
        }
    }
}

@Composable
private fun AmbientSoundItem(
    mode: AmbientSoundMode,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    showDivider: Boolean = true
) {
    val color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    val bgColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = bgColor,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.padding(10.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = mode.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        
        if (selected) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
