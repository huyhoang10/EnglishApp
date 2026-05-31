package com.example.efishapp.feature.Auth.Presentation

interface AuthUiState {
    // chờ
    object Idle : AuthUiState
    // đang xử lý
    object Loading : AuthUiState
    // xử lý thành công
    object Success : AuthUiState
    // xử lý thất bại
    data class Error(val message: String) : AuthUiState
}