package com.example.efishapp.feature.setting.presentation

import com.example.efishapp.feature.setting.domain.model.AppTheme

data class SettingUiState (
    val currentTheme: AppTheme = AppTheme.LIGHT
)