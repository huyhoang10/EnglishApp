package com.example.efishapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.efishapp.feature.Auth.Presentation.AuthViewModel
import com.example.efishapp.feature.Auth.Presentation.ForgotPasswordScreen
import com.example.efishapp.feature.Auth.Presentation.LoginScreen
import com.example.efishapp.feature.Auth.Presentation.RegisterScreen
import com.example.efishapp.feature.dashboard.presentation.DashboardScreen
import com.example.efishapp.feature.flashcard.presentation.CongratulationScreen
import com.example.efishapp.feature.flashcard.presentation.FlashcardScreen
import com.example.efishapp.feature.flashcard.presentation.FlashcardViewModel

@Composable
fun EfishNavGraph(
    authViewModel: AuthViewModel,
    flashcardViewModel: FlashcardViewModel,
    navController: NavHostController = rememberNavController()
){
    NavHost(
        navController = navController,
        startDestination = Screen.HOME
    ){
        composable(Screen.LOGIN){
            LoginScreen(
                authViewModel,
                onNavigateToRegister = {navController.navigate(Screen.REGISTER)},
                onNavigateToForgotPassword = {navController.navigate(Screen.FORGOT_PASSWORD)},
                onLoginSuccess = {navController.navigate(Screen.HOME) {
                    popUpTo(Screen.LOGIN) {
                        inclusive = true}
                    }
                }
            )
        }
        composable(Screen.REGISTER) {
            RegisterScreen(
                authViewModel,
                onRegisterSuccess = {navController.navigate(Screen.LOGIN)},
                onNavigateToLogin = {navController.navigate(Screen.LOGIN)}
            )
        }
        composable(Screen.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                authViewModel,
                onNavigateBackToLogin = {navController.navigate(Screen.LOGIN)},
                onSendEmailSuccess = {}
            )
        }

        composable(Screen.HOME) {
            DashboardScreen(Modifier)
        }

        composable(Screen.FLASHCARD) {
            FlashcardScreen(
                flashcardViewModel,
                onNavigateToCongratulation = { totalRemember, totalForget ->
                    navController.navigate("congratulation_screen/$totalRemember/$totalForget")
                }
            )
        }

        composable("congratulation_screen/{remember}/{forget}") { backStackEntry ->
            val remember = backStackEntry.arguments?.getString("remember")?.toInt() ?: 0
            val forget = backStackEntry.arguments?.getString("forget")?.toInt() ?: 0

            CongratulationScreen(totalRemember = remember, totalForget = forget,
                onBackToHome = {
                navController.navigate(
                    Screen.HOME) }
            )
        }

    }
}