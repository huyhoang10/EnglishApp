package com.example.efishapp.feature.profile.domain.usecase

import com.example.efishapp.feature.profile.domain.repository.UserProfileRepository
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val repository: UserProfileRepository
) {
    suspend operator fun invoke() = repository.getProfile()
}
