package com.example.efishapp.feature.notification.presentation

data class DailyReminderUiState(
    val loading: Boolean = false,
    val title: String = "EnglishApp",
    val message: String = "Time to study English!",
    val hour: Int = 20,
    val minute: Int = 0,
    val isActive: Boolean = false,
    val info: String? = null
)
