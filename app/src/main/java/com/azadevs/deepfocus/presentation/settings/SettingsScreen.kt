package com.azadevs.deepfocus.presentation.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.delay

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
    
    var showSavedIndicator by remember { mutableStateOf(false) }
    var saveIndicatorTrigger by remember { mutableIntStateOf(0) }

    LaunchedEffect(saveIndicatorTrigger) {
        if (saveIndicatorTrigger > 0) {
            showSavedIndicator = true
            delay(1500)
            showSavedIndicator = false
        }
    }

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
                .systemBarsPadding()
        ) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(scrollState)
                    .padding(bottom = 96.dp)
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Section 1: Timer Intervals
                SettingsSectionHeader(title = "Timer Intervals", emoji = "⏱️")

                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
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
                                saveIndicatorTrigger++
                            }
                        )
                        SettingSliderCard(
                            title = stringResource(R.string.short_break),
                            icon = Icons.Outlined.Coffee,
                            iconBgColor = Color(0xFFFF9800),
                            value = shortMins,
                            range = 1f..15f,
                            onValueChange = { 
                                viewModel.updateShortBreakDuration(it)
                                saveIndicatorTrigger++
                            }
                        )
                        SettingSliderCard(
                            title = stringResource(R.string.long_break),
                            icon = Icons.Outlined.SelfImprovement,
                            iconBgColor = Color(0xFF4CAF50),
                            value = longMins,
                            range = 5f..30f,
                            showDivider = false,
                            onValueChange = { 
                                viewModel.updateLongBreakDuration(it)
                                saveIndicatorTrigger++
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 2: Notifications & Sound
                SettingsSectionHeader(title = "Notifications & Sound", emoji = "🔔")

                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        SettingsSwitchCard(
                            title = "Sound Alert",
                            subtitle = "Play audio chime when timer finishes",
                            icon = Icons.AutoMirrored.Outlined.VolumeUp,
                            iconBgColor = Color(0xFF2196F3),
                            checked = soundEnabled,
                            onCheckedChange = {
                                viewModel.toggleSound(it)
                                saveIndicatorTrigger++
                            }
                        )
                        SettingsSwitchCard(
                            title = "Vibration Alert",
                            subtitle = "Vibrate phone on phase completion",
                            icon = Icons.Outlined.Vibration,
                            iconBgColor = Color(0xFF9C27B0),
                            checked = vibrationEnabled,
                            onCheckedChange = {
                                viewModel.toggleVibration(it)
                                saveIndicatorTrigger++
                            }
                        )
                        SettingsSwitchCard(
                            title = "Auto-Start Breaks",
                            subtitle = "Start break timer automatically",
                            icon = Icons.Outlined.AutoMode,
                            iconBgColor = Color(0xFF00BCD4),
                            checked = autoStartBreaks,
                            showDivider = false,
                            onCheckedChange = {
                                viewModel.toggleAutoStartBreaks(it)
                                saveIndicatorTrigger++
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 3: Theme & Personalization
                SettingsSectionHeader(title = "Theme & Personalization", emoji = "🎨")

                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        SettingsThemeSelectorCard(
                            selectedTheme = themeMode,
                            onThemeSelected = {
                                viewModel.setThemeMode(it)
                                saveIndicatorTrigger++
                            },
                            showDivider = false
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 4: About & System
                SettingsSectionHeader(title = "About & Reset", emoji = "ℹ️")

                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
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
                                saveIndicatorTrigger++
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

            // Animated Settings Saved Toast Notification
            AnimatedVisibility(
                visible = showSavedIndicator,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    tonalElevation = 6.dp,
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Settings Saved ✨",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}