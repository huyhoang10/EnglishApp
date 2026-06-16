package com.example.efishapp.feature.vocabulary.domain.usecase

import com.example.efishapp.feature.vocabulary.domain.VocabularyRepository
import com.example.efishapp.feature.vocabulary.domain.model.Vocabulary
import javax.inject.Inject

class VocabularyUseCase @Inject constructor(
    private val repository: VocabularyRepository
) {
    suspend fun getVocabulariesByFolder(folderId: String): List<Vocabulary> {
        return repository.getVocabulariesByFolder(folderId)
    }

    suspend fun createVocabulary(vocabulary: Vocabulary): Result<Unit> {
        return repository.createVocabulary(vocabulary)
    }

    suspend fun updateVocabulary(vocabulary: Vocabulary): Result<Unit> {
        return repository.updateVocabulary(vocabulary)
    }

    suspend fun deleteVocabulary(vocabularyId: String): Result<Unit> {
        return repository.deleteVocabulary(vocabularyId)
    }

    suspend fun deleteVocabulariesByFolder(folderId: String): Result<Unit> {
        return repository.deleteVocabulariesByFolder(folderId)
    }
}
