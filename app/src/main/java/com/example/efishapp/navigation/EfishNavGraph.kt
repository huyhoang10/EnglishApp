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
import com.example.efishapp.feature.dashboard.presentation.DashboardViewModel
import com.example.efishapp.feature.flashcard.presentation.CongratulationScreen
import com.example.efishapp.feature.flashcard.presentation.FlashcardScreen
import com.example.efishapp.feature.flashcard.presentation.FlashcardViewModel
import com.example.efishapp.feature.notification.presentation.DailyStudyReminderScreen
import com.example.efishapp.feature.notification.presentation.DailyStudyReminderViewModel
import com.example.efishapp.feature.profile.presentation.ProfileScreen
import com.example.efishapp.feature.profile.presentation.ProfileViewModel
import com.example.efishapp.feature.profile.presentation.ProfileSetupScreen
import com.example.efishapp.feature.profile.presentation.ProfileSetupViewModel
import com.example.efishapp.feature.notification.presentation.ReviewReminderScreen

@Composable
fun EfishNavGraph(
    authViewModel: AuthViewModel,
    flashcardViewModel: FlashcardViewModel,
    dashboardViewModel: DashboardViewModel,
    profileViewModel: ProfileViewModel,
    profileSetupViewModel: ProfileSetupViewModel,
    dailyStudyReminderViewModel: DailyStudyReminderViewModel,
    navController: NavHostController = rememberNavController()
){
    NavHost(
        navController = navController,
        startDestination = Screen.DUE_WORDS_REMINDER
    ){

        composable(Screen.LOGIN){
            LoginScreen(
                authViewModel,
                onNavigateToRegister = {navController.navigate(Screen.REGISTER)},
                onNavigateToForgotPassword = {navController.navigate(Screen.FORGOT_PASSWORD)},
                onLoginSuccess = { profileExists ->
                    if (profileExists) {
                        navController.navigate(Screen.HOME) {
                            popUpTo(Screen.LOGIN) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.PROFILE_SETUP) {
                            popUpTo(Screen.LOGIN) { inclusive = true }
                        }
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

        composable(Screen.PROFILE_SETUP) {
            ProfileSetupScreen(
                viewModel = profileSetupViewModel,
                onSetupComplete = {
                    navController.navigate(Screen.HOME) {
                        popUpTo(Screen.PROFILE_SETUP) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.PROFILE) {
            ProfileScreen(
                viewModel = profileViewModel,
                onNavigateBack = { navController.popBackStack() },
                onDeleteSuccess = {
                    navController.navigate(Screen.HOME) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.HOME) {
            DashboardScreen(dashboardViewModel, Modifier)
        }

        composable(Screen.DAILY_STUDY_REMINDER) {
            DailyStudyReminderScreen(dailyStudyReminderViewModel)
        }

        composable(Screen.DUE_WORDS_REMINDER) {
            ReviewReminderScreen(
                flashcardViewModel = flashcardViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
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