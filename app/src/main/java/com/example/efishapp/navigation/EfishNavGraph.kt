package com.example.efishapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.example.efishapp.feature.flashcard.presentation.CongratulationViewModel
import com.example.efishapp.feature.flashcard.presentation.FlashcardScreen
import com.example.efishapp.feature.flashcard.presentation.FlashcardViewModel
import com.example.efishapp.feature.notification.presentation.DailyStudyReminderScreen
import com.example.efishapp.feature.notification.presentation.DailyStudyReminderViewModel
import com.example.efishapp.feature.profile.presentation.ProfileSetupViewModel

@Composable
fun EfishNavGraph(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
){
    NavHost(
        navController = navController,
        startDestination = Screen.HOME,
        modifier = modifier
    ){
        composable(Screen.LOGIN){
            val authViewModel: AuthViewModel = hiltViewModel()
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
            val authViewModel: AuthViewModel = hiltViewModel()
            RegisterScreen(
                authViewModel,
                onRegisterSuccess = {navController.navigate(Screen.LOGIN)},
                onNavigateToLogin = {navController.navigate(Screen.LOGIN)}
            )
        }
        composable(Screen.FORGOT_PASSWORD) {
            val authViewModel: AuthViewModel = hiltViewModel()
            ForgotPasswordScreen(
                authViewModel,
                onNavigateBackToLogin = {navController.navigate(Screen.LOGIN)},
                onSendEmailSuccess = {}
            )
        }

        composable(Screen.PROFILE_SETUP) {
            val profileViewModel: ProfileSetupViewModel = hiltViewModel()
            com.example.efishapp.feature.profile.presentation.ProfileSetupScreen(
                viewModel = profileViewModel,
                onSetupComplete = {
                    navController.navigate(Screen.HOME) {
                        popUpTo(Screen.PROFILE_SETUP) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.HOME) {
            val dashboardViewModel: DashboardViewModel = hiltViewModel()
            DashboardScreen(
                dashboardViewModel,
                Modifier,
                onNavigateToUserProfile = { navController.navigate(Screen.PROFILE_SETUP) },
                onNavigateToNotification = { navController.navigate(Screen.DAILY_STUDY_REMINDER) },
            )
        }

        composable(Screen.DAILY_STUDY_REMINDER) {
            val dailyStudyReminderViewModel: DailyStudyReminderViewModel = hiltViewModel()
            DailyStudyReminderScreen(dailyStudyReminderViewModel)
        }


        composable<FlashcardScreenRoute> {
            val flashcardViewModel: FlashcardViewModel = hiltViewModel()
            FlashcardScreen(
                flashcardViewModel,
                onNavigateToCongratulation = { totalRemember, totalForget ->
                    navController.navigate(CongratulationScreenRoute(totalRemember,totalForget))
                }
            )
        }

        composable <CongratulationScreenRoute> {
            val congratulationViewModel: CongratulationViewModel = hiltViewModel()
            CongratulationScreen(
                congratulationViewModel,
                onBackToHome = {
                }
            )
        }
    }
}