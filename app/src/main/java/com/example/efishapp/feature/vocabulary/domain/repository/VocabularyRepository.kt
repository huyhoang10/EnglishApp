package com.example.efishapp.feature.vocabulary.domain.repository

import com.example.efishapp.feature.vocabulary.domain.model.Vocabulary
import kotlinx.coroutines.flow.Flow

interface VocabularyRepository {
    fun getVocabulariesByFolder(folderId: String): Flow<List<Vocabulary>>
    fun getVocabularyById(vocabularyId: String): Flow<Vocabulary?>
    suspend fun createVocabulary(vocabulary: Vocabulary): Result<String>
    suspend fun updateVocabulary(vocabulary: Vocabulary): Result<Unit>
    suspend fun deleteVocabulary(vocabularyId: String): Result<Unit>
    suspend fun deleteAllVocabulariesInFolder(folderId: String): Result<Unit>
}
