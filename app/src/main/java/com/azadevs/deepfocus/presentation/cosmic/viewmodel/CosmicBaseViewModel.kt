package com.azadevs.deepfocus.presentation.cosmic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azadevs.deepfocus.core.model.Resource
import com.azadevs.deepfocus.domain.model.CosmicModule
import com.azadevs.deepfocus.domain.repository.FocusStreakRepository
import com.azadevs.deepfocus.domain.usecase.DeepFocusUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Created by : Azamat Kalmurzaev
 * 27/05/2026
 */
@HiltViewModel
class CosmicBaseViewModel @Inject constructor(
    private val useCases: DeepFocusUseCases,
    private val streakRepository: FocusStreakRepository
) : ViewModel() {

    val stardust: StateFlow<Int> = useCases.getStardust()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val modules: StateFlow<List<CosmicModule>> = useCases.getCosmicModules()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _uiEvent = MutableSharedFlow<CosmicUiEvent>()
    val uiEvent: SharedFlow<CosmicUiEvent> = _uiEvent.asSharedFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun unlockModule(module: CosmicModule) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = useCases.unlockCosmicModule(module.id, module.stardustCost)) {
                is Resource.Success -> {
                    _uiEvent.emit(CosmicUiEvent.UnlockSuccess(module.emoji))
                }

                is Resource.Error -> {
                    _uiEvent.emit(CosmicUiEvent.UnlockFailed(result.error))
                }
            }
            _isLoading.value = false
        }
    }

    fun addDebugStardust() {
        viewModelScope.launch {
            streakRepository.addStardust(5000)
        }
    }
}

sealed interface CosmicUiEvent {
    data class UnlockSuccess(val moduleEmoji: String) : CosmicUiEvent
    data class UnlockFailed(val reason: String) : CosmicUiEvent
}
