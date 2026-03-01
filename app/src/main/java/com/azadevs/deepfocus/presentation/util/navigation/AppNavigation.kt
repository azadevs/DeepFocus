package com.azadevs.deepfocus.presentation.util.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.azadevs.deepfocus.presentation.pomodoro.PomodoroScreen
import com.azadevs.deepfocus.presentation.settings.SettingsScreen
import com.azadevs.deepfocus.presentation.statistics.StatisticsScreen

/**
 * Created by : Azamat Kalmurzaev
 * 25/02/2026
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = PomodoroRoute
    ) {
        composable<PomodoroRoute> {
            PomodoroScreen(
                onNavigateToStatistics = {
                    navController.navigate(StatisticsRoute)
                },
                onNavigateToSettings = {
                    navController.navigate(SettingsRoute)
                }
            )
        }
        composable<StatisticsRoute> {
            StatisticsScreen(
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
        composable<SettingsRoute> {
            SettingsScreen(
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
    }
}