package com.example.efishapp.feature.Auth.Domain.UseCase

import com.example.efishapp.feature.Auth.Domain.Repository.AuthRepository

class LoginWithGoogleUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(idToken: String): Result<Unit> {
        if (idToken.isBlank()) {
            return Result.failure(IllegalArgumentException("Google ID Token không hợp lệ"))
        }
        return repository.loginWithGoogle(idToken)
    }
}