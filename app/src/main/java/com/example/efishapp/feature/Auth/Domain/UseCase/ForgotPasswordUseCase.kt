package com.example.efishapp.feature.Auth.Domain.UseCase

import com.example.efishapp.feature.Auth.Domain.Repository.AuthRepository

class ForgotPasswordUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String): Result<Unit> {
        if (email.isBlank()) {
            return Result.failure(IllegalArgumentException("Email không được để trống"))
        }
        return repository.forgotPassword(email)
    }
}