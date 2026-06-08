package com.example.efishapp.feature.flashcard.domain.model

import com.google.firebase.firestore.DocumentId
import java.util.Date

data class VocabularyReview(
    val vocabularyId: String = "",
    val learnAt: String = "",
    val repetitions: Int = 0,
    val easinessFactor: Float = 2.5f,
    val intervalDays: Int = 1,
    val nextReviewDate: String = ""
)

data class Vocabulary(
    @DocumentId
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

enum class ActionType(val quality: Int) {
    AGAIN(0),
    HARD(1),
    GOOD(2),
    EASY(3)
}

class FlashcardSession(
    val userId: String,
    val folderId: String,
    val currentIndex: Int,
    val countForget: Int,
    val countRemember: Int,
    val vocabularies: List<Vocabulary>,
    val userAnswers: Map<String, ActionType>
)
