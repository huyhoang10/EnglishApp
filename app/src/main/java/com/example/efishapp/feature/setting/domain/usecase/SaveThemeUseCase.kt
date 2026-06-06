package com.example.efishapp.feature.setting.domain.usecase

import com.example.efishapp.feature.setting.domain.model.AppTheme
import com.example.efishapp.feature.setting.domain.repository.SettingRepository
import javax.inject.Inject

class SaveThemeUseCase @Inject constructor(
    private val repository: SettingRepository
) {
    suspend operator fun invoke(theme: AppTheme) = repository.saveTheme(theme)
}