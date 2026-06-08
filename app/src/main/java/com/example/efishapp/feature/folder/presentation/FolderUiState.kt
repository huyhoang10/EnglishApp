package com.example.efishapp.feature.folder.presentation

import com.example.efishapp.feature.folder.domain.model.Folder

enum class FolderFilterOption { ALL, STARRED }
enum class FolderSortOption { NEWEST, OLDEST, ALPHABETICAL, STARRED }

data class FolderUiState(
    val folders: List<Folder> = emptyList(),
    val displayedFolders: List<Folder> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,

    val filterOption: FolderFilterOption = FolderFilterOption.ALL,
    val sortOption: FolderSortOption = FolderSortOption.NEWEST,

    val createDialog: CreateDialogState = CreateDialogState(),
    val deleteDialog: DeleteDialogState = DeleteDialogState()
)

data class CreateDialogState(
    val isOpen: Boolean = false,
    val isEditMode: Boolean = false,
    val editingFolderId: String? = null,
    val name: String = "",
    val description: String = "",
    val color: String = "#4CAF50"
)

data class DeleteDialogState(
    val isOpen: Boolean = false,
    val folderId: String? = null,
    val folderName: String = ""
)


sealed interface FolderUiEvent {
    object LoadFolders : FolderUiEvent
    object RefreshFolders : FolderUiEvent

    // Filter & Sort
    data class SetFilter(val option: FolderFilterOption) : FolderUiEvent
    data class SetSort(val option: FolderSortOption) : FolderUiEvent

    // Create / Edit dialog
    object OpenCreateDialog : FolderUiEvent
    object CloseCreateDialog : FolderUiEvent
    data class OpenEditDialog(val folder: Folder) : FolderUiEvent
    data class OnDialogNameChange(val name: String) : FolderUiEvent
    data class OnDialogColorChange(val color: String) : FolderUiEvent
    object ConfirmCreateOrUpdate : FolderUiEvent

    // Delete dialog
    data class OpenDeleteDialog(val folder: Folder) : FolderUiEvent
    object CloseDeleteDialog : FolderUiEvent
    object ConfirmDelete : FolderUiEvent

    // Folder actions
    data class ToggleStar(val folder: Folder) : FolderUiEvent

    // General
    object ClearError : FolderUiEvent
}