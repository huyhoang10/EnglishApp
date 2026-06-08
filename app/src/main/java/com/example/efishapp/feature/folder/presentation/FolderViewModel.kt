package com.example.efishapp.feature.folder.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.efishapp.feature.folder.domain.FolderUseCase
import com.example.efishapp.feature.folder.domain.model.Folder
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val folderUseCase: FolderUseCase,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(FolderUiState())
    val uiState: StateFlow<FolderUiState> = _uiState.asStateFlow()

    private val currentUserId: String?
        get() = auth.currentUser?.uid

    init {
        onEvent(FolderUiEvent.LoadFolders)
    }

    fun onEvent(event: FolderUiEvent) {
        when (event) {
            is FolderUiEvent.LoadFolders -> loadFolders()
            is FolderUiEvent.RefreshFolders -> loadFolders()

            is FolderUiEvent.SetFilter -> {
                _uiState.update { it.copy(filterOption = event.option) }
                applyFilterAndSort()
            }
            is FolderUiEvent.SetSort -> {
                _uiState.update { it.copy(sortOption = event.option) }
                applyFilterAndSort()
            }

            is FolderUiEvent.OpenCreateDialog -> {
                _uiState.update {
                    it.copy(
                        createDialog = CreateDialogState(isOpen = true),
                        error = null
                    )
                }
            }
            is FolderUiEvent.CloseCreateDialog -> {
                _uiState.update {
                    it.copy(createDialog = CreateDialogState(isOpen = false))
                }
            }
            is FolderUiEvent.OpenEditDialog -> {
                _uiState.update {
                    it.copy(
                        createDialog = CreateDialogState(
                            isOpen = true,
                            isEditMode = true,
                            editingFolderId = event.folder.id,
                            name = event.folder.name,
                            color = event.folder.color.ifEmpty { "#4CAF50" }
                        ),
                        error = null
                    )
                }
            }
            is FolderUiEvent.OnDialogNameChange -> {
                _uiState.update {
                    it.copy(createDialog = it.createDialog.copy(name = event.name))
                }
            }
            is FolderUiEvent.OnDialogColorChange -> {
                _uiState.update {
                    it.copy(createDialog = it.createDialog.copy(color = event.color))
                }
            }
            is FolderUiEvent.ConfirmCreateOrUpdate -> createOrUpdateFolder()

            is FolderUiEvent.OpenDeleteDialog -> {
                _uiState.update {
                    it.copy(
                        deleteDialog = DeleteDialogState(
                            isOpen = true,
                            folderId = event.folder.id,
                            folderName = event.folder.name
                        )
                    )
                }
            }
            is FolderUiEvent.CloseDeleteDialog -> {
                _uiState.update {
                    it.copy(deleteDialog = DeleteDialogState(isOpen = false))
                }
            }
            is FolderUiEvent.ConfirmDelete -> deleteFolder()

            is FolderUiEvent.ToggleStar -> toggleStar(event.folder)

            is FolderUiEvent.ClearError -> {
                _uiState.update { it.copy(error = null) }
            }
        }
    }

    private fun loadFolders() {
        val userId = currentUserId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val folders = folderUseCase.getFolders(userId)
                _uiState.update { state ->
                    state.copy(folders = folders, isLoading = false)
                }
                applyFilterAndSort()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun applyFilterAndSort() {
        val currentState = _uiState.value
        val filtered = when (currentState.filterOption) {
            FolderFilterOption.ALL -> currentState.folders
            FolderFilterOption.STARRED -> currentState.folders.filter { it.isStarred }
        }
        val sorted = when (currentState.sortOption) {
            FolderSortOption.NEWEST -> filtered.sortedByDescending { it.createdAt }
            FolderSortOption.OLDEST -> filtered.sortedBy { it.createdAt }
            FolderSortOption.ALPHABETICAL -> filtered.sortedBy { it.name.lowercase() }
            FolderSortOption.STARRED -> filtered.sortedByDescending { it.isStarred }
        }
        _uiState.update { it.copy(displayedFolders = sorted) }
    }

    private fun createOrUpdateFolder() {
        val userId = currentUserId ?: return
        val dialog = _uiState.value.createDialog

        if (dialog.name.isBlank()) {
            _uiState.update { it.copy(error = "Tên thư mục không được để trống") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = if (dialog.isEditMode && dialog.editingFolderId != null) {
                val existingFolder = _uiState.value.folders.find { it.id == dialog.editingFolderId }
                    ?: return@launch
                val updated = existingFolder.copy(
                    name = dialog.name.trim(),
                    color = dialog.color
                )
                folderUseCase.updateFolder(updated)
            } else {
                val newFolder = Folder(
                    name = dialog.name.trim(),
                    color = dialog.color,
                    userId = userId
                )
                folderUseCase.createFolder(newFolder)
            }

            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        createDialog = CreateDialogState(isOpen = false)
                    )
                }
                loadFolders()
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun deleteFolder() {
        val folderId = _uiState.value.deleteDialog.folderId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            folderUseCase.deleteFolder(folderId)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            deleteDialog = DeleteDialogState(isOpen = false)
                        )
                    }
                    loadFolders()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    private fun toggleStar(folder: Folder) {
        viewModelScope.launch {
            val updated = folder.copy(isStarred = !folder.isStarred)
            folderUseCase.updateFolder(updated)
                .onSuccess { loadFolders() }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }
}
