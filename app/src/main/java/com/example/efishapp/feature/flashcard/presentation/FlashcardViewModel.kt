package com.example.efishapp.feature.flashcard.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlin.math.max

data class FlashcardHistorySnapshot(
    val indexWord: Int,
    val countForget: Int,
    val countRemember: Int,
    val vocabularies: List<Vocabulary>
)


@HiltViewModel
class FlashcardViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(FlashcardUiState())
    val uiState: StateFlow<FlashcardUiState> = _uiState.asStateFlow()

    private val historyStack = ArrayDeque<FlashcardHistorySnapshot>()

    fun onEvent(event: FlashcardUiEvent) {
        when (event) {
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

            FlashcardUiEvent.OnClickAgainAction -> processCardReview(quality = 0)
            FlashcardUiEvent.OnClickHardAction -> processCardReview(quality = 2)
            FlashcardUiEvent.OnClickGoodAction -> processCardReview(quality = 4)
            FlashcardUiEvent.OnClickEasyAction -> processCardReview(quality = 5)
        }
    }

    private fun processCardReview(quality: Int) {
        _uiState.update { currentState ->
            val currentIndex = currentState.indexWord
            val currentList = currentState.vocabularies

            if (currentList.isEmpty() || currentIndex !in currentList.indices) return@update currentState

            val snapshot = FlashcardHistorySnapshot(
                indexWord = currentIndex,
                countForget = currentState.countForget,
                countRemember = currentState.countRemember,
                vocabularies = currentList
            )
            historyStack.addLast(snapshot)

            val currentVocab = currentList[currentIndex]
            val updatedVocab = calculateSM2(currentVocab, quality)
            val updatedList = currentList.toMutableList().apply { set(currentIndex, updatedVocab) }

            val isCorrect = quality >= 3
            val newCountForget = if (!isCorrect) currentState.countForget + 1 else currentState.countForget
            val newCountRemember = if (isCorrect) currentState.countRemember + 1 else currentState.countRemember

            val nextIndex = currentIndex + 1
            val hasFinishedNow = nextIndex >= updatedList.size

            currentState.copy(
                vocabularies = updatedList,
                indexWord = if (hasFinishedNow) currentIndex else nextIndex,
                countForget = newCountForget,
                countRemember = newCountRemember,
                isFlipped = false,
                isShowDetail = false,
                isFinished = hasFinishedNow
            )
        }
    }

    private fun calculateSM2(vocab: Vocabulary, quality: Int): Vocabulary {
        val nextRepetitions = if (quality >= 3) vocab.repetitions + 1 else 0

        val nextInterval = when (nextRepetitions) {
            0 -> 0
            1 -> 1
            2 -> 6
            else -> max(1, (vocab.interval * vocab.easinessFactor).toInt())
        }

        val qFactor = 5 - quality
        val newEF = vocab.easinessFactor + (0.1f - qFactor * (0.08f + qFactor * 0.02f))
        val finalEF = max(1.3f, newEF)

        return vocab.copy(
            repetitions = nextRepetitions,
            interval = nextInterval,
            easinessFactor = finalEF
        )
    }

    fun resetNavigationFlag() {
        _uiState.update { it.copy(isFinished = false) }
        historyStack.clear()
    }
}