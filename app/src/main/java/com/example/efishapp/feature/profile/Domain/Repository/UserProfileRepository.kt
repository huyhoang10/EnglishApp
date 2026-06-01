package com.example.efishapp.feature.profile.domain.repository

import com.example.efishapp.feature.profile.domain.model.UserProfile

interface UserProfileRepository {
    suspend fun getProfile(): UserProfile
    suspend fun updateProfile(
        fullName: String? = null,
        dateOfBirth: String? = null,
        gender: String? = null,
        goal: String? = null,
        level: String? = null
    ): Result<Unit>
    
    suspend fun isProfileCompleted(): Boolean
    suspend fun deleteUserProfile(): Result<Unit>
    suspend fun deleteFirebaseAuth(): Result<Unit>
}
