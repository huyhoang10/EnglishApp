package com.example.efishapp.feature.Auth.Domain.UseCase

import com.example.efishapp.feature.Auth.Domain.Repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<Unit>{
        if(email.isBlank()){
            return Result.failure(IllegalArgumentException("Email không được để trống"))
        }
        if(password.isBlank()){
            return Result.failure(IllegalArgumentException("Mật khẩu không được để trống"))
        }
        return repository.loginWithEmail(email, password)
    }
}