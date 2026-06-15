package com.example.efishapp.feature.Auth.Presentation

interface AuthUiState {
    // chờ
    object Idle : AuthUiState
    object Loading : AuthUiState
    object Success : AuthUiState
    object RegisterSuccessNeedVerify : AuthUiState
    object ForgotPasswordEmailSent : AuthUiState
    data class NeedAccountLinkingConfirmation(val idToken: String) : AuthUiState
    data class Error(val message: String) : AuthUiState
}