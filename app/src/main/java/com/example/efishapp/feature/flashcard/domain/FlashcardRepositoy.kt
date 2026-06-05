package com.example.efishapp.feature.flashcard.domain

import com.example.efishapp.feature.flashcard.domain.model.Vocabulary
import com.example.efishapp.feature.flashcard.domain.model.VocabularyReview

interface FlashcardRepository {
    suspend fun getVocabulariesReview(userId: String): List<Vocabulary>
    suspend fun getFlashcardProgress(userId: String, vocabularyId: String): VocabularyReview
    suspend fun updateFlashcardProgress(userId: String, vocabularyReview: VocabularyReview)
}
