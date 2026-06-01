package com.example.efishapp.feature.vocabulary.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.efishapp.feature.vocabulary.domain.model.Vocabulary
import com.example.efishapp.feature.vocabulary.domain.repository.VocabularyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VocabularyDetailUiState(
    val vocabulary: Vocabulary? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class VocabularyDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val vocabularyRepository: VocabularyRepository
) : ViewModel() {

    private val vocabularyId: String = savedStateHandle.get<String>("vocabularyId") ?: ""

    private val _uiState = MutableStateFlow(VocabularyDetailUiState())
    val uiState: StateFlow<VocabularyDetailUiState> = _uiState.asStateFlow()

    init {
        loadVocabulary()
    }

    private fun loadVocabulary() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            vocabularyRepository.getVocabularyById(vocabularyId)
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { vocabulary ->
                    _uiState.update { it.copy(vocabulary = vocabulary, isLoading = false) }
                }
        }
    }

    fun toggleLearned() {
        val vocabulary = _uiState.value.vocabulary ?: return
        viewModelScope.launch {
            val updatedVocabulary = vocabulary.copy(isLearned = !vocabulary.isLearned)
            vocabularyRepository.updateVocabulary(updatedVocabulary)
            _uiState.update { it.copy(vocabulary = updatedVocabulary) }
        }
    }
}
