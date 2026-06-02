package com.example.efishapp.feature.flashcard.domain

import java.util.Date


data class FlashcardProgress(
    val userId: String = "",
    val vocabularyId: String = "",
    val repetitions: Int = 0,
    val easinessFactor: Float = 2.5f,
    val intervalDays: Int = 1,
    val nextReviewDate: Date = Date()
)