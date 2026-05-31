package com.example.efishapp.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.efishapp.feature.Auth.Presentation.AuthViewModel
import com.example.efishapp.feature.Auth.Presentation.ForgotPasswordScreen
import com.example.efishapp.feature.Auth.Presentation.LoginScreen
import com.example.efishapp.feature.Auth.Presentation.RegisterScreen

@Composable
fun EfishNavGraph(
    authViewModel: AuthViewModel,
    navController: NavHostController = rememberNavController()
){
    NavHost(
        navController = navController,
        startDestination = AuthScreen.LOGIN
    ){
        composable(AuthScreen.LOGIN){
            LoginScreen(
                authViewModel,
                onNavigateToRegister = {navController.navigate(AuthScreen.REGISTER)},
                onNavigateToForgotPassword = {navController.navigate(AuthScreen.FORGOT_PASSWORD)},
                onLoginSuccess = {navController.navigate(AuthScreen.HOME) {
                    popUpTo(com.example.efishapp.feature.Auth.Presentation.AuthScreen.LOGIN) {
                        inclusive = true} // Xóa màn Login khỏi lịch sử
                    }
                }
            )
        }
        composable(AuthScreen.REGISTER) {
            RegisterScreen(
                authViewModel,
                onRegisterSuccess = {navController.navigate(AuthScreen.LOGIN)},
                onNavigateToLogin = {navController.navigate(AuthScreen.LOGIN)}
            )
        }
        composable(AuthScreen.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                authViewModel,
                onNavigateBackToLogin = {navController.navigate(AuthScreen.LOGIN)},
                onSendEmailSuccess = {}
            )
        }

        composable(com.example.efishapp.feature.Auth.Presentation.AuthScreen.HOME) {
            androidx.compose.material3.Text(text = "ĐĂNG NHẬP THÀNH CÔNG! Chào mừng vào App.")
        }
    }
}