package com.example.efishapp.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.efishapp.feature.Auth.Presentation.AuthViewModel
import com.example.efishapp.feature.Auth.Presentation.ForgotPasswordScreen
import com.example.efishapp.feature.Auth.Presentation.LoginScreen
import com.example.efishapp.feature.Auth.Presentation.RegisterScreen
import com.example.efishapp.feature.profile.domain.repository.UserProfileRepository
import com.example.efishapp.feature.profile.domain.usecase.GetProfileUseCase
import com.example.efishapp.feature.profile.domain.usecase.UpdateProfileUseCase
import com.example.efishapp.feature.profile.presentation.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EfishNavGraph(
    authViewModel: AuthViewModel,
    userProfileRepository: UserProfileRepository,
    navController: NavHostController = rememberNavController()
){
    val scope = rememberCoroutineScope()
    
    NavHost(
        navController = navController,
        startDestination = AuthScreen.LOGIN
    ){
        composable(AuthScreen.LOGIN){
            LoginScreen(
                authViewModel,
                onNavigateToRegister = {navController.navigate(AuthScreen.REGISTER)},
                onNavigateToForgotPassword = {navController.navigate(AuthScreen.FORGOT_PASSWORD)},
                onLoginSuccess = {
                    scope.launch {
                        println("DEBUG: Login successful, checking profile...")
                        try {
                            val isCompleted = userProfileRepository.isProfileCompleted()
                            println("DEBUG: isProfileCompleted = $isCompleted")
                            if (isCompleted) {
                                navController.navigate(AuthScreen.HOME) {
                                    popUpTo(AuthScreen.LOGIN) { inclusive = true }
                                }
                            } else {
                                navController.navigate(AuthScreen.PROFILE_SETUP) {
                                    popUpTo(AuthScreen.LOGIN) { inclusive = true }
                                }
                            }
                        } catch (e: Exception) {
                            println("DEBUG: Error checking profile: ${e.message}")
                            navController.navigate(AuthScreen.PROFILE_SETUP) {
                                popUpTo(AuthScreen.LOGIN) { inclusive = true }
                            }
                        }
                    }
                }
            )
        }
        
        composable(AuthScreen.PROFILE_SETUP) {
            val profileSetupViewModel = remember { ProfileSetupViewModel(userProfileRepository) }
            ProfileSetupScreen(
                viewModel = profileSetupViewModel,
                onSetupComplete = {
                    navController.navigate(AuthScreen.HOME) {
                        popUpTo(AuthScreen.PROFILE_SETUP) { inclusive = true }
                    }
                }
            )
        }

        composable(AuthScreen.HOME) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Efish App") },
                        actions = {
                            IconButton(
                                onClick = { navController.navigate("profile_detail") }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profile",
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    )
                }
            ) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text(text = "Chào mừng bạn đã tới đây")
                }
            }
        }

        composable("profile_detail") {
            val getProfileUseCase = remember { GetProfileUseCase(userProfileRepository) }
            val updateProfileUseCase = remember { UpdateProfileUseCase(userProfileRepository) }
            val profileViewModel = remember { ProfileViewModel(getProfileUseCase, updateProfileUseCase, userProfileRepository) }
            
            ProfileScreen(
                viewModel = profileViewModel,
                onNavigateBack = { navController.popBackStack() },
                onDeleteSuccess = {
                    navController.navigate(AuthScreen.LOGIN) {
                        popUpTo(AuthScreen.HOME) { inclusive = true }
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
    }
}
