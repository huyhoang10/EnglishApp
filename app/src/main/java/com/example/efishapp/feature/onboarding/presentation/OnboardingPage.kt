package com.example.efishapp.feature.onboarding.presentation

import androidx.compose.ui.graphics.Color

data class OnboardingPage(
    val title: String,
    val description: String,
    val imageRes: Int,
    val backgroundColor: Color = Color(0xFF4C58BA)
)
