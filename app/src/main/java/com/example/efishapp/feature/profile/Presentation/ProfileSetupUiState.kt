package com.example.efishapp.feature.profile.presentation

data class ProfileSetupUiState(
    val fullName: String = "",
    val dateOfBirth: String = "",
    val gender: String = "",
    val goal: String = "",
    val level: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)
