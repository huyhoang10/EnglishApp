package com.example.efishapp.feature.notification.domain.model

data class DailyStudyNotification(
    val title: String = "EnglishApp",
    val message: String = "Time to study English!",
    val hour: Int = 20,
    val minute: Int = 0,
    val isActive: Boolean = false
)