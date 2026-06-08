package com.example.efishapp.feature.Auth.Domain.UseCase

import com.example.efishapp.feature.Auth.Domain.Repository.AuthRepository
import javax.inject.Inject

class ClearSessionUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.clearSession()
    }
}