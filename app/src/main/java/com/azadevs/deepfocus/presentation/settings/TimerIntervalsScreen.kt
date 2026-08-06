package com.azadevs.deepfocus.presentation.settings

import android.widget.NumberPicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.azadevs.deepfocus.presentation.settings.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerIntervalsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val focusMins by viewModel.focusMinutes.collectAsStateWithLifecycle()
    val shortMins by viewModel.shortBreakMinutes.collectAsStateWithLifecycle()
    val longMins by viewModel.longBreakMinutes.collectAsStateWithLifecycle()

    var showPicker by remember { mutableStateOf(false) }
    var pickerTitle by remember { mutableStateOf("") }
    var pickerValue by remember { mutableStateOf(0) }
    var pickerRange by remember { mutableStateOf(0..0) }
    var onPickerSave by remember { mutableStateOf<(Int) -> Unit>({}) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Timer Intervals", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(top = 16.dp)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.background
            ) {
                Column {
                    TimerItem(
                        title = "Focus Time",
                        value = focusMins,
                        onClick = {
                            pickerTitle = "Focus Time"
                            pickerValue = focusMins
                            pickerRange = 10..60
                            onPickerSave = { viewModel.updateFocusDuration(it) }
                            showPicker = true
                        }
                    )
                    TimerItem(
                        title = "Short Break",
                        value = shortMins,
                        onClick = {
                            pickerTitle = "Short Break"
                            pickerValue = shortMins
                            pickerRange = 1..15
                            onPickerSave = { viewModel.updateShortBreakDuration(it) }
                            showPicker = true
                        }
                    )
                    TimerItem(
                        title = "Long Break",
                        value = longMins,
                        showDivider = false,
                        onClick = {
                            pickerTitle = "Long Break"
                            pickerValue = longMins
                            pickerRange = 5..30
                            onPickerSave = { viewModel.updateLongBreakDuration(it) }
                            showPicker = true
                        }
                    )
                }
            }
        }
    }

    if (showPicker) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showPicker = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = pickerTitle,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(24.dp))
                
                var currentValue by remember { mutableIntStateOf(pickerValue) }
                
                AndroidView(
                    factory = { context ->
                        NumberPicker(context).apply {
                            minValue = pickerRange.first
                            maxValue = pickerRange.last
                            value = currentValue
                            wrapSelectorWheel = false
                            setOnValueChangedListener { _, _, newVal ->
                                currentValue = newVal
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = {
                        onPickerSave(currentValue)
                        showPicker = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save")
                }
            }
        }
    }
}

@Composable
private fun TimerItem(
    title: String,
    value: Int,
    showDivider: Boolean = true,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "$value min",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 20.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
        }
    }
}
