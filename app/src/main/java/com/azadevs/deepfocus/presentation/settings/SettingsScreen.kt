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
import androidx.navigation.NavController
import com.azadevs.deepfocus.R
import com.azadevs.deepfocus.presentation.settings.component.SettingsNavigationCard
import com.azadevs.deepfocus.presentation.settings.component.SettingsSectionHeader
import com.azadevs.deepfocus.presentation.settings.component.SettingsSwitchCard
import com.azadevs.deepfocus.presentation.settings.component.SettingsThemeSelectorCard
import com.azadevs.deepfocus.presentation.settings.viewmodel.SettingsViewModel
import com.azadevs.deepfocus.presentation.util.navigation.TimerSettingsRoute

/**
 * Created by : Azamat Kalmurzaev
 * 27/02/26
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
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
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        // Section 1: Timer
                        SettingsSectionHeader(title = "Timer")
                        SettingsNavigationCard(
                            title = "Timer Intervals",
                            icon = Icons.Outlined.Timer,
                            iconBgColor = MaterialTheme.colorScheme.primary,
                            showDivider = true,
                            onClick = { navController.navigate(TimerSettingsRoute) }
                        )

                        // Section 2: Notifications & Sound
                        SettingsSectionHeader(title = "Notifications & Sound")
                        SettingsSwitchCard(
                            title = "Sound Alert",
                            subtitle = "Play audio chime when timer finishes",
                            icon = Icons.AutoMirrored.Outlined.VolumeUp,
                            iconBgColor = MaterialTheme.colorScheme.primary,
                            checked = soundEnabled,
                            onCheckedChange = { viewModel.toggleSound(it) }
                        )
                        SettingsSwitchCard(
                            title = "Vibration Alert",
                            subtitle = "Vibrate phone on phase completion",
                            icon = Icons.Outlined.Vibration,
                            iconBgColor = MaterialTheme.colorScheme.primary,
                            checked = vibrationEnabled,
                            onCheckedChange = { viewModel.toggleVibration(it) }
                        )
                        SettingsSwitchCard(
                            title = "Auto-Start Breaks",
                            subtitle = "Start break timer automatically",
                            icon = Icons.Outlined.AutoMode,
                            iconBgColor = MaterialTheme.colorScheme.primary,
                            checked = autoStartBreaks,
                            showDivider = true,
                            onCheckedChange = { viewModel.toggleAutoStartBreaks(it) }
                        )

                        // Section 3: Theme & Personalization
                        SettingsSectionHeader(title = "Theme & Personalization")
                        SettingsThemeSelectorCard(
                            selectedTheme = themeMode,
                            onThemeSelected = { viewModel.setThemeMode(it) },
                            showDivider = false
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "DeepFocus v1.0.0",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }

        }
    }
}