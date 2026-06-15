package com.example.efishapp.feature.setting.data.repository

import com.example.efishapp.feature.setting.data.datastore.SettingPreferences
import com.example.efishapp.feature.setting.domain.model.AppTheme
import com.example.efishapp.feature.setting.domain.repository.SettingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingRepositoryImpl @Inject constructor(
    private val settingPreferences: SettingPreferences
): SettingRepository {
    override fun getTheme(): Flow<AppTheme> = settingPreferences.themeFlow

    override suspend fun saveTheme(theme: AppTheme) {
        settingPreferences.saveTheme(theme)
    }

}