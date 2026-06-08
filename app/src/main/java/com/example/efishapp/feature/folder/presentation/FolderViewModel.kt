package com.example.efishapp.feature.folder.presentation

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.efishapp.feature.folder.domain.FolderUseCase
import com.example.efishapp.feature.folder.domain.model.Folder
import com.example.efishapp.feature.vocabulary.domain.VocabularyRepository
import com.example.efishapp.feature.vocabulary.domain.model.Vocabulary
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    private val auth: FirebaseAuth,
    private val vocabularyRepository: VocabularyRepository,
    @ApplicationContext private val context: Context
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

            // Import / Export
            is FolderUiEvent.OpenImportDialog -> {
                _uiState.update {
                    it.copy(
                        importDialog = ImportDialogState(isOpen = true),
                        error = null
                    )
                }
            }
            is FolderUiEvent.CloseImportDialog -> {
                _uiState.update {
                    it.copy(importDialog = ImportDialogState(isOpen = false))
                }
            }
            is FolderUiEvent.OnFileSelected -> processFile(event.uri)
            is FolderUiEvent.ConfirmImport -> importVocabularies()
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

    private fun processFile(uri: Uri) {
        val fileName = getFileName(uri)
        _uiState.update {
            it.copy(
                importDialog = it.importDialog.copy(
                    selectedUri = uri,
                    fileName = fileName,
                    isProcessing = true
                )
            )
        }

        viewModelScope.launch {
            try {
                val vocabularies = parseFile(uri)
                _uiState.update {
                    it.copy(
                        importDialog = it.importDialog.copy(
                            previewVocabularies = vocabularies,
                            isProcessing = false
                        )
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        importDialog = it.importDialog.copy(
                            isProcessing = false,
                            importResult = ImportResult(
                                success = false,
                                message = "Lỗi đọc file: ${e.message}"
                            )
                        )
                    )
                }
            }
        }
    }

    private fun getFileName(uri: Uri): String {
        var name = "unknown"
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (nameIndex >= 0 && cursor.moveToFirst()) {
                name = cursor.getString(nameIndex) ?: "unknown"
            }
        }
        return name.removeSuffix(".txt")
    }

    private suspend fun parseFile(uri: Uri): List<ImportedVocabulary> {
        val vocabularies = mutableListOf<ImportedVocabulary>()
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val lines = inputStream.bufferedReader().readLines()
            for (line in lines) {
                val trimmed = line.trim()
                if (trimmed.isEmpty()) continue

                val parts = trimmed.split("|")
                vocabularies.add(
                    ImportedVocabulary(
                        word = parts.getOrNull(0)?.trim() ?: "",
                        meaning = parts.getOrNull(1)?.trim() ?: "",
                        pronunciation = parts.getOrNull(2)?.trim() ?: "",
                        example = parts.getOrNull(3)?.trim() ?: "",
                        description = parts.getOrNull(4)?.trim() ?: ""
                    )
                )
            }
        }
        return vocabularies
    }

    private fun importVocabularies() {
        val dialog = _uiState.value.importDialog
        if (dialog.previewVocabularies.isEmpty() || dialog.selectedUri == null) {
            _uiState.update {
                it.copy(
                    importDialog = it.importDialog.copy(
                        importResult = ImportResult(
                            success = false,
                            message = "Không có từ vựng nào để import"
                        )
                    )
                )
            }
            return
        }

        val folderName = dialog.fileName.ifEmpty { "Thư mục mới" }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    importDialog = it.importDialog.copy(isProcessing = true)
                )
            }

            try {
                val userId = currentUserId ?: throw Exception("Người dùng chưa đăng nhập")

                // Tạo folder mới với tên = tên file
                val newFolder = Folder(
                    name = folderName,
                    color = "#4CAF50",
                    userId = userId
                )
                folderUseCase.createFolder(newFolder).getOrThrow()

                // Lấy folder vừa tạo (lấy folder mới nhất của user)
                val folders = folderUseCase.getFolders(userId)
                val createdFolder = folders.find { it.name == folderName }
                    ?: throw Exception("Không tìm thấy thư mục vừa tạo")

                // Import từng vocabulary
                var importedCount = 0
                var skippedCount = 0

                for (vocab in dialog.previewVocabularies) {
                    if (vocab.word.isBlank()) {
                        skippedCount++
                        continue
                    }

                    val vocabulary = Vocabulary(
                        folderId = createdFolder.id,
                        word = vocab.word,
                        meaning = vocab.meaning,
                        pronunciation = vocab.pronunciation,
                        example = vocab.example,
                        description = vocab.description
                    )

                    vocabularyRepository.createVocabulary(vocabulary)
                        .onSuccess { importedCount++ }
                        .onFailure { skippedCount++ }
                }

                _uiState.update {
                    it.copy(
                        importDialog = it.importDialog.copy(
                            isProcessing = false,
                            importResult = ImportResult(
                                success = true,
                                message = "Import thành công!",
                                importedCount = importedCount,
                                skippedCount = skippedCount
                            )
                        )
                    )
                }
                loadFolders()

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        importDialog = it.importDialog.copy(
                            isProcessing = false,
                            importResult = ImportResult(
                                success = false,
                                message = "Lỗi import: ${e.message}"
                            )
                        )
                    )
                }
            }
        }
    }
}
