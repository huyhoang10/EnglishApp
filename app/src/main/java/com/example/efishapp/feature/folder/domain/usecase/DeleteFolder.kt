package com.example.efishapp.feature.folder.domain.usecase

import com.example.efishapp.feature.folder.domain.repository.FolderRepository
import com.example.efishapp.feature.vocabulary.domain.repository.VocabularyRepository

class DeleteFolder (
    private val folderRepository: FolderRepository,
    private val vocabularyRepository: VocabularyRepository
) {
    suspend operator fun invoke(folderId: String): Result<Unit> {
        return try {
            vocabularyRepository.deleteAllVocabulariesInFolder(folderId)
            folderRepository.deleteFolder(folderId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
