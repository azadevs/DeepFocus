package com.azadevs.deepfocus.presentation.settings


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.outlined.AutoMode
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.Vibration
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.azadevs.deepfocus.R
import com.azadevs.deepfocus.presentation.settings.component.SettingSliderCard
import com.azadevs.deepfocus.presentation.settings.component.SettingsSectionHeader
import com.azadevs.deepfocus.presentation.settings.component.SettingsSwitchCard
import com.azadevs.deepfocus.presentation.settings.component.SettingsThemeSelectorCard
import com.azadevs.deepfocus.presentation.settings.viewmodel.SettingsViewModel

/**
 * Created by : Azamat Kalmurzaev
 * 27/02/26
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val focusMins by viewModel.focusMinutes.collectAsStateWithLifecycle()
    val shortMins by viewModel.shortBreakMinutes.collectAsStateWithLifecycle()
    val longMins by viewModel.longBreakMinutes.collectAsStateWithLifecycle()
    val soundEnabled by viewModel.soundEnabled.collectAsStateWithLifecycle()
    val vibrationEnabled by viewModel.vibrationEnabled.collectAsStateWithLifecycle()
    val autoStartBreaks by viewModel.autoStartBreaks.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings), fontWeight = FontWeight.ExtraBold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                    titleContentColor = Color.Unspecified
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            contentAlignment = Alignment.TopCenter
        ) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(scrollState)
                    .padding(bottom = 96.dp)
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Section 1: Timer Intervals
                SettingsSectionHeader(title = "Timer Intervals")

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        SettingSliderCard(
                            title = stringResource(R.string.focus_time),
                            icon = Icons.Outlined.Timer,
                            iconBgColor = MaterialTheme.colorScheme.primary,
                            value = focusMins,
                            range = 10f..60f,
                            onValueChange = { 
                                viewModel.updateFocusDuration(it)
                            }
                        )
                        SettingSliderCard(
                            title = stringResource(R.string.short_break),
                            icon = Icons.Outlined.Coffee,
                            iconBgColor = MaterialTheme.colorScheme.primary,
                            value = shortMins,
                            range = 1f..15f,
                            onValueChange = { 
                                viewModel.updateShortBreakDuration(it)
                            }
                        )
                        SettingSliderCard(
                            title = stringResource(R.string.long_break),
                            icon = Icons.Outlined.SelfImprovement,
                            iconBgColor = MaterialTheme.colorScheme.primary,
                            value = longMins,
                            range = 5f..30f,
                            showDivider = false,
                            onValueChange = { 
                                viewModel.updateLongBreakDuration(it)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 2: Notifications & Sound
                SettingsSectionHeader(title = "Notifications & Sound")

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        SettingsSwitchCard(
                            title = "Sound Alert",
                            subtitle = "Play audio chime when timer finishes",
                            icon = Icons.AutoMirrored.Outlined.VolumeUp,
                            iconBgColor = MaterialTheme.colorScheme.primary,
                            checked = soundEnabled,
                            onCheckedChange = {
                                viewModel.toggleSound(it)
                            }
                        )
                        SettingsSwitchCard(
                            title = "Vibration Alert",
                            subtitle = "Vibrate phone on phase completion",
                            icon = Icons.Outlined.Vibration,
                            iconBgColor = MaterialTheme.colorScheme.primary,
                            checked = vibrationEnabled,
                            onCheckedChange = {
                                viewModel.toggleVibration(it)
                            }
                        )
                        SettingsSwitchCard(
                            title = "Auto-Start Breaks",
                            subtitle = "Start break timer automatically",
                            icon = Icons.Outlined.AutoMode,
                            iconBgColor = MaterialTheme.colorScheme.primary,
                            checked = autoStartBreaks,
                            showDivider = false,
                            onCheckedChange = {
                                viewModel.toggleAutoStartBreaks(it)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 3: Theme & Personalization
                SettingsSectionHeader(title = "Theme & Personalization")

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        SettingsThemeSelectorCard(
                            selectedTheme = themeMode,
                            onThemeSelected = {
                                viewModel.setThemeMode(it)
                            },
                            showDivider = false
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 4: About & System
                SettingsSectionHeader(title = "About & Reset")

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                modifier = Modifier.padding(end = 12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "DeepFocus",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "v1.0.0 • Developed by Azadevs",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedButton(
                            onClick = {
                                viewModel.resetToDefaults()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Refresh,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Reset All Settings to Defaults",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

        }
    }
}