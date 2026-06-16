package com.example.efishapp.feature.setting.data.datastore
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.efishapp.feature.setting.domain.model.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.dataStore by preferencesDataStore(name = "settings_pref")

class SettingPreferences @Inject constructor(
    private val context: Context
){
    private val themeKey = stringPreferencesKey("app_theme")

    val themeFlow: Flow<AppTheme> = context.dataStore.data.map { preferences ->
        val themeName = preferences[themeKey] ?: AppTheme.LIGHT.name
        AppTheme.valueOf(themeName)
    }

    suspend fun saveTheme(theme: AppTheme) {
        context.dataStore.edit { preferences ->
            preferences[themeKey] = theme.name }
        }
}