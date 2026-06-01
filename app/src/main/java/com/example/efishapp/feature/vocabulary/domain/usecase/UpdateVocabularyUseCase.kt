package com.example.efishapp.feature.vocabulary.domain.usecase

import com.example.efishapp.feature.vocabulary.domain.model.Vocabulary
import com.example.efishapp.feature.vocabulary.domain.repository.VocabularyRepository

class UpdateVocabularyUseCase(
    private val vocabularyRepository: VocabularyRepository
) {
    suspend operator fun invoke(vocabulary: Vocabulary): Result<Unit> {
        if (vocabulary.word.isBlank()) {
            return Result.failure(IllegalArgumentException("Từ không được trống"))
        }
        if (vocabulary.meaning.isBlank()) {
            return Result.failure(IllegalArgumentException("Nghĩa không được trống"))
        }
        return vocabularyRepository.updateVocabulary(vocabulary)
    }
}
