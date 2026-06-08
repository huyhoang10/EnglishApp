package com.example.efishapp.feature.notification.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.efishapp.feature.flashcard.domain.model.Vocabulary
import com.example.efishapp.feature.flashcard.domain.usecase.FlashcardSessionResult
import com.example.efishapp.feature.flashcard.domain.usecase.GetVocabularyReviewUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReviewReminderUiState(
    val vocabularies: List<Vocabulary> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ReviewReminderViewModel @Inject constructor(
    private val getVocabularyReviewUseCase: GetVocabularyReviewUseCase,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewReminderUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadReviewVocabularies()
    }

    fun loadReviewVocabularies() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val list: List<Vocabulary>
                when (val result = getVocabularyReviewUseCase(userId)) {
                    is FlashcardSessionResult.ContinueSession -> {
                        list = result.vocabularies
                    }

                    is FlashcardSessionResult.NewSession -> {
                        list = result.vocabularies
                    }
                }
                _uiState.update { it.copy(vocabularies = list, isLoading = false)}

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
