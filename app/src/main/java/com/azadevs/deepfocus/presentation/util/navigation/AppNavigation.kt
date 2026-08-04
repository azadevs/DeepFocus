package com.azadevs.deepfocus.presentation.util.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.azadevs.deepfocus.presentation.MainViewModel
import com.azadevs.deepfocus.presentation.onboarding.OnboardingScreen
import com.azadevs.deepfocus.presentation.pomodoro.PomodoroScreen
import com.azadevs.deepfocus.presentation.settings.SettingsScreen
import com.azadevs.deepfocus.presentation.statistics.StatisticsScreen

import com.azadevs.deepfocus.presentation.tasks.TasksScreen

/**
 * Created by : Azamat Kalmurzaev
 * 25/02/2026
 */
@Composable
fun AppNavigation(
    viewModel: MainViewModel = hiltViewModel()
) {
    val isOnboardingCompleted by viewModel.isOnboardingCompleted.collectAsStateWithLifecycle()

    if (isOnboardingCompleted == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        )
        return
    }

    if (isOnboardingCompleted == false) {
        OnboardingScreen(
            onFinishClick = { viewModel.completeOnboarding() },
            onSkipClick = { viewModel.completeOnboarding() }
        )
    } else {
        val navController = rememberNavController()
        Scaffold(
            bottomBar = {
                AppBottomNavigationBar(navController = navController)
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = PomodoroRoute,
                modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
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
                        SettingsScreen()
                    }
                }
        }
    }
}