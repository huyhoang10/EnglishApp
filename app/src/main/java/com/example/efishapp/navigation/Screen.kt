package com.example.efishapp.navigation

import kotlinx.serialization.Serializable

object Screen {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    const val PROFILE_SETUP = "profile_setup"
    const val HOME = "home" // Màn hình chính sau khi vào app thành công
    const val FLASHCARD = "flashcard"
    const val DAILY_STUDY_REMINDER = "daily_study_reminder"

}

@Serializable
data class FlashcardScreenRoute(
    val flashcardSetId: String
)

@Serializable
data class CongratulationScreenRoute(
    val totalRemember: Int,
    val totalForget: Int,
)