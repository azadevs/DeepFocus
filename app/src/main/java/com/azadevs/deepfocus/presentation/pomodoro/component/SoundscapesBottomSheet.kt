package com.azadevs.deepfocus.presentation.pomodoro.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeOff
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.azadevs.deepfocus.R
import com.azadevs.deepfocus.presentation.pomodoro.model.SoundscapeItem

/**
 * Created by : Azamat Kalmurzaev
 * 03/03/26
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoundscapesBottomSheet(
    selectedSoundId: Int,
    onDismiss: () -> Unit,
    onSoundSelected: (Int) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    val sounds = listOf(
        SoundscapeItem(-1, stringResource(R.string.sound_none),
            Icons.AutoMirrored.Outlined.VolumeOff
        ),
        SoundscapeItem(R.raw.ambient_rain, stringResource(R.string.sound_rain), Icons.Outlined.WaterDrop),
        SoundscapeItem(R.raw.ambient_cafe, stringResource(R.string.sound_cafe), Icons.Outlined.Coffee),
        SoundscapeItem(R.raw.ambient_fire, stringResource(R.string.sound_fire), Icons.Outlined.LocalFireDepartment)
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = stringResource(R.string.soundscapes),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )

            sounds.forEach { item ->
                val isSelected = selectedSoundId == item.resId
                ListItem(
                    modifier = Modifier.clickable { onSoundSelected(item.resId) },
                    headlineContent = {
                        Text(
                            text = item.name,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    leadingContent = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }
    }
}