package com.example.efishapp.feature.vocabulary.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.efishapp.feature.vocabulary.domain.model.Vocabulary
import com.example.efishapp.feature.vocabulary.domain.repository.VocabularyRepository
import com.example.efishapp.feature.vocabulary.domain.usecase.CreateVocabularyUseCase
import com.example.efishapp.feature.vocabulary.domain.usecase.DeleteVocabularyUseCase
import com.example.efishapp.feature.vocabulary.domain.usecase.GetVocabulariesUseCase
import com.example.efishapp.feature.vocabulary.domain.usecase.UpdateVocabularyUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VocabularyViewModel(
    private val folderId: String,
    private val getVocabulariesUseCase: GetVocabulariesUseCase,
    private val createVocabularyUseCase: CreateVocabularyUseCase,
    private val updateVocabularyUseCase: UpdateVocabularyUseCase,
    private val deleteVocabularyUseCase: DeleteVocabularyUseCase
) : ViewModel() {

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
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
