package com.example.efishapp.feature.profile.domain.usecase

import com.example.efishapp.feature.profile.domain.repository.UserProfileRepository

class GetProfileUseCase(
    private val repository: UserProfileRepository
) {
    suspend operator fun invoke() = repository.getProfile()
}
