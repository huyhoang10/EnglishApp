package com.example.efishapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
//import com.example.efishapp.core.designsystem.AppTheme
import com.example.efishapp.core.designsystem.EfishAppTheme
import com.google.firebase.auth.FirebaseAuth
import com.example.efishapp.feature.Auth.Data.Repository.AuthRepositoryImpl
import com.example.efishapp.feature.Auth.Domain.UseCase.ForgotPasswordUseCase
import com.example.efishapp.feature.Auth.Domain.UseCase.LoginUseCase
import com.example.efishapp.feature.Auth.Domain.UseCase.LoginWithGoogleUseCase
import com.example.efishapp.feature.Auth.Domain.UseCase.RegisterUseCase
import com.google.firebase.firestore.FirebaseFirestore
import com.example.efishapp.feature.Auth.Presentation.AuthViewModel
import com.example.efishapp.feature.dashboard.presentation.DashboardScreen
import com.example.efishapp.feature.dashboard.presentation.DashboardViewModel
import com.example.efishapp.feature.flashcard.presentation.FlashcardViewModel
import com.example.efishapp.feature.notification.presentation.DailyStudyReminderViewModel
import com.example.efishapp.navigation.EfishNavGraph
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.example.efishapp.feature.profile.data.repository.UserProfileRepositoryImpl
import com.example.efishapp.feature.profile.presentation.ProfileSetupViewModel


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val dashboardViewModel: DashboardViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            EfishAppTheme() {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DashboardScreen(dashboardViewModel, onNavigateToUserProfile = {}, onNavigateToNotification = {},
                        modifier = Modifier.padding(innerPadding)
                    )
                }
//                EfishNavGraph(
//                )

            }
        }
    }
}



