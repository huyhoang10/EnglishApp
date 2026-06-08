package com.example.efishapp.feature.Auth.Domain.UseCase

import com.example.efishapp.feature.Auth.Domain.Repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<Unit>{
        if(email.isBlank() || password.isBlank()){
            return Result.failure(IllegalArgumentException("Vui lòng nhập đầy đủ email và mật khẩu"))
        }
        return repository.loginWithEmail(email, password)
    }
}