package com.example.efishapp.feature.setting.domain.repository

import com.example.efishapp.feature.setting.domain.model.AppTheme
import kotlinx.coroutines.flow.Flow

// khai báo mấy cái sườn để thằng khác theo đó mà làm
interface SettingRepository {
    fun getTheme(): Flow<AppTheme>
    suspend fun saveTheme(theme: AppTheme)
}