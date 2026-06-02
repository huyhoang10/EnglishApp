package com.example.efishapp.feature.flashcard.presentation

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.efishapp.R
import com.example.efishapp.feature.notification.alarm.ReviewReminderReceiver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.efishapp.feature.flashcard.domain.ActionType
import com.example.efishapp.feature.flashcard.domain.GetVocabularyReviewUseCase
import com.example.efishapp.feature.flashcard.domain.UpdateFlashcardProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject



data class FlashcardHistorySnapshot(
    val indexWord: Int,
    val countForget: Int,
    val countRemember: Int,
    val vocabularies: List<Vocabulary>
)


@HiltViewModel
class FlashcardViewModel @Inject constructor(
    private val getVocabularyReviewUseCase: GetVocabularyReviewUseCase,
    private val updateFlashcardProgressUseCase: UpdateFlashcardProgressUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FlashcardUiState())
    val uiState: StateFlow<FlashcardUiState> = _uiState.asStateFlow()
    private val historyStack = ArrayDeque<FlashcardHistorySnapshot>()
    fun onEvent(event: FlashcardUiEvent) {
        when (event) {
            is FlashcardUiEvent.LoadVocabularies -> {
                loadReviewVocabularies(event.userId)
            }
            FlashcardUiEvent.OnFlipCard -> {
                _uiState.update { it.copy(isFlipped = !it.isFlipped) }
            }
            FlashcardUiEvent.OnClickDetail -> {
                _uiState.update { it.copy(isShowDetail = !it.isShowDetail) }
            }
            FlashcardUiEvent.OnClickBack -> {
                val lastSnapshot = historyStack.removeLastOrNull()

                if (lastSnapshot != null) {
                    _uiState.update { currentState ->
                        currentState.copy(
                            indexWord = lastSnapshot.indexWord,
                            countForget = lastSnapshot.countForget,
                            countRemember = lastSnapshot.countRemember,
                            vocabularies = lastSnapshot.vocabularies,
                            isFlipped = false,   // Reset lại giao diện thẻ phẳng
                            isShowDetail = false
                        )
                    }
                } else {
                    _uiState.update { currentState ->
                        val prevIndex = if (currentState.indexWord > 0) currentState.indexWord - 1 else 0
                        currentState.copy(
                            indexWord = prevIndex,
                            isFlipped = false,
                            isShowDetail = false
                        )
                    }
                }
            }
            is FlashcardUiEvent.OnAnswer -> {
                handleUserAnswer(userId = "CURRENT_USER_ID", actionType = event.actionType)
            }
        }
    }

    private fun loadReviewVocabularies(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val list = getVocabularyReviewUseCase(userId)
                _uiState.update {
                    it.copy(vocabularies = list, isLoading = false, isFinished = list.isEmpty())
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun handleUserAnswer(userId: String, actionType: ActionType) {
        val currentState = _uiState.value
        val currentIndex = currentState.indexWord

        if (currentIndex >= currentState.vocabularies.size) return

        val currentVocab = currentState.vocabularies[currentIndex]

        viewModelScope.launch {
            // Tác vụ 2 & 3: Tính toán SM-2 và cập nhật xuống DB/Repository bất đồng bộ
            updateFlashcardProgressUseCase(userId, currentVocab.id, actionType)

            // Cập nhật trạng thái UI cục bộ chuyển sang từ tiếp theo
            val isCorrect = actionType != ActionType.AGAIN
            val newCountForget = if (!isCorrect) currentState.countForget + 1 else currentState.countForget
            val newCountRemember = if (isCorrect) currentState.countRemember + 1 else currentState.countRemember

            val nextIndex = currentIndex + 1
            val hasFinishedNow = nextIndex >= currentState.vocabularies.size

            _uiState.update {
                it.copy(
                    indexWord = if (hasFinishedNow) currentIndex else nextIndex,
                    countForget = newCountForget,
                    countRemember = newCountRemember,
                    isFlipped = false,
                    isShowDetail = false,
                    isFinished = hasFinishedNow
                )
            }
        }
    }

    fun resetNavigationFlag() {
        _uiState.update { it.copy(isFinished = false) }
        historyStack.clear()
    }

    fun checkAndNotifyReview(context: Context) {
        val count = _uiState.value.vocabularies.count { it.interval <= 0 && it.repetitions > 0 }
        if (count > 0) {
            val intent = Intent(context, ReviewReminderReceiver::class.java).apply {
                putExtra(ReviewReminderReceiver.EXTRA_COUNT, count)
            }
            context.sendBroadcast(intent)
        }
    }
}