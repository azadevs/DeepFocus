package com.azadevs.deepfocus.presentation.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
    
    var showSavedIndicator by remember { mutableStateOf(false) }
    var saveIndicatorTrigger by remember { androidx.compose.runtime.mutableIntStateOf(0) }

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
                title = { Text(stringResource(R.string.settings), fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                    navigationIconContentColor = Color.Unspecified,
                    titleContentColor = Color.Unspecified,
                    actionIconContentColor = Color.Unspecified
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
                    .padding(bottom = 110.dp)
                    .padding(start = 20.dp, end = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.2f)),
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
                            iconBgColor = MaterialTheme.colorScheme.secondary,
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
                            iconBgColor = MaterialTheme.colorScheme.tertiary,
                            value = longMins,
                            range = 10f..45f,
                            showDivider = false,
                            onValueChange = { 
                                viewModel.updateLongBreakDuration(it) 
                                saveIndicatorTrigger++
                            }
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                AnimatedVisibility(
                    visible = showSavedIndicator,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF4CAF50).copy(alpha = 0.9f),
                        contentColor = Color.White,
                        shadowElevation = 8.dp
                    ) {
                        androidx.compose.foundation.layout.Row(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Saved", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}