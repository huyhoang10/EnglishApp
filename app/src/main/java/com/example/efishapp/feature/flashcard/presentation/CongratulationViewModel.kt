package com.example.efishapp.feature.flashcard.presentation

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.efishapp.feature.dashboard.domain.usecase.UpdateMonthlyAccuracyUseCase
import com.example.efishapp.feature.dashboard.domain.usecase.UpdateStreakAndActivityUseCase
import com.example.efishapp.navigation.CongratulationScreenRoute
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CongratulationUiState(
    val totalRemember: Int = 0,
    val totalForget: Int = 0,
    val isLoading: Boolean = false
)

sealed interface CongratulationUiEvent {
    object loading: CongratulationUiEvent
}

@HiltViewModel
class CongratulationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    //private val updateWeeklyStatsUseCase: UpdateWeeklyStatsUseCase,
    private val updateMonthlyAccuracyUseCase: UpdateMonthlyAccuracyUseCase,
    private val updateStreakAndActivityUseCase: UpdateStreakAndActivityUseCase,
    private val firebaseAuth: FirebaseAuth

) : ViewModel() {
    val routeArgs = savedStateHandle.toRoute<CongratulationScreenRoute>()
    val totalForget: Int = routeArgs.totalForget
    val totalRemember: Int = routeArgs.totalRemember
    private val userId: String = firebaseAuth.currentUser?.uid ?: ""
    private val _uiState = MutableStateFlow(CongratulationUiState(totalRemember, totalForget))

    val uiState: StateFlow<CongratulationUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(isLoading = true) }
    }
    suspend fun updateStats(){
        //updateWeeklyStatsUseCase(userId)
        updateMonthlyAccuracyUseCase(userId)
        updateStreakAndActivityUseCase(userId)
        _uiState.update { it.copy(isLoading = false) }
    }



}