package com.azadevs.deepfocus.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.azadevs.deepfocus.presentation.util.navigation.AppNavigation
import com.azadevs.deepfocus.presentation.util.theme.DeepFocusTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DeepFocusTheme {
                AppNavigation()
            }
        }
    }
}