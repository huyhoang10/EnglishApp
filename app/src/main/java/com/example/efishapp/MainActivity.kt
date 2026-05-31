package com.example.efishapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.auth.FirebaseAuth
import com.example.efishapp.Core.designsystem.AppTheme
import com.example.efishapp.feature.Auth.Data.Repository.AuthRepositoryImpl
import com.example.efishapp.feature.Auth.Domain.UseCase.ForgotPasswordUseCase
import com.example.efishapp.feature.Auth.Domain.UseCase.LoginUseCase
import com.example.efishapp.feature.Auth.Domain.UseCase.LoginWithGoogleUseCase
import com.example.efishapp.feature.Auth.Domain.UseCase.RegisterUseCase
import com.example.efishapp.feature.Auth.Presentation.AuthNavGraph
import com.example.efishapp.feature.Auth.Presentation.AuthViewModel

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

        setContent {
            // 4. Áp dụng Theme dùng chung từ nhánh <core>
            AppTheme {
                // 5. Kích hoạt luồng điều hướng màn hình
                AuthNavGraph(viewModel = authViewModel)
            }
        }
    }
}