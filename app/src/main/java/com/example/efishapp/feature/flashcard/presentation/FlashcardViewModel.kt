package com.example.efishapp.feature.flashcard.presentation

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.efishapp.feature.dashboard.domain.usecase.UpdateWeeklyStatsUseCase
import com.example.efishapp.feature.flashcard.domain.model.ActionType
import com.example.efishapp.feature.flashcard.domain.usecase.FlashcardSessionResult
import com.example.efishapp.feature.flashcard.domain.usecase.GetVocabularyReviewUseCase
import com.example.efishapp.feature.flashcard.domain.model.Vocabulary
import com.example.efishapp.feature.flashcard.domain.usecase.GetVocabularyFromFolder
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
    private val getVocabularyFromFolder: GetVocabularyFromFolder,
    private val getVocabularyReviewUseCase: GetVocabularyReviewUseCase,
    private val updateWeeklyStatsUseCase: UpdateWeeklyStatsUseCase,
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
        if(folderId != null){
            loadVocabularyFromFolder(folderId)
        }
        else{
            loadVocabularyReview()
        }

        if(_uiState.value.vocabularies.isEmpty()){
            _uiState.update { it.copy(isEmpty = true)}
            Log.d("DeBugEmty",_uiState.value.toString())
        }
    }

    private fun loadVocabularyReview() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }

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
                    isLoading = false
                ) }
            }
            is FlashcardSessionResult.NewSession -> {
                _uiState.update { it.copy(
                    vocabularies = result.vocabularies,
                    indexWord = 0,
                    countForget = 0,
                    countRemember = 0,
                    isLoading = false
                ) }
            }
        }
    }

    private fun loadVocabularyFromFolder(folderId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val list = getVocabularyFromFolder(folderId)
                _uiState.update {
                    it.copy(vocabularies = list, isLoading = false, isFinished = list.isEmpty())
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }


    fun onEvent(event: FlashcardUiEvent) {
        when (event) {
            is FlashcardUiEvent.LoadVocabularies -> {
                loadVocabularyFromFolder(event.userId)
            }
            FlashcardUiEvent.OnFlipCard -> {
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
        }
    }



    private fun handleUserAnswer(actionType: ActionType) {
        val currentState = _uiState.value
        val currentIndex = currentState.indexWord

        if (currentIndex >= currentState.vocabularies.size) return

        historyStack.add(FlashcardHistorySnapshot(
            indexWord = currentState.indexWord,
            countForget = currentState.countForget,
            countRemember = currentState.countRemember,
            vocabularies = currentState.vocabularies
        ))

        userActionStack.add(UserActionSnapshot(
            vocabId = currentState.vocabularies[currentState.indexWord].id,
            actionType = actionType
        ))

        viewModelScope.launch {
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
        _uiState.update { it.copy(isEmpty = false) }
        historyStack.clear()
    }

suspend fun updateUserReview() {
    if (userActionStack.isEmpty()) return

    try {
        val tasks = userActionStack.map { actionSnapshot ->
            viewModelScope.async {
                if (actionSnapshot.vocabId.isNotEmpty()) {
                    updateFlashcardProgressUseCase(
                        userId = "DuwZLdACmcWoYCqFPhbPdeKy7Mk1",
                        vocabularyId = actionSnapshot.vocabId,
                        actionType = actionSnapshot.actionType
                    )
                }
            }
        }

        updateWeeklyStatsUseCase(userId)
        tasks.awaitAll()
        userActionStack.clear()
    } catch (e: Exception) {
        Log.e("updateUserReview", "Error ${e.message}", e)
    }
}

//    fun checkAndNotifyReview(context: Context) {
//        val count = _uiState.value.vocabularies.count { it.interval <= 0 && it.repetitions > 0 }
//        if (count > 0) {
//            val intent = Intent(context, ReviewReminderReceiver::class.java).apply {
//                putExtra(ReviewReminderReceiver.EXTRA_COUNT, count)
//            }
//            context.sendBroadcast(intent)
//        }
//    }
}