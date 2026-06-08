package com.example.efishapp.feature.folder.domain

import com.example.efishapp.feature.folder.domain.model.Folder
import javax.inject.Inject

class FolderUseCase @Inject constructor(
    private val repository: FolderRepository
) {
    suspend fun getFolders(userId: String): List<Folder> {
        return repository.getFolders(userId)
    }

    suspend fun createFolder(folder: Folder): Result<Unit> {
        return repository.createFolder(folder)
    }

    suspend fun updateFolder(folder: Folder): Result<Unit> {
        return repository.updateFolder(folder)
    }

    suspend fun deleteFolder(folderId: String): Result<Unit> {
        return repository.deleteFolder(folderId)
    }
}
