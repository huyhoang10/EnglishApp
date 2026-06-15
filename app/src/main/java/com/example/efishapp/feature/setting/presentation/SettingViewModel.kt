package com.example.efishapp.feature.setting.presentation

import androidx.lifecycle.ViewModel
import com.example.efishapp.feature.setting.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.example.efishapp.feature.setting.domain.model.AppTheme
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingViewModel @Inject constructor(
    getThemeUseCase: GetThemeUseCase,
    private val saveThemeUseCase: SaveThemeUseCase
): ViewModel(){

    val uiState: StateFlow<SettingUiState> = getThemeUseCase()
        .map { theme -> SettingUiState(currentTheme = theme) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SettingUiState()
        )
    fun onThemeSelected(theme: AppTheme) {
        viewModelScope.launch{
            saveThemeUseCase(theme)
        }
    }

}