package com.example.efishapp.feature.folder.presentation

import com.example.efishapp.feature.folder.domain.model.Folder

data class FolderUiState(
    val folders: List<Folder> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showCreateDialog: Boolean = false,
    val folderToEdit: Folder? = null,
    val showDeleteConfirmation: Boolean = false,
    val folderToDelete: Folder? = null
)
