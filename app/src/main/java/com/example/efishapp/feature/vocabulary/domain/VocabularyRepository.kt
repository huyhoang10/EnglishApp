package com.example.efishapp.feature.vocabulary.domain

import com.example.efishapp.feature.vocabulary.domain.model.Vocabulary

interface VocabularyRepository {
    suspend fun getVocabulariesByFolder(folderId: String): List<Vocabulary>
    suspend fun getVocabulary(vocabularyId: String): Vocabulary?
    suspend fun createVocabulary(vocabulary: Vocabulary): Result<Unit>
    suspend fun updateVocabulary(vocabulary: Vocabulary): Result<Unit>
    suspend fun deleteVocabulary(vocabularyId: String): Result<Unit>
    suspend fun deleteVocabulariesByFolder(folderId: String): Result<Unit>
}
