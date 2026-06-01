package com.example.efishapp.feature.profile.presentation

import com.example.efishapp.feature.profile.domain.model.UserProfile

data class ProfileUiState(
    val isLoading: Boolean = false,
    val profile: UserProfile? = null,
    val error: String? = null,
    val isEditMode: Boolean = false,
    val editedFullName: String = "",
    val editedDateOfBirth: String = "",
    val editedGender: String = "",
    val editedGoal: String = "",
    val editedLevel: String = "",
    val isDeleteSuccess: Boolean = false
)
