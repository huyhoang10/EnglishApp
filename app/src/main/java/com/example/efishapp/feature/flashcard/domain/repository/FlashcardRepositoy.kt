package com.example.efishapp.feature.flashcard.domain.repository

import com.example.efishapp.feature.flashcard.domain.model.Vocabulary
import com.example.efishapp.feature.flashcard.domain.model.VocabularyReview

interface FlashcardRepository {
    suspend fun getVocabulariesReview(userId: String): List<Vocabulary>
    suspend fun getVocabulariesFromFolder(folderId: String?): List<Vocabulary>
    suspend fun getFlashcardProgressList(
        userId: String,
        vocabularyIds: List<String>
    ): List<VocabularyReview>
    suspend fun updateFlashcardProgressList(
        userId: String,
        vocabularyReviews: List<VocabularyReview>
    )
}