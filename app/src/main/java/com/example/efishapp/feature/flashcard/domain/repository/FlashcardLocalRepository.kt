package com.example.efishapp.feature.flashcard.domain.repository
import com.example.efishapp.feature.flashcard.domain.model.FlashcardSession

interface FlashcardLocalRepository {
    suspend fun saveCurrentSession(session: FlashcardSession)
    suspend fun getCurrentSession(userId: String, folderId: String): FlashcardSession?
    suspend fun clearSession(userId: String, folderId: String)
}