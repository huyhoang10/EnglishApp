package com.example.efishapp.feature.flashcard.presentation

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.efishapp.feature.dashboard.domain.usecase.UpdateMonthlyAccuracyUseCase
import com.example.efishapp.feature.dashboard.domain.usecase.UpdateStreakAndActivityUseCase
import com.example.efishapp.feature.flashcard.domain.model.ActionType
import com.example.efishapp.feature.flashcard.domain.usecase.FlashcardSessionResult
import com.example.efishapp.feature.flashcard.domain.usecase.GetVocabularyReviewUseCase
import com.example.efishapp.feature.flashcard.domain.model.Vocabulary
import com.example.efishapp.feature.flashcard.domain.usecase.GetVocabularyFromFolderUsecase
import com.example.efishapp.feature.flashcard.domain.usecase.UpdateFlashcardProgressUseCase
import com.example.efishapp.navigation.FlashcardScreenRoute
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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

data class UserActionSnapshot(
    val vocabId: String,
    val actionType: ActionType
)

@HiltViewModel
class FlashcardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val updateFlashcardProgressUseCase: UpdateFlashcardProgressUseCase,
    private val updateStreakAndActivityUseCase: UpdateStreakAndActivityUseCase,
    private val updateMonthlyAccuracyUseCase: UpdateMonthlyAccuracyUseCase,
    private val getVocabularyFromFolderUsecase: GetVocabularyFromFolderUsecase,
    private val getVocabularyReviewUseCase: GetVocabularyReviewUseCase,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val userId: String = firebaseAuth.currentUser?.uid ?: "DuwZLdACmcWoYCqFPhbPdeKy7Mk1"
    val routeArgs = savedStateHandle.toRoute<FlashcardScreenRoute>()
    private val folderId: String? = routeArgs.folderId
    // Trong Kotlin
    private val _uiState = MutableStateFlow(FlashcardUiState())
    val uiState: StateFlow<FlashcardUiState> = _uiState.asStateFlow()
    private val historyStack = ArrayDeque<FlashcardHistorySnapshot>()
    private val userActionStack = ArrayDeque<UserActionSnapshot>()
    private val userAnswers = mutableMapOf<String, ActionType>()

init {
    viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, isError = false) }
        try {
            if (folderId != null) {
                // Đợi hàm suspend này chạy xong xuôi
                loadVocabularyFromFolder(folderId)
            } else {
                // Đợi hàm suspend này chạy xong xuôi
                loadVocabularyReview()
            }
        } catch (e: Exception) {
            Log.e("VocabularyViewModel", "Load Vocabulary Fail: ${e.message}", e)
            _uiState.update { it.copy(isError = true, isLoading = false) }
        }
    }
}

    private suspend fun loadVocabularyReview() {
        // Gọi UseCase xử lý phân luồng logic giữa Room và Firestore
        when (val result = getVocabularyReviewUseCase(userId)) {
            is FlashcardSessionResult.ContinueSession -> {
                // TÌNH HUỐNG HỌC DỞ: Khôi phục lại toàn bộ dữ liệu từ Room Local lên UI
                userAnswers.putAll(result.savedAnswers)
                _uiState.update { it.copy(
                    vocabularies = result.vocabularies,
                    indexWord = result.currentIndex,
                    countForget = result.countForget,
                    countRemember = result.countRemember,
                    isEmpty = result.vocabularies.isEmpty(),
                    isLoading = false // Tắt loading sau khi cập nhật trạng thái thành công
                ) }
            }
            is FlashcardSessionResult.NewSession -> {
                _uiState.update { it.copy(
                    vocabularies = result.vocabularies,
                    indexWord = 0,
                    countForget = 0,
                    countRemember = 0,
                    isEmpty = result.vocabularies.isEmpty(),
                    isLoading = false // Tắt loading sau khi cập nhật trạng thái thành công
                ) }
            }
        }
    }

    // Chuyển thành suspend fun, loại bỏ hoàn toàn viewModelScope.launch thừa ở đây
    private suspend fun loadVocabularyFromFolder(folderId: String) {
        // Gọi UseCase lấy dữ liệu bất đồng bộ từ Repo
        val list = getVocabularyFromFolderUsecase(folderId)

        _uiState.update {
            it.copy(
                vocabularies = list,
                isEmpty = list.isEmpty(),
                isFinished = false,
                isLoading = false // Tắt loading ngay tại đây khi dữ liệu đã nạp xong
            )
        }
    }

    fun onEvent(event: FlashcardUiEvent) {
        when (event) {
            is FlashcardUiEvent.OnFlipCard -> {
                _uiState.update { it.copy(isFlipped = !it.isFlipped) }
            }
            FlashcardUiEvent.OnClickDetail -> {
                _uiState.update { it.copy(isShowDetail = !it.isShowDetail) }
            }
            FlashcardUiEvent.OnClickBack -> {
                val lastSnapshot = historyStack.removeLastOrNull()
                userActionStack.removeLastOrNull()
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
                val actionType = event.actionType
                handleUserAnswer(actionType = actionType)
            }
            else -> {

            }
        }
    }

    private fun handleUserAnswer(actionType: ActionType) {
        val currentState = _uiState.value
        val currentIndex = currentState.indexWord

        if (currentIndex >= currentState.vocabularies.size) {
            _uiState.update { it.copy(isFlipped = true) }
            return
        }

        historyStack.add(FlashcardHistorySnapshot(
            indexWord = currentState.indexWord,
            countForget = currentState.countForget,
            countRemember = currentState.countRemember,
            vocabularies = currentState.vocabularies
        ))

        userActionStack.add(UserActionSnapshot(
            vocabId = currentState.vocabularies[currentState.indexWord].documentId,
            actionType = actionType
        ))

        val isCorrect = (actionType != ActionType.AGAIN)
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

    fun resetNavigationFlag() {
        _uiState.update { it.copy(isFinished = false) }
        _uiState.update { it.copy(isEmpty = false) }
        _uiState.update { it.copy(isLoading = false) }
        historyStack.clear()
        userActionStack.clear()
    }
    suspend fun updateUserReview() {
        if (userActionStack.isEmpty()) return
        try {
            updateFlashcardProgressUseCase(userId, userActionStack )
        } catch (e: Exception) {
            Log.e("updateUserReview", "Error ${e.message}", e)
        }
    }

    suspend fun loadIsFinish(){
        _uiState.update { it.copy(isLoading = true) }
        updateUserReview()
        updateStreakAndActivityUseCase(userId)
        updateMonthlyAccuracyUseCase(userId)
        resetNavigationFlag()
    }

}