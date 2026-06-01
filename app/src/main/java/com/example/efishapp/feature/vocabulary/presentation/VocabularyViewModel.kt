package com.example.efishapp.feature.vocabulary.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.efishapp.feature.vocabulary.domain.model.Vocabulary
import com.example.efishapp.feature.vocabulary.domain.repository.VocabularyRepository
import com.example.efishapp.feature.vocabulary.domain.usecase.CreateVocabularyUseCase
import com.example.efishapp.feature.vocabulary.domain.usecase.DeleteVocabularyUseCase
import com.example.efishapp.feature.vocabulary.domain.usecase.GetVocabulariesUseCase
import com.example.efishapp.feature.vocabulary.domain.usecase.UpdateVocabularyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VocabularyViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val vocabularyRepository: VocabularyRepository,
    private val folderRepository: com.example.efishapp.feature.folder.domain.repository.FolderRepository,
    private val getVocabulariesUseCase: GetVocabulariesUseCase,
    private val createVocabularyUseCase: CreateVocabularyUseCase,
    private val updateVocabularyUseCase: UpdateVocabularyUseCase,
    private val deleteVocabularyUseCase: DeleteVocabularyUseCase
) : ViewModel() {

    private val folderId: String = savedStateHandle.get<String>("folderId") ?: ""

    private val _uiState = MutableStateFlow(VocabularyUiState())
    val uiState: StateFlow<VocabularyUiState> = _uiState.asStateFlow()

    init {
        loadVocabularies()
    }

    private fun loadVocabularies() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getVocabulariesUseCase(folderId)
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { vocabularies ->
                    _uiState.update { it.copy(vocabularies = vocabularies, isLoading = false) }
                }
        }
    }

    private fun updateFolderVocabularyCount() {
        viewModelScope.launch {
            folderRepository.updateVocabularyCount(folderId)
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun getFilteredVocabularies(): List<Vocabulary> {
        val query = _uiState.value.searchQuery.lowercase()
        return if (query.isBlank()) {
            _uiState.value.vocabularies
        } else {
            _uiState.value.vocabularies.filter {
                it.word.lowercase().contains(query) ||
                it.meaning.lowercase().contains(query)
            }
        }
    }

    fun showCreateVocabularyDialog() {
        _uiState.update { it.copy(showVocabularyDialog = true, vocabularyToEdit = null) }
    }

    fun showEditVocabularyDialog(vocabulary: Vocabulary) {
        _uiState.update { it.copy(showVocabularyDialog = true, vocabularyToEdit = vocabulary) }
    }

    fun hideVocabularyDialog() {
        _uiState.update { it.copy(showVocabularyDialog = false, vocabularyToEdit = null) }
    }

    fun createVocabulary(word: String, phonetic: String, meaning: String, example: String, exampleMeaning: String) {
        viewModelScope.launch {
            val vocabulary = Vocabulary(
                folderId = folderId,
                word = word,
                phonetic = phonetic,
                meaning = meaning,
                example = example,
                exampleMeaning = exampleMeaning
            )
            createVocabularyUseCase(vocabulary)
                .onSuccess {
                    hideVocabularyDialog()
                    updateFolderVocabularyCount()
                    _uiState.update { it.copy(successMessage = "Thêm từ vựng thành công") }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
        }
    }

    fun updateVocabulary(word: String, phonetic: String, meaning: String, example: String, exampleMeaning: String) {
        val currentVocabulary = _uiState.value.vocabularyToEdit ?: return
        viewModelScope.launch {
            val updatedVocabulary = currentVocabulary.copy(
                word = word,
                phonetic = phonetic,
                meaning = meaning,
                example = example,
                exampleMeaning = exampleMeaning
            )
            updateVocabularyUseCase(updatedVocabulary)
                .onSuccess {
                    hideVocabularyDialog()
                    _uiState.update { it.copy(successMessage = "Cập nhật từ vựng thành công") }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
        }
    }

    fun showDeleteConfirmation(vocabulary: Vocabulary) {
        _uiState.update { it.copy(showDeleteConfirmation = true, vocabularyToDelete = vocabulary) }
    }

    fun hideDeleteConfirmation() {
        _uiState.update { it.copy(showDeleteConfirmation = false, vocabularyToDelete = null) }
    }

    fun deleteVocabulary() {
        val vocabulary = _uiState.value.vocabularyToDelete ?: return
        viewModelScope.launch {
            deleteVocabularyUseCase(vocabulary.id)
                .onSuccess {
                    hideDeleteConfirmation()
                    updateFolderVocabularyCount()
                    _uiState.update { it.copy(successMessage = "Xóa từ vựng thành công") }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
        }
    }

    fun toggleVocabularyLearned(vocabulary: Vocabulary) {
        viewModelScope.launch {
            val updatedVocabulary = vocabulary.copy(isLearned = !vocabulary.isLearned)
            updateVocabularyUseCase(updatedVocabulary)
                .onSuccess {
                    val message = if (!vocabulary.isLearned) "Đã đánh dấu là đã học" else "Đã bỏ đánh dấu"
                    _uiState.update { it.copy(successMessage = message) }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun clearSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }

    fun toggleSelectionMode() {
        _uiState.update {
            it.copy(
                isSelectionMode = !it.isSelectionMode,
                selectedVocabularyIds = emptySet()
            )
        }
    }

    fun toggleVocabularySelection(vocabularyId: String) {
        _uiState.update {
            val newSelectedIds = if (it.selectedVocabularyIds.contains(vocabularyId)) {
                it.selectedVocabularyIds - vocabularyId
            } else {
                it.selectedVocabularyIds + vocabularyId
            }
            it.copy(selectedVocabularyIds = newSelectedIds)
        }
    }

    fun selectAll() {
        _uiState.update {
            it.copy(selectedVocabularyIds = it.vocabularies.map { vocab -> vocab.id }.toSet())
        }
    }

    fun deleteSelectedVocabularies() {
        val selectedIds = _uiState.value.selectedVocabularyIds
        if (selectedIds.isEmpty()) return

        viewModelScope.launch {
            var successCount = 0
            selectedIds.forEach { vocabId ->
                deleteVocabularyUseCase(vocabId)
                    .onSuccess { successCount++ }
            }
            updateFolderVocabularyCount()
            _uiState.update {
                it.copy(
                    isSelectionMode = false,
                    selectedVocabularyIds = emptySet(),
                    successMessage = "Đã xóa $successCount từ vựng"
                )
            }
        }
    }
}
