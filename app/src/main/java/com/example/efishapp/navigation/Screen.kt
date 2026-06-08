package com.example.efishapp.navigation

import kotlinx.serialization.Serializable

object Screen {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    const val PROFILE_SETUP = "profile_setup"
    const val PROFILE = "profile"
    const val HOME = "home" // Màn hình chính sau khi vào app thành công
    const val DAILY_STUDY_REMINDER = "daily_study_reminder"
    const val DUE_WORDS_REMINDER = "due_words_reminder"

    const val EMPTY_VOCABULARY = "empty_vocabulary"
    const val FOLDER = "folder"
    const val VOCABULARY = "vocabulary"
}

@Serializable
data class FlashcardScreenRoute(
    val folderId: String?
)

@Serializable
data class VocabularyScreenRoute(
    val folderId: String,
    val folderName: String
)

@Serializable
data class CongratulationScreenRoute(
    val totalRemember: Int,
    val totalForget: Int,
)

@Serializable
data object SettingScreenRoute