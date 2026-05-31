package com.example.efishapp.feature.Auth.Presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// Định nghĩa các tuyến đường (Route)
object AuthScreen {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    const val HOME = "home" // Màn hình chính sau khi vào app thành công
}

@Composable
fun AuthNavGraph(
    viewModel: AuthViewModel,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = AuthScreen.LOGIN
    ) {
        // 1. Màn hình Đăng nhập
        composable(AuthScreen.LOGIN) {
            LoginScreen(
                viewModel = viewModel,
                onNavigateToRegister = { navController.navigate(AuthScreen.REGISTER) },
                onNavigateToForgotPassword = { navController.navigate(AuthScreen.FORGOT_PASSWORD) },
                onLoginSuccess = {
                    navController.navigate(AuthScreen.HOME) {
                        popUpTo(AuthScreen.LOGIN) { inclusive = true } // Xóa màn Login khỏi lịch sử
                    }
                }
            )
        }

        // 2. Màn hình Đăng ký
        composable(AuthScreen.REGISTER) {
            RegisterScreen(
                viewModel = viewModel,
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = { navController.popBackStack() } // Đăng ký xong quay lại đăng nhập
            )
        }

        // 3. Màn hình Quên mật khẩu
        composable(AuthScreen.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                viewModel = viewModel,
                onNavigateBackToLogin = { navController.popBackStack() },
                onSendEmailSuccess = { navController.popBackStack() }
            )
        }

        // 4. Màn hình Home tạm thời để test
        composable(AuthScreen.HOME) {
            androidx.compose.material3.Text(text = "ĐĂNG NHẬP THÀNH CÔNG! Chào mừng vào App.")
        }
    }
}