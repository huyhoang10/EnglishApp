package com.example.efishapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.efishapp.core.designsystem.EfishAppTheme
import com.example.efishapp.feature.Auth.Presentation.AuthViewModel
import com.example.efishapp.feature.setting.domain.model.AppTheme
import com.example.efishapp.feature.setting.presentation.SettingViewModel
import com.example.efishapp.navigation.EfishNavGraph
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.example.efishapp.feature.profile.data.repository.UserProfileRepositoryImpl
import com.example.efishapp.feature.profile.domain.usecase.GetProfileUseCase
import com.example.efishapp.feature.profile.domain.usecase.UpdateProfileUseCase

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val settingViewModel: SettingViewModel = hiltViewModel()
            val uiState by settingViewModel.uiState.collectAsState()
            val isDarkMode = uiState.currentTheme == AppTheme.DARK
            EfishAppTheme(darkTheme = isDarkMode) {
                EfishNavGraph()
            }
        }
    }
}
