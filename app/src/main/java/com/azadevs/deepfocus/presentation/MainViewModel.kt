package com.azadevs.deepfocus.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azadevs.deepfocus.domain.usecase.DeepFocusUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.azadevs.deepfocus.presentation.settings.component.AppThemeMode

/**
 * Created by Azamat on 29/05/2026.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val useCases: DeepFocusUseCases
) : ViewModel() {

    val isOnboardingCompleted: StateFlow<Boolean?> = useCases.getOnboardingCompleted()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val themeMode: StateFlow<AppThemeMode> = useCases.getThemeMode().map { modeStr ->
        try {
            AppThemeMode.valueOf(modeStr)
        } catch (e: Exception) {
            AppThemeMode.SYSTEM
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppThemeMode.SYSTEM)

    fun completeOnboarding() {
        viewModelScope.launch {
            useCases.setOnboardingCompleted(true)
        }
    }
}
