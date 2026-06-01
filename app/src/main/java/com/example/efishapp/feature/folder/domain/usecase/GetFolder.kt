package com.example.efishapp.feature.folder.domain.usecase

import com.example.efishapp.feature.folder.domain.model.Folder
import com.example.efishapp.feature.folder.domain.repository.FolderRepository
import kotlinx.coroutines.flow.Flow

class GetFolder (
    private val folderRepository: FolderRepository
) {
    operator fun invoke(): Flow<List<Folder>> {
        return folderRepository.getFolders()
    }
}
