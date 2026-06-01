package com.example.efishapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.google.firebase.auth.FirebaseAuth
import com.example.efishapp.feature.Auth.Data.Repository.AuthRepositoryImpl
import com.example.efishapp.feature.Auth.Domain.UseCase.ForgotPasswordUseCase
import com.example.efishapp.feature.Auth.Domain.UseCase.LoginUseCase
import com.example.efishapp.feature.Auth.Domain.UseCase.LoginWithGoogleUseCase
import com.example.efishapp.feature.Auth.Domain.UseCase.RegisterUseCase
import com.google.firebase.firestore.FirebaseFirestore
import com.example.efishapp.feature.Auth.Presentation.AuthNavGraph
import com.example.efishapp.feature.Auth.Presentation.AuthViewModel
import com.example.efishapp.feature.dashboard.presentation.DashboardViewModel
import com.example.efishapp.feature.flashcard.presentation.FlashcardScreen
import com.example.efishapp.feature.flashcard.presentation.Vocabulary
import com.example.efishapp.feature.flashcard.presentation.FlashcardViewModel
import com.example.efishapp.navigation.EfishNavGraph
import com.example.efishapp.feature.folder.presentation.FolderNavGraph
import com.example.efishapp.feature.onboarding.presentation.OnboardingScreen
import com.example.efishapp.ui.theme.EfishAppTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.example.efishapp.feature.profile.data.repository.UserProfileRepositoryImpl


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. Khởi tạo tầng Data (Firebase SDK)
        val firebaseAuth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        val authRepository = AuthRepositoryImpl(firebaseAuth)
        val userProfileRepository = UserProfileRepositoryImpl(firebaseAuth, firestore)

        // 2. Khởi tạo tầng Domain (Các UseCases)
        val loginUseCase = LoginUseCase(authRepository)
        val registerUseCase = RegisterUseCase(authRepository)
        val forgotPasswordUseCase = ForgotPasswordUseCase(authRepository)
        val loginWithGoogleUseCase = LoginWithGoogleUseCase(authRepository)

        // 3. Khởi tạo tầng Presentation (ViewModel)
        // (Lưu ý: Cách khởi tạo trực tiếp này dùng để chạy ngay, thực tế sau này bạn nên dùng DI như Hilt/Koin)
        val authViewModel = AuthViewModel(
            loginUseCase = loginUseCase,
            registerUseCase = registerUseCase,
            forgotPasswordUseCase = forgotPasswordUseCase,
            loginWithGoogleUseCase = loginWithGoogleUseCase
        )
        val flashcardViewModel = FlashcardViewModel()
        val dashboardUiState = DashboardViewModel()
        enableEdgeToEdge()
        setContent {
            EfishAppTheme {
//                var showOnboarding by remember { mutableStateOf(true) }
//                var showAuth by remember { mutableStateOf(false) }
//                var showFolder by remember { mutableStateOf(false) }
//
//                when {
//                    showOnboarding -> {
//                        OnboardingScreen(
//                            onNavigateToAuth = {
//                                showOnboarding = false
//                                showAuth = false
//                                showFolder = true
//                            }
//                        )
//                    }
//                    showAuth -> {
//                        AuthNavGraph(
//                            viewModel = authViewModel,
//                            onAuthSuccess = {
//                                showAuth = false
//                                showFolder = true
//                            }
//                        )
//                    }
//                    showFolder -> {
//                        FolderNavGraph()
//                    }
//                }

//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    // 2. Bây giờ bạn có thể truyền innerPadding và vocabularys vào đây mà không bị lỗi
//
//                    FlashcardScreen(
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
//                EfishNavGraph(authViewModel = authViewModel,
//                    flashcardViewModel = flashcardViewModel,
//                    dashboardViewModel = dashboardUiState)
            }
        }
    }
}



