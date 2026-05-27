package com.azadevs.deepfocus.presentation.util.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.azadevs.deepfocus.presentation.cosmic.CosmicBaseScreen
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
    Scaffold(
        bottomBar = {
            AppBottomNavigationBar(navController = navController)
        },
        containerColor = Color.Transparent
    ) { _ ->
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = PomodoroRoute
            ) {
                composable<PomodoroRoute> {
                    PomodoroScreen()
                }
                composable<StatisticsRoute> {
                    StatisticsScreen()
                }
                composable<CosmicBaseRoute> {
                    CosmicBaseScreen()
                }
                composable<SettingsRoute> {
                    SettingsScreen()
                }
            }
        }
    }
}