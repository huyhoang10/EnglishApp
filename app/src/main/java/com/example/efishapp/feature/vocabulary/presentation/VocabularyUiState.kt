package com.example.efishapp.feature.vocabulary.presentation

import com.example.efishapp.feature.vocabulary.domain.model.Vocabulary

data class VocabularyUiState(
    val vocabularies: List<Vocabulary> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val searchQuery: String = "",
    val showVocabularyDialog: Boolean = false,
    val vocabularyToEdit: Vocabulary? = null,
    val showDeleteConfirmation: Boolean = false,
    val vocabularyToDelete: Vocabulary? = null,
    val isSelectionMode: Boolean = false,
    val selectedVocabularyIds: Set<String> = emptySet()
)
