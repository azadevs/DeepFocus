package com.azadevs.deepfocus.presentation.util.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.azadevs.deepfocus.presentation.MainViewModel
import com.azadevs.deepfocus.presentation.onboarding.OnboardingScreen
import com.azadevs.deepfocus.presentation.pomodoro.PomodoroScreen
import com.azadevs.deepfocus.presentation.settings.SettingsScreen
import com.azadevs.deepfocus.presentation.settings.TimerIntervalsScreen
import com.azadevs.deepfocus.presentation.statistics.StatisticsScreen
import com.azadevs.deepfocus.presentation.tasks.TasksScreen

/**
 * Created by : Azamat Kalmurzaev
 * 27/02/26
 */
@Composable
fun AppNavigation(
    viewModel: MainViewModel = hiltViewModel()
) {
    val isOnboardingCompleted by viewModel.isOnboardingCompleted.collectAsStateWithLifecycle()

    if (isOnboardingCompleted == false) {
        OnboardingScreen(
            onFinishClick = { viewModel.completeOnboarding() },
            onSkipClick = { viewModel.completeOnboarding() }
        )
    } else {
        val navController = rememberNavController()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            NavHost(
                navController = navController,
                startDestination = PomodoroRoute,
                modifier = Modifier.fillMaxSize()
            ) {
                composable<PomodoroRoute> {
                    PomodoroScreen()
                }
                composable<TasksRoute> {
                    TasksScreen(navController = navController)
                }
                composable<StatisticsRoute> {
                    StatisticsScreen()
                }
                composable<SettingsRoute> {
                    SettingsScreen(navController = navController)
                }
                composable<TimerSettingsRoute> {
                    TimerIntervalsScreen(navController = navController)
                }
            }

            AppBottomNavigationBar(
                navController = navController,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}