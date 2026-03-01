package com.azadevs.deepfocus.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.azadevs.deepfocus.R
import com.azadevs.deepfocus.presentation.settings.component.SettingSliderCard
import com.azadevs.deepfocus.presentation.settings.viewmodel.SettingsViewModel

/**
 * Created by : Azamat Kalmurzaev
 * 27/02/26
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val focusMins by viewModel.focusMinutes.collectAsState()
    val shortMins by viewModel.shortBreakMinutes.collectAsState()
    val longMins by viewModel.longBreakMinutes.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings), fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.8f)
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        onNavigateBack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                        )
                    )
                )
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                SettingSliderCard(
                    title = stringResource(R.string.focus_time),
                    icon = Icons.Outlined.Timer,
                    value = focusMins,
                    range = 10f..60f,
                    onValueChange = { viewModel.updateFocusDuration(it) }
                )

                SettingSliderCard(
                    title = stringResource(R.string.short_break),
                    icon = Icons.Outlined.Coffee,
                    value = shortMins,
                    range = 1f..15f,
                    onValueChange = { viewModel.updateShortBreakDuration(it) }
                )

                SettingSliderCard(
                    title = stringResource(R.string.long_break),
                    icon = Icons.Outlined.SelfImprovement,
                    value = longMins,
                    range = 10f..45f,
                    onValueChange = { viewModel.updateLongBreakDuration(it) }
                )
            }
        }
    }
}