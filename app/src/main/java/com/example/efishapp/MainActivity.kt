package com.example.efishapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.efishapp.feature.Auth.Data.Repository.AuthRepositoryImpl
import com.example.efishapp.feature.Auth.Domain.UseCase.ForgotPasswordUseCase
import com.example.efishapp.feature.Auth.Domain.UseCase.LoginUseCase
import com.example.efishapp.feature.Auth.Domain.UseCase.LoginWithGoogleUseCase
import com.example.efishapp.feature.Auth.Domain.UseCase.RegisterUseCase
import com.example.efishapp.feature.Auth.Presentation.AuthNavGraph
import com.example.efishapp.feature.Auth.Presentation.AuthViewModel
import com.example.efishapp.feature.folder.data.repository.FolderRepositoryImpl
import com.example.efishapp.feature.folder.presentation.FolderNavGraph
import com.example.efishapp.feature.onboarding.presentation.OnboardingScreen
import com.example.efishapp.feature.vocabulary.data.repository.VocabularyRepositoryImpl
import com.example.efishapp.ui.theme.EfishAppTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val firebaseAuth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        
        val authRepository = AuthRepositoryImpl(firebaseAuth)
        val folderRepository = FolderRepositoryImpl(firestore)
        val vocabularyRepository = VocabularyRepositoryImpl(firestore)

        val loginUseCase = LoginUseCase(authRepository)
        val registerUseCase = RegisterUseCase(authRepository)
        val forgotPasswordUseCase = ForgotPasswordUseCase(authRepository)
        val loginWithGoogleUseCase = LoginWithGoogleUseCase(authRepository)

        val authViewModel = AuthViewModel(
            loginUseCase = loginUseCase,
            registerUseCase = registerUseCase,
            forgotPasswordUseCase = forgotPasswordUseCase,
            loginWithGoogleUseCase = loginWithGoogleUseCase
        )
        
        enableEdgeToEdge()
        setContent {
            EfishAppTheme {
                var showOnboarding by remember { mutableStateOf(true) }
                var showAuth by remember { mutableStateOf(false) }
                var showFolder by remember { mutableStateOf(false) }

                when {
                    showOnboarding -> {
                        OnboardingScreen(
                            onNavigateToAuth = {
                                showOnboarding = false
                                showAuth = false
                                showFolder = true
                            }
                        )
                    }
                    showAuth -> {
                        AuthNavGraph(
                            viewModel = authViewModel,
                            onAuthSuccess = {
                                showAuth = false
                                showFolder = true
                            }
                        )
                    }
                    showFolder -> {
                        FolderNavGraph(
                            folderRepository = folderRepository,
                            vocabularyRepository = vocabularyRepository
                        )
                    }
                }
            }
        }
    }
}
