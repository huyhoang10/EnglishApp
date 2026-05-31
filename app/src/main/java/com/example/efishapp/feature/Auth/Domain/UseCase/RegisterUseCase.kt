package com.example.efishapp.feature.Auth.Domain.UseCase

import com.example.efishapp.feature.Auth.Domain.Repository.AuthRepository

class RegisterUseCase(private val repository: AuthRepository) {

    private val passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[^A-Za-z0-9]).{8,}$".toRegex()

    suspend operator fun invoke(email: String, password: String): Result<Unit> {
        if (email.isBlank()) {
            return Result.failure(IllegalArgumentException("Email không được để trống"))
        }
        if (password.isBlank()) {
            return Result.failure(IllegalArgumentException("Mật khẩu không được để trống"))
        }
        if (!password.matches(passwordPattern)){
            return Result.failure(IllegalArgumentException("Mật khẩu chưa đủ mạnh")
            )
        }
        return repository.register(email, password)
    }
}