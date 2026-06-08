package com.example.efishapp.feature.vocabulary.presentation

import com.example.efishapp.feature.vocabulary.domain.model.Vocabulary

data class VocabularyUiState(
    val vocabularies: List<Vocabulary> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,

    val addDialog: AddVocabularyDialogState = AddVocabularyDialogState(),
    val editDialog: EditVocabularyDialogState = EditVocabularyDialogState(),
    val deleteDialog: DeleteVocabularyDialogState = DeleteVocabularyDialogState()
)

data class AddVocabularyDialogState(
    val isOpen: Boolean = false,
    val word: String = "",
    val pronunciation: String = "",
    val meaning: String = "",
    val description: String = "",
    val example: String = "",
    val relatedWord: String = "",
    val collocation: String = "",
    val note: String = "",
    val audioUrl: String = "",
    val isFetchingDefinition: Boolean = false,
    val fetchedFromApi: Boolean = false,
    val error: String? = null
)

data class EditVocabularyDialogState(
    val isOpen: Boolean = false,
    val editingVocabularyId: String = "",
    val word: String = "",
    val pronunciation: String = "",
    val meaning: String = "",
    val description: String = "",
    val example: String = "",
    val relatedWord: String = "",
    val collocation: String = "",
    val note: String = "",
    val audioUrl: String = ""
)

data class DeleteVocabularyDialogState(
    val isOpen: Boolean = false,
    val vocabularyId: String = "",
    val vocabularyWord: String = ""
)

sealed interface VocabularyUiEvent {
    object LoadVocabularies : VocabularyUiEvent

    // Add dialog
    object OpenAddDialog : VocabularyUiEvent
    object CloseAddDialog : VocabularyUiEvent
    data class OnWordChange(val word: String) : VocabularyUiEvent
    data class OnMeaningChange(val meaning: String) : VocabularyUiEvent
    data class OnPronunciationChange(val pronunciation: String) : VocabularyUiEvent
    data class OnDescriptionChange(val description: String) : VocabularyUiEvent
    data class OnExampleChange(val example: String) : VocabularyUiEvent
    data class OnRelatedWordChange(val relatedWord: String) : VocabularyUiEvent
    data class OnCollocationChange(val collocation: String) : VocabularyUiEvent
    data class OnNoteChange(val note: String) : VocabularyUiEvent
    object ConfirmAdd : VocabularyUiEvent

    // Edit dialog
    data class OpenEditDialog(val vocabulary: Vocabulary) : VocabularyUiEvent
    object CloseEditDialog : VocabularyUiEvent
    object ConfirmEdit : VocabularyUiEvent
    data class OnEditWordChange(val word: String) : VocabularyUiEvent
    data class OnEditMeaningChange(val meaning: String) : VocabularyUiEvent
    data class OnEditPronunciationChange(val pronunciation: String) : VocabularyUiEvent
    data class OnEditDescriptionChange(val description: String) : VocabularyUiEvent
    data class OnEditExampleChange(val example: String) : VocabularyUiEvent
    data class OnEditRelatedWordChange(val relatedWord: String) : VocabularyUiEvent
    data class OnEditCollocationChange(val collocation: String) : VocabularyUiEvent
    data class OnEditNoteChange(val note: String) : VocabularyUiEvent

    // Delete dialog
    data class OpenDeleteDialog(val vocabulary: Vocabulary) : VocabularyUiEvent
    object CloseDeleteDialog : VocabularyUiEvent
    object ConfirmDelete : VocabularyUiEvent

    // General
    object ClearError : VocabularyUiEvent
}
