package com.example.efishapp.feature.flashcard.domain

import com.example.efishapp.feature.flashcard.presentation.Vocabulary


interface FlashcardRepository {
    suspend fun getVocabulariesReview(userId: String): List<Vocabulary>
    suspend fun  getFlashcardProgress(userId: String, vocabularyId: String): FlashcardProgress
    suspend fun updateFlashcardProgress(flashcardProgress: FlashcardProgress)
}