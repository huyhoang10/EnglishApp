package com.example.efishapp.feature.folder.domain.repository

import com.example.efishapp.feature.folder.domain.model.Folder
import kotlinx.coroutines.flow.Flow

interface FolderRepository {
    fun getFolders(): Flow<List<Folder>>
    fun getFolderById(folderId: String): Flow<Folder?>
    suspend fun createFolder(folder: Folder): Result<String>
    suspend fun updateFolder(folder: Folder): Result<Unit>
    suspend fun deleteFolder(folderId: String): Result<Unit>
}
