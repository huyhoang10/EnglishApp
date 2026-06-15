package com.example.efishapp.feature.vocabulary.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.efishapp.feature.vocabulary.domain.model.Vocabulary
import com.example.efishapp.feature.vocabulary.domain.usecase.FetchDictionaryInfoUseCase
import com.example.efishapp.feature.vocabulary.domain.usecase.VocabularyUseCase
import com.example.efishapp.navigation.VocabularyScreenRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VocabularyViewModel @Inject constructor(
    private val vocabularyUseCase: VocabularyUseCase,
    private val fetchDictionaryInfoUseCase: FetchDictionaryInfoUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val route = savedStateHandle.toRoute<VocabularyScreenRoute>()

    val folderId: String = route.folderId
    val folderName: String = route.folderName

    private val _uiState = MutableStateFlow(VocabularyUiState())
    val uiState: StateFlow<VocabularyUiState> = _uiState.asStateFlow()

    private var dictionaryFetchJob: Job? = null

    init {
        loadVocabularies()
    }

    fun onEvent(event: VocabularyUiEvent) {
        when (event) {
            is VocabularyUiEvent.LoadVocabularies -> loadVocabularies()
            is VocabularyUiEvent.OpenAddDialog -> openAddDialog()
            is VocabularyUiEvent.CloseAddDialog -> closeAddDialog()
            is VocabularyUiEvent.OnWordChange -> {
                _uiState.update {
                    it.copy(addDialog = it.addDialog.copy(
                        word = event.word,
                        error = null,
                        fetchedFromApi = false
                    ))
                }
                dictionaryFetchJob?.cancel()
                dictionaryFetchJob = viewModelScope.launch {
                    delay(500)
                    if (event.word.isNotBlank()) {
                        fetchDictionaryInfo(event.word)
                    }
                }
            }
            is VocabularyUiEvent.OnMeaningChange -> {
                _uiState.update { it.copy(addDialog = it.addDialog.copy(meaning = event.meaning)) }
            }
            is VocabularyUiEvent.OnPronunciationChange -> {
                _uiState.update { it.copy(addDialog = it.addDialog.copy(pronunciation = event.pronunciation)) }
            }
            is VocabularyUiEvent.OnDescriptionChange -> {
                _uiState.update { it.copy(addDialog = it.addDialog.copy(description = event.description)) }
            }
            is VocabularyUiEvent.OnExampleChange -> {
                _uiState.update { it.copy(addDialog = it.addDialog.copy(example = event.example)) }
            }
            is VocabularyUiEvent.OnRelatedWordChange -> {
                _uiState.update { it.copy(addDialog = it.addDialog.copy(relatedWord = event.relatedWord)) }
            }
            is VocabularyUiEvent.OnCollocationChange -> {
                _uiState.update { it.copy(addDialog = it.addDialog.copy(collocation = event.collocation)) }
            }
            is VocabularyUiEvent.OnNoteChange -> {
                _uiState.update { it.copy(addDialog = it.addDialog.copy(note = event.note)) }
            }
            is VocabularyUiEvent.ConfirmAdd -> createVocabulary()
            is VocabularyUiEvent.OpenEditDialog -> openEditDialog(event.vocabulary)
            is VocabularyUiEvent.CloseEditDialog -> closeEditDialog()
            is VocabularyUiEvent.ConfirmEdit -> updateVocabulary()
            is VocabularyUiEvent.OnEditWordChange -> {
                _uiState.update { it.copy(editDialog = it.editDialog.copy(word = event.word)) }
            }
            is VocabularyUiEvent.OnEditMeaningChange -> {
                _uiState.update { it.copy(editDialog = it.editDialog.copy(meaning = event.meaning)) }
            }
            is VocabularyUiEvent.OnEditPronunciationChange -> {
                _uiState.update { it.copy(editDialog = it.editDialog.copy(pronunciation = event.pronunciation)) }
            }
            is VocabularyUiEvent.OnEditDescriptionChange -> {
                _uiState.update { it.copy(editDialog = it.editDialog.copy(description = event.description)) }
            }
            is VocabularyUiEvent.OnEditExampleChange -> {
                _uiState.update { it.copy(editDialog = it.editDialog.copy(example = event.example)) }
            }
            is VocabularyUiEvent.OnEditRelatedWordChange -> {
                _uiState.update { it.copy(editDialog = it.editDialog.copy(relatedWord = event.relatedWord)) }
            }
            is VocabularyUiEvent.OnEditCollocationChange -> {
                _uiState.update { it.copy(editDialog = it.editDialog.copy(collocation = event.collocation)) }
            }
            is VocabularyUiEvent.OnEditNoteChange -> {
                _uiState.update { it.copy(editDialog = it.editDialog.copy(note = event.note)) }
            }
            is VocabularyUiEvent.OpenDeleteDialog -> openDeleteDialog(event.vocabulary)
            is VocabularyUiEvent.CloseDeleteDialog -> closeDeleteDialog()
            is VocabularyUiEvent.ConfirmDelete -> deleteVocabulary()
            is VocabularyUiEvent.ClearError -> {
                _uiState.update {
                    it.copy(
                        error = null,
                        addDialog = it.addDialog.copy(error = null)
                    )
                }
            }
        }
    }

    private fun loadVocabularies() {
        if (folderId.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val vocabularies = vocabularyUseCase.getVocabulariesByFolder(folderId)
                _uiState.update { it.copy(vocabularies = vocabularies, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun openAddDialog() {
        _uiState.update {
            it.copy(
                addDialog = AddVocabularyDialogState(isOpen = true),
                error = null
            )
        }
    }

    private fun closeAddDialog() {
        _uiState.update {
            it.copy(addDialog = AddVocabularyDialogState(isOpen = false))
        }
    }

    private fun fetchDictionaryInfo(word: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(addDialog = it.addDialog.copy(isFetchingDefinition = true)) }
            fetchDictionaryInfoUseCase(word).fold(
                onSuccess = { result ->
                    _uiState.update {
                        it.copy(
                            addDialog = it.addDialog.copy(
                                isFetchingDefinition = false,
                                pronunciation = result.pronunciation,
                                meaning = result.meaning,
                                example = result.example,
                                description = result.description,
                                relatedWord = result.relatedWords,
                                collocation = result.collocation,
                                audioUrl = result.audioUrl,
                                fetchedFromApi = true
                            )
                        )
                    }
                },
                onFailure = {
                    _uiState.update {
                        it.copy(
                            addDialog = it.addDialog.copy(
                                isFetchingDefinition = false,
                                error = "Không tìm thấy '$word' trong từ điển!"
                            )
                        )
                    }
                }
            )
        }
    }

    private fun createVocabulary() {
        val dialog = _uiState.value.addDialog
        if (dialog.word.isBlank()) {
            _uiState.update { it.copy(error = "Từ vựng không được để trống") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val vocabulary = Vocabulary(
                folderId = folderId,
                word = dialog.word.trim(),
                pronunciation = dialog.pronunciation,
                meaning = dialog.meaning,
                description = dialog.description,
                example = dialog.example,
                relatedWord = dialog.relatedWord,
                collocation = dialog.collocation,
                note = dialog.note,
                audioUrl = dialog.audioUrl
            )

            vocabularyUseCase.createVocabulary(vocabulary).fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            addDialog = AddVocabularyDialogState(isOpen = false)
                        )
                    }
                    loadVocabularies()
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
            )
        }
    }

    private fun openEditDialog(vocabulary: Vocabulary) {
        _uiState.update {
            it.copy(
                editDialog = EditVocabularyDialogState(
                    isOpen = true,
                    editingVocabularyId = vocabulary.id,
                    word = vocabulary.word,
                    pronunciation = vocabulary.pronunciation,
                    meaning = vocabulary.meaning,
                    description = vocabulary.description,
                    example = vocabulary.example,
                    relatedWord = vocabulary.relatedWord,
                    collocation = vocabulary.collocation,
                    note = vocabulary.note,
                    audioUrl = vocabulary.audioUrl
                ),
                error = null
            )
        }
    }

    private fun closeEditDialog() {
        _uiState.update {
            it.copy(editDialog = EditVocabularyDialogState(isOpen = false))
        }
    }

    private fun updateVocabulary() {
        val dialog = _uiState.value.editDialog
        if (dialog.word.isBlank()) {
            _uiState.update { it.copy(error = "Tu vung khong duoc de trong") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val vocabulary = Vocabulary(
                id = dialog.editingVocabularyId,
                folderId = folderId,
                word = dialog.word.trim(),
                pronunciation = dialog.pronunciation,
                meaning = dialog.meaning,
                description = dialog.description,
                example = dialog.example,
                relatedWord = dialog.relatedWord,
                collocation = dialog.collocation,
                note = dialog.note,
                audioUrl = dialog.audioUrl
            )

            vocabularyUseCase.updateVocabulary(vocabulary).fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            editDialog = EditVocabularyDialogState(isOpen = false)
                        )
                    }
                    loadVocabularies()
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
            )
        }
    }

    private fun openDeleteDialog(vocabulary: Vocabulary) {
        _uiState.update {
            it.copy(
                deleteDialog = DeleteVocabularyDialogState(
                    isOpen = true,
                    vocabularyId = vocabulary.id,
                    vocabularyWord = vocabulary.word
                )
            )
        }
    }

    private fun closeDeleteDialog() {
        _uiState.update {
            it.copy(deleteDialog = DeleteVocabularyDialogState(isOpen = false))
        }
    }

    private fun deleteVocabulary() {
        val vocabularyId = _uiState.value.deleteDialog.vocabularyId
        if (vocabularyId.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            vocabularyUseCase.deleteVocabulary(vocabularyId).fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            deleteDialog = DeleteVocabularyDialogState(isOpen = false)
                        )
                    }
                    loadVocabularies()
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
            )
        }
    }
}
