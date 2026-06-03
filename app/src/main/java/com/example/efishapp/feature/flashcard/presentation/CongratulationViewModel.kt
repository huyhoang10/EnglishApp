package com.example.efishapp.feature.flashcard.presentation

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.example.efishapp.navigation.CongratulationScreenRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class CongratulationUiState(
    val totalRemember: Int = 0,
    val totalForget: Int = 0,
)

sealed interface CongratulationUiEvent {
    object OnClickHome: CongratulationUiEvent
}

@HiltViewModel
class CongratulationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
){
    val routeArgs = savedStateHandle.toRoute<CongratulationScreenRoute>()
    val totalForget: Int = routeArgs.totalForget
    val totalRemember: Int = routeArgs.totalRemember
    private val _uiState = MutableStateFlow(CongratulationUiState(totalRemember, totalForget))

    val uiState: StateFlow<CongratulationUiState> = _uiState.asStateFlow()

    fun onEvent(event: CongratulationUiEvent){
        when(event){
            CongratulationUiEvent.OnClickHome -> {

            }
        }
    }

}