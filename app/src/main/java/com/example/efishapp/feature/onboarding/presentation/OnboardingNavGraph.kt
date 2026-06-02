package com.example.efishapp.feature.onboarding.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

sealed class OnboardingRoute(val route: String) {
    data object Onboarding : OnboardingRoute("onboarding")
}

@Composable
fun OnboardingNavGraph(
    onNavigateToAuth: () -> Unit,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = OnboardingRoute.Onboarding.route
    ) {
        composable(OnboardingRoute.Onboarding.route) {
            OnboardingScreen(
                onNavigateToAuth = onNavigateToAuth
            )
        }
    }
}
