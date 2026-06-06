package com.example.efishapp.feature.profile.domain.model

data class UserProfile(
    val userId: String = "",
    val email: String = "",
    val fullName: String = "",
    val dateOfBirth: String = "",
    val gender: String = "",
    val goal: String = "",
    val level: String = "",
    val isCompleted: Boolean = false
)
