package com.example.efishapp.feature.Auth.Presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

object AuthScreen {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
}

@Composable
fun AuthNavGraph(
    viewModel: AuthViewModel,
    onAuthSuccess: () -> Unit,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = AuthScreen.LOGIN
    ) {
        composable(AuthScreen.LOGIN) {
            LoginScreen(
                viewModel = viewModel,
                onNavigateToRegister = { navController.navigate(AuthScreen.REGISTER) },
                onNavigateToForgotPassword = { navController.navigate(AuthScreen.FORGOT_PASSWORD) },
                onLoginSuccess = onAuthSuccess
            )
        }

        composable(AuthScreen.REGISTER) {
            RegisterScreen(
                viewModel = viewModel,
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = onAuthSuccess
            )
        }

        composable(AuthScreen.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                viewModel = viewModel,
                onNavigateBackToLogin = { navController.popBackStack() },
                onSendEmailSuccess = { navController.popBackStack() }
            )
        }
    }
}
