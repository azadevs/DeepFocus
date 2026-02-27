package com.azadevs.deepfocus.presentation.statistics.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azadevs.deepfocus.core.model.Resource
import com.azadevs.deepfocus.domain.model.FocusSession
import com.azadevs.deepfocus.domain.usecase.DeepFocusUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * Created by : Azamat Kalmurzaev
 * 27/02/26
 */
@HiltViewModel
class StatisticsViewModel @Inject constructor(
    useCases: DeepFocusUseCases
) : ViewModel() {

    val totalFocusMinutes: StateFlow<Int> = useCases.getTotalFocusMinutes()
        .map { resource ->
            if (resource is Resource.Success) {
                resource.data
            } else {
                0
            }
        }
        .catch {

        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val allSessions: StateFlow<List<FocusSession>> = useCases.getAllSessions()
        .map { resource ->
            if (resource is Resource.Success) {
                resource.data
            } else {
                emptyList()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}