package com.example.efishapp.feature.profile.domain.model

data class UserProfile(
    val userId: String = "",
    val email: String = "",
    val fullName: String = "", // Chỉ dùng 1 trường tên duy nhất
    val dateOfBirth: String = "",
    val gender: String = "",
    val goal: String = "",
    val level: String = "",
    val isCompleted: Boolean = false
)
