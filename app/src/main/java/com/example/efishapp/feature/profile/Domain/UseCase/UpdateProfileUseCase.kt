package com.example.efishapp.feature.profile.domain.usecase

import com.example.efishapp.feature.profile.domain.repository.UserProfileRepository

class UpdateProfileUseCase(
    private val repository: UserProfileRepository
) {
    suspend operator fun invoke(
        fullName: String? = null,
        dateOfBirth: String? = null,
        gender: String? = null,
        goal: String? = null,
        level: String? = null
    ) = repository.updateProfile(
        fullName = fullName,
        dateOfBirth = dateOfBirth,
        gender = gender,
        goal = goal,
        level = level
    )
}
