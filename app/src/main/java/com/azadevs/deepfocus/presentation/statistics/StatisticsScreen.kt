package com.azadevs.deepfocus.presentation.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.azadevs.deepfocus.R
import com.azadevs.deepfocus.presentation.statistics.component.EmptyStatisticsCard
import com.azadevs.deepfocus.presentation.statistics.component.FocusHeatmapCard
import com.azadevs.deepfocus.presentation.statistics.component.SummaryCard
import com.azadevs.deepfocus.presentation.statistics.component.TaskTimelineCard
import com.azadevs.deepfocus.presentation.statistics.component.WeeklyBarChart
import com.azadevs.deepfocus.presentation.statistics.viewmodel.StatisticsViewModel

/**
 * Created by : Azamat Kalmurzaev
 * 27/02/26
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val totalMinutes by viewModel.totalFocusMinutes.collectAsStateWithLifecycle()
    val sessions by viewModel.allSessions.collectAsStateWithLifecycle()
    val weeklyStats by viewModel.weeklyStats.collectAsStateWithLifecycle()
    val currentStreak by viewModel.currentStreak.collectAsStateWithLifecycle()
    val bestStreak by viewModel.bestStreak.collectAsStateWithLifecycle()
    val heatmapStats by viewModel.heatmapStats.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()

    val timelineFilter by viewModel.timelineFilter.collectAsStateWithLifecycle()
    val taskTimelineStats by viewModel.taskTimelineStats.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.statistics), fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                item(key = "progress_summary", contentType = "summary") {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = stringResource(R.string.your_progress),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SummaryCard(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.streak),
                            value = "$currentStreak 🔥",
                            icon = Icons.Default.LocalFireDepartment
                        )
                        SummaryCard(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.best_streak),
                            value = "$bestStreak 🏆",
                            icon = Icons.Default.EmojiEvents
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SummaryCard(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.sessions),
                            value = "${sessions.size}",
                            icon = Icons.Outlined.CheckCircle
                        )
                        SummaryCard(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.average),
                            value = if (sessions.isNotEmpty()) "${totalMinutes / sessions.size}m" else "0m",
                            icon = Icons.Outlined.Timer
                        )
                    }
                }
            }

            if (sessions.isNotEmpty()) {
                item(key = "task_timeline_card", contentType = "timeline") {
                    TaskTimelineCard(
                        timelineItems = taskTimelineStats,
                        selectedFilter = timelineFilter,
                        onFilterSelect = viewModel::setTimelineFilter
                    )
                }
            }

            if (sessions.isEmpty()) {
                item(key = "empty_statistics_state", contentType = "empty") {
                    EmptyStatisticsCard()
                }
            }

            if (weeklyStats.isNotEmpty() && sessions.isNotEmpty()) {
                item(key = "weekly_chart", contentType = "chart") {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(
                            text = stringResource(R.string.last_7_days),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        WeeklyBarChart(stats = weeklyStats)
                    }
                }
            }

            if (sessions.isNotEmpty()) {
                item(key = "heatmap_card", contentType = "heatmap") {
                    FocusHeatmapCard(stats = heatmapStats)
                }
            }
        }
        }
    }
}