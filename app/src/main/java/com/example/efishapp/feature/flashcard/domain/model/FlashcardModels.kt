package com.example.efishapp.feature.flashcard.domain.model

import java.util.Date

data class VocabularyReview(
    val vocabularyId: String = "",
    val repetitions: Int = 0,
    val easinessFactor: Float = 2.5f,
    val intervalDays: Int = 1,
    val nextReviewDate: Date = Date()
)

data class Vocabulary(
    val id: String = "",
    val word: String = "",
    val pronunciation: String = "",
    val meaning: String = "",
    val description: String = "",
    val example: String = "",
    val collocation: String = "",
    val relatedWords: String = "",
    val note: String = "",
)
