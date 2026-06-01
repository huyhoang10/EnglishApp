package com.example.efishapp.feature.vocabulary.presentation

import com.example.efishapp.feature.vocabulary.domain.model.Vocabulary

data class VocabularyUiState(
    val vocabularies: List<Vocabulary> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val showVocabularyDialog: Boolean = false,
    val vocabularyToEdit: Vocabulary? = null,
    val showDeleteConfirmation: Boolean = false,
    val vocabularyToDelete: Vocabulary? = null,
    val searchQuery: String = "",
    val isSelectionMode: Boolean = false,
    val selectedVocabularyIds: Set<String> = emptySet()
)

data class VocabularyListState(
    val folderId: String = "",
    val folderName: String = "",
    val folderColor: Long = 0xFF4C58BA
)
