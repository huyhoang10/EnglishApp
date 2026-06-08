package com.example.efishapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.example.efishapp.feature.mainscreen.MainScreen
import com.example.efishapp.feature.notification.presentation.DailyStudyReminderScreen
import com.example.efishapp.feature.notification.presentation.DailyStudyReminderViewModel
import com.example.efishapp.feature.profile.presentation.ProfileScreen
import com.example.efishapp.feature.profile.presentation.ProfileSetupScreen
import com.example.efishapp.feature.profile.presentation.ProfileSetupViewModel
import com.example.efishapp.feature.profile.presentation.ProfileViewModel
import com.example.efishapp.feature.notification.presentation.ReviewReminderScreen
import com.example.efishapp.feature.notification.presentation.ReviewReminderViewModel

@Composable
fun EfishNavGraph(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
){
    val authViewModel: AuthViewModel = hiltViewModel()
    val startDestination = authViewModel.getStartDestination()
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ){
        composable(Screen.LOGIN){
            val authViewModel: AuthViewModel = hiltViewModel()
            LaunchedEffect(Unit) {
                authViewModel.clearUserSession()
            }
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
                onSendEmailSuccess = {
                    navController.navigate(Screen.LOGIN) {
                        popUpTo(Screen.FORGOT_PASSWORD) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.PROFILE_SETUP) {
            val profileSetupViewModel: ProfileSetupViewModel = hiltViewModel()
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
            val profileViewModel: ProfileViewModel = hiltViewModel()
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
            MainScreen(
                onNavigateToUserProfile = { navController.navigate(Screen.PROFILE) },
                onNavigateToNotification = { navController.navigate(Screen.DUE_WORDS_REMINDER) },
                onLogoutSuccess = {
                    navController.navigate(Screen.LOGIN) {
                        popUpTo(Screen.HOME) { inclusive = true }
                    }
                } // Đóng lambda của onLogoutSuccess đúng chỗ
            ) // Đóng hàm MainScreen đúng chỗ
        } // Đóng composable(Screen.HOME) đúng chỗ

        composable(Screen.DAILY_STUDY_REMINDER) {
            val dailyStudyReminderViewModel: DailyStudyReminderViewModel = hiltViewModel()
            DailyStudyReminderScreen(dailyStudyReminderViewModel)
        }

        composable(Screen.DUE_WORDS_REMINDER) {
            val reviewReminderViewModel: ReviewReminderViewModel = hiltViewModel()
            ReviewReminderScreen(
                viewModel = reviewReminderViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
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
                    navController.navigate(Screen.HOME) {
                        popUpTo(Screen.HOME) { inclusive = true }
                    }
                }
            )
        }
    }
}