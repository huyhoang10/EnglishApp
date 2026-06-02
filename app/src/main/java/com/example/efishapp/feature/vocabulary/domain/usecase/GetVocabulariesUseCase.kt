package com.example.efishapp.feature.vocabulary.domain.usecase

import com.example.efishapp.feature.vocabulary.domain.model.Vocabulary
import com.example.efishapp.feature.vocabulary.domain.repository.VocabularyRepository
import kotlinx.coroutines.flow.Flow

class GetVocabulariesUseCase(
    private val vocabularyRepository: VocabularyRepository
) {
    operator fun invoke(folderId: String): Flow<List<Vocabulary>> {
        return vocabularyRepository.getVocabulariesByFolder(folderId)
    }
}
