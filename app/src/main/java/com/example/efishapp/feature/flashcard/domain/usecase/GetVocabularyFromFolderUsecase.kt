package com.example.efishapp.feature.flashcard.domain.usecase

import com.example.efishapp.feature.flashcard.domain.repository.FlashcardRepository
import com.example.efishapp.feature.flashcard.domain.model.Vocabulary
import javax.inject.Inject

class GetVocabularyFromFolderUsecase @Inject constructor(
    private val repository: FlashcardRepository
) {
    suspend operator fun invoke(folderId: String): List<Vocabulary> {
        return repository.getVocabulariesFromFolder(folderId)
    }
}