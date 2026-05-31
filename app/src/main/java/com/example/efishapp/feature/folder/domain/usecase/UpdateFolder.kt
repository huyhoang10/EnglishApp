package com.example.efishapp.feature.folder.domain.usecase

import com.example.efishapp.feature.folder.domain.model.Folder
import com.example.efishapp.feature.folder.domain.repository.FolderRepository

class UpdateFolder (
    private val folderRepository: FolderRepository
) {
    suspend operator fun invoke(folder: Folder): Result<Unit> {
        if (folder.name.isBlank()) {
            return Result.failure(IllegalArgumentException("Tên thư mục không được trống"))
        }
        return folderRepository.updateFolder(folder)
    }
}
