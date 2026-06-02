package com.example.efishapp.feature.vocabulary.domain.usecase

import com.example.efishapp.feature.vocabulary.domain.model.Vocabulary
import com.example.efishapp.feature.vocabulary.domain.repository.VocabularyRepository

class CreateVocabularyUseCase(
    private val vocabularyRepository: VocabularyRepository
) {
    suspend operator fun invoke(vocabulary: Vocabulary): Result<String> {
        if (vocabulary.word.isBlank()) {
            return Result.failure(IllegalArgumentException("Từ không được trống"))
        }
        if (vocabulary.meaning.isBlank()) {
            return Result.failure(IllegalArgumentException("Nghĩa không được trống"))
        }
        return vocabularyRepository.createVocabulary(vocabulary)
    }
}
