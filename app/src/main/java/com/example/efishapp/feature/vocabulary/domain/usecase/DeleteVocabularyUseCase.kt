package com.example.efishapp.feature.vocabulary.domain.usecase

import com.example.efishapp.feature.vocabulary.domain.repository.VocabularyRepository

class DeleteVocabularyUseCase(
    private val vocabularyRepository: VocabularyRepository
) {
    suspend operator fun invoke(vocabularyId: String): Result<Unit> {
        return vocabularyRepository.deleteVocabulary(vocabularyId)
    }
}