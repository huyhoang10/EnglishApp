package com.example.efishapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.efishapp.core.util.OnDeviceTTSHelper
import com.example.efishapp.feature.Auth.Presentation.AuthViewModel
import com.example.efishapp.feature.Auth.Presentation.ForgotPasswordScreen
import com.example.efishapp.feature.Auth.Presentation.LoginScreen
import com.example.efishapp.feature.Auth.Presentation.RegisterScreen
import com.example.efishapp.feature.dashboard.presentation.DashboardScreen
import com.example.efishapp.feature.dashboard.presentation.DashboardViewModel
import com.example.efishapp.feature.folder.presentation.FolderScreen
import com.example.efishapp.feature.flashcard.presentation.CongratulationScreen
import com.example.efishapp.feature.flashcard.presentation.CongratulationViewModel
import com.example.efishapp.feature.flashcard.presentation.FlashcardScreen
import com.example.efishapp.feature.flashcard.presentation.FlashcardViewModel
import com.example.efishapp.feature.flashcard.presentation.EmptyReviewScreen
import com.example.efishapp.feature.mainscreen.MainScreen
import com.example.efishapp.feature.notification.presentation.DailyStudyReminderScreen
import com.example.efishapp.feature.notification.presentation.DailyStudyReminderViewModel
import com.example.efishapp.feature.profile.presentation.ProfileScreen
import com.example.efishapp.feature.profile.presentation.ProfileSetupScreen
import com.example.efishapp.feature.profile.presentation.ProfileSetupViewModel
import com.example.efishapp.feature.profile.presentation.ProfileViewModel
import com.example.efishapp.feature.notification.presentation.ReviewReminderScreen
import com.example.efishapp.feature.notification.presentation.ReviewReminderViewModel
import com.example.efishapp.feature.vocabulary.presentation.VocabularyScreen
import com.example.efishapp.feature.vocabulary.presentation.VocabularyViewModel

@Composable
fun EfishNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
){
    val authViewModel: AuthViewModel = hiltViewModel()
    val startDestination = androidx.compose.runtime.saveable.rememberSaveable {
        authViewModel.getStartDestination()
    }
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
                },
                onNavigateToReview = {navController.navigate(FlashcardScreenRoute(null))},
                onNavigateToFolderDetail = { folderId, folderName ->
                    navController.navigate(VocabularyScreenRoute(folderId, folderName))
                }
            )
        }

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

        composable(Screen.FOLDER) {
            FolderScreen(
                onNavigateToFolderDetail = { folderId, folderName ->
                    navController.navigate(VocabularyScreenRoute(folderId, folderName))
                }
            )
        }

        composable<VocabularyScreenRoute> {
            val vocabularyViewModel: VocabularyViewModel = hiltViewModel()

            VocabularyScreen(
                viewModel = vocabularyViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }


        composable<FlashcardScreenRoute> {
            val flashcardViewModel: FlashcardViewModel = hiltViewModel()
            val context = LocalContext.current
            var isTtsReady by remember { mutableStateOf(false) }

            val onDeviceTTSHelper = remember {
                object : OnDeviceTTSHelper(context) {
                    override fun onInit(status: Int) {
                        if (status == android.speech.tts.TextToSpeech.SUCCESS) {
                            isTtsReady = true
                        }
                    }
                }
            }
            DisposableEffect(Unit) {
                onDispose { onDeviceTTSHelper.shutdown() }
            }

            FlashcardScreen(
                flashcardViewModel,
                isTtsReady = isTtsReady,
                onClickSpeech = { word ->
                    if (onDeviceTTSHelper.isReady) {
                        onDeviceTTSHelper.speak(word)
                    }
                },
                onNavigateToCongratulation = { totalRemember, totalForget ->
                    navController.navigate(CongratulationScreenRoute(totalRemember,totalForget))
                },
                onNavigateNotifyEmpty = {navController.navigate(Screen.EMPTY_VOCABULARY)}
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

        composable(Screen.EMPTY_VOCABULARY) {
            EmptyReviewScreen(
//                {
//                    navController.navigate(Screen.HOME){
//                        popUpTo(Screen.FOLDER) { inclusive = true }
//                    }
//                }
                { navController.navigate(Screen.HOME) }
            )
        }

    }
}