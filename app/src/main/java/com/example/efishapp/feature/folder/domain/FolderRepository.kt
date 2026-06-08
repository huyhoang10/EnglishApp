package com.example.efishapp.feature.folder.domain

import com.example.efishapp.feature.folder.domain.model.Folder

interface FolderRepository {
    suspend fun getFolders(userId: String): List<Folder>
    suspend fun createFolder(folder: Folder): Result<Unit>
    suspend fun updateFolder(folder: Folder): Result<Unit>
    suspend fun deleteFolder(folderId: String): Result<Unit>
}
