package com.example.efishapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.firebase.auth.FirebaseAuth
import com.example.efishapp.feature.Auth.Data.Repository.AuthRepositoryImpl
import com.example.efishapp.feature.Auth.Domain.UseCase.ForgotPasswordUseCase
import com.example.efishapp.feature.Auth.Domain.UseCase.LoginUseCase
import com.example.efishapp.feature.Auth.Domain.UseCase.LoginWithGoogleUseCase
import com.example.efishapp.feature.Auth.Domain.UseCase.RegisterUseCase
import com.example.efishapp.feature.Auth.Presentation.AuthViewModel
import com.example.efishapp.navigation.EfishNavGraph
import com.example.efishapp.ui.theme.EfishAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. Khởi tạo tầng Data (Firebase SDK)
        val firebaseAuth = FirebaseAuth.getInstance()
        val authRepository = AuthRepositoryImpl(firebaseAuth)

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
        enableEdgeToEdge()
        setContent {
            EfishAppTheme {
//                var vocabularies by remember { mutableStateOf<List<Vocabulary>?>(null) }
//
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    // 2. Bây giờ bạn có thể truyền innerPadding và vocabularys vào đây mà không bị lỗi
//                    FlashcardScreen(
//                        innerPadding = innerPadding,
//                        vocabularies = vocabularies
//                    )
//                }
                EfishNavGraph(authViewModel = authViewModel)
            }
        }
    }
}



