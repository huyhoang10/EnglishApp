package com.example.efishapp.feature.folder.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.efishapp.feature.folder.domain.model.Folder
import com.example.efishapp.feature.folder.domain.model.Topic
import com.example.efishapp.feature.folder.domain.usecase.CreateFolder
import com.example.efishapp.feature.folder.domain.usecase.DeleteFolder
import com.example.efishapp.feature.folder.domain.usecase.GetFolder
import com.example.efishapp.feature.folder.domain.usecase.UpdateFolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FolderViewModel(
    private val getFolder: GetFolder,
    private val createFolder: CreateFolder,
    private val updateFolderUseCase: UpdateFolder,
    private val deleteFolderUseCase: DeleteFolder
) : ViewModel() {

    private val _uiState = MutableStateFlow(FolderUiState())
    val uiState: StateFlow<FolderUiState> = _uiState.asStateFlow()

    init {
        loadFolders()
    }

    private fun loadFolders() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            getFolder()
                .catch { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message
                        )
                    }
                }
                .collect { folders ->
                    _uiState.update {
                        it.copy(
                            folders = folders,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    fun showCreateDialog() {
        _uiState.update {
            it.copy(
                showCreateDialog = true,
                folderToEdit = null
            )
        }
    }

    fun showEditDialog(folder: Folder) {
        _uiState.update {
            it.copy(
                showCreateDialog = true,
                folderToEdit = folder
            )
        }
    }

    fun hideDialog() {
        _uiState.update {
            it.copy(
                showCreateDialog = false,
                folderToEdit = null
            )
        }
    }

    fun createFolder(
        name: String,
        description: String,
        topicType: Topic,
        colorHex: Long
    ) {
        viewModelScope.launch {
            val folder = Folder(
                name = name,
                description = description,
                topicType = topicType,
                colorHex = colorHex
            )

            createFolder(folder)
                .onSuccess {
                    hideDialog()
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(error = exception.message)
                    }
                }
        }
    }

    fun updateFolder(
        name: String,
        description: String,
        topicType: Topic,
        colorHex: Long
    ) {
        val currentFolder = _uiState.value.folderToEdit ?: return

        viewModelScope.launch {
            val updatedFolder = currentFolder.copy(
                name = name,
                description = description,
                topicType = topicType,
                colorHex = colorHex
            )

            updateFolderUseCase(updatedFolder)
                .onSuccess {
                    hideDialog()
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(error = exception.message)
                    }
                }
        }
    }

    fun showDeleteConfirmation(folder: Folder) {
        _uiState.update {
            it.copy(
                showDeleteConfirmation = true,
                folderToDelete = folder
            )
        }
    }

    fun hideDeleteConfirmation() {
        _uiState.update {
            it.copy(
                showDeleteConfirmation = false,
                folderToDelete = null
            )
        }
    }

    fun deleteFolder() {
        val folder = _uiState.value.folderToDelete ?: return

        viewModelScope.launch {
            deleteFolderUseCase(folder.id)
                .onSuccess {
                    hideDeleteConfirmation()
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(error = exception.message)
                    }
                }
        }
    }

    fun clearError() {
        _uiState.update {
            it.copy(error = null)
        }
    }
}
