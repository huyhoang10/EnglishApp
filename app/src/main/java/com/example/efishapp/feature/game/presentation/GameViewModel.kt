package com.example.efishapp.feature.game.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.efishapp.feature.game.domain.model.GameLevel
import com.example.efishapp.feature.game.domain.model.GameType
import com.example.efishapp.feature.game.domain.usecase.CheckAnswerUseCase
import com.example.efishapp.feature.game.domain.usecase.GenerateGameSessionUseCase
import com.example.efishapp.feature.folder.domain.FolderRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val generateGameSessionUseCase: GenerateGameSessionUseCase,
    private val checkAnswerUseCase: CheckAnswerUseCase,
    private val folderRepository: FolderRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val userId: String
        get() = firebaseAuth.currentUser?.uid ?: ""

    init {
        loadFolders()
    }

    private fun loadFolders() {
        viewModelScope.launch {
            val folders = folderRepository.getFolders(userId)
            _uiState.update { it.copy(folders = folders) }
        }
    }

    fun onEvent(event: GameEvent) {
        when (event) {
            is GameEvent.SelectGameType -> selectGameType(event.gameType)
            is GameEvent.SelectLevel -> selectLevel(event.level)
            is GameEvent.SelectFolder -> selectFolder(event.folderId)
            GameEvent.StartGame -> startGame()
            is GameEvent.UpdateAnswer -> updateAnswer(event.answer)
            is GameEvent.SubmitAnswer -> submitAnswer(event.answer)
            is GameEvent.LetterSelected -> onLetterSelected(event.letter, event.index)
            is GameEvent.LetterDeselected -> onLetterDeselected(event.letter, event.index)
            GameEvent.NextQuestion -> nextQuestion()
            GameEvent.RetryQuestion -> retryQuestion()
            GameEvent.ResetGame -> resetGame()
            GameEvent.PlayTTS -> { /* handled in UI via ttsHelper directly */ }
        }
    }

    private fun selectGameType(gameType: GameType) {
        _uiState.update { it.copy(selectedGameType = gameType) }
    }

    private fun selectLevel(level: GameLevel) {
        _uiState.update { it.copy(selectedLevel = level) }
    }

    private fun selectFolder(folderId: String?) {
        _uiState.update { it.copy(selectedFolderId = folderId) }
    }

    private fun startGame() {
        val state = _uiState.value
        val gameType = state.selectedGameType ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val session = generateGameSessionUseCase(
                userId = userId,
                folderId = state.selectedFolderId,
                gameType = gameType,
                gameLevel = state.selectedLevel
            )

            if (session == null || session.questions.isEmpty()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Số lượng từ vựng chưa đủ"
                    )
                }
                return@launch
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    isGameStarted = true,
                    isGameCompleted = false,
                    gameSession = session,
                    currentQuestion = session.currentQuestion,
                    currentQuestionIndex = 0,
                    correctAnswers = 0,
                    wrongAnswers = 0,
                    score = 0,
                    userAnswer = "",
                    usedLetterIndices = emptySet(),
                    currentInputLetters = List(session.currentQuestion?.originalWord?.length ?: 0) { null },
                    inputSlotToShuffledIndex = List(session.currentQuestion?.originalWord?.length ?: 0) { null },
                    hangmanUsedLetters = emptySet(),
                    hangmanWrongCount = 0
                )
            }
        }
    }

    private fun updateAnswer(answer: String) {
        _uiState.update { it.copy(userAnswer = answer) }
    }

    private fun submitAnswer(answer: String) {
        val question = _uiState.value.currentQuestion ?: return
        val isCorrect = checkAnswerUseCase(answer, question.originalWord)

        _uiState.update {
            it.copy(
                lastAnswerCorrect = isCorrect,
                correctAnswers = if (isCorrect) it.correctAnswers + 1 else it.correctAnswers,
                wrongAnswers = if (!isCorrect) it.wrongAnswers + 1 else it.wrongAnswers
            )
        }
    }

    private fun onLetterSelected(letter: Char, index: Int) {
        val state = _uiState.value

        if (state.selectedGameType == GameType.HANGMAN) {
            if (letter in state.hangmanUsedLetters) return

            val wordLetters = state.currentQuestion?.originalWord?.toSet() ?: emptySet()
            val isCorrect = letter in wordLetters
            val newWrongCount = if (isCorrect) state.hangmanWrongCount else state.hangmanWrongCount + 1

            _uiState.update {
                it.copy(
                    hangmanUsedLetters = it.hangmanUsedLetters + letter,
                    hangmanWrongCount = newWrongCount
                )
            }

            val revealedLetters = state.hangmanUsedLetters + letter
            val allRevealed = wordLetters.all { it in revealedLetters }
            val isLose = newWrongCount >= 6

            if (allRevealed || isLose) {
                val won = allRevealed
                _uiState.update {
                    it.copy(
                        lastAnswerCorrect = won,
                        correctAnswers = if (won) it.correctAnswers + 1 else it.correctAnswers,
                        wrongAnswers = if (!won) it.wrongAnswers + 1 else it.wrongAnswers
                    )
                }
            }
        } else {
            val currentInput = state.currentInputLetters.toMutableList()
            val inputSlotMap = state.inputSlotToShuffledIndex.toMutableList()
            val firstEmptyIndex = currentInput.indexOfFirst { it == null }

            if (firstEmptyIndex != -1) {
                currentInput[firstEmptyIndex] = letter
                inputSlotMap[firstEmptyIndex] = index
                _uiState.update {
                    it.copy(
                        currentInputLetters = currentInput,
                        inputSlotToShuffledIndex = inputSlotMap,
                        userAnswer = currentInput.map { c -> c ?: '_' }.joinToString(""),
                        usedLetterIndices = it.usedLetterIndices + index
                    )
                }

                if (!currentInput.contains(null)) {
                    submitAnswer(currentInput.map { it ?: '_' }.joinToString(""))
                }
            }
        }
    }

    private fun onLetterDeselected(letter: Char, index: Int) {
        if (_uiState.value.selectedGameType == GameType.HANGMAN) return

        val state = _uiState.value
        val currentInput = state.currentInputLetters.toMutableList()
        val inputSlotMap = state.inputSlotToShuffledIndex.toMutableList()
        val slotIndex = inputSlotMap.indexOf(index)

        if (slotIndex != -1) {
            currentInput[slotIndex] = null
            inputSlotMap[slotIndex] = null
            _uiState.update {
                it.copy(
                    currentInputLetters = currentInput,
                    inputSlotToShuffledIndex = inputSlotMap,
                    userAnswer = currentInput.map { c -> c ?: '_' }.joinToString(""),
                    usedLetterIndices = it.usedLetterIndices - index
                )
            }
        }
    }

    private fun nextQuestion() {
        val state = _uiState.value
        val session = state.gameSession ?: return
        val nextIndex = state.currentQuestionIndex + 1

        if (nextIndex >= session.questions.size) {
            _uiState.update { it.copy(isGameCompleted = true) }
            return
        }

        val nextQuestion = session.questions[nextIndex]
        _uiState.update {
            it.copy(
                currentQuestionIndex = nextIndex,
                currentQuestion = nextQuestion,
                lastAnswerCorrect = null,
                userAnswer = "",
                usedLetterIndices = emptySet(),
                currentInputLetters = List(nextQuestion.originalWord.length) { null },
                inputSlotToShuffledIndex = List(nextQuestion.originalWord.length) { null },
                hangmanUsedLetters = emptySet(),
                hangmanWrongCount = 0
            )
        }
    }

    private fun retryQuestion() {
        val question = _uiState.value.currentQuestion ?: return
        _uiState.update {
            it.copy(
                lastAnswerCorrect = null,
                userAnswer = "",
                usedLetterIndices = emptySet(),
                currentInputLetters = List(question.originalWord.length) { null },
                inputSlotToShuffledIndex = List(question.originalWord.length) { null },
                hangmanUsedLetters = emptySet(),
                hangmanWrongCount = 0
            )
        }
    }

    private fun resetGame() {
        _uiState.update {
            it.copy(
                isGameStarted = false,
                isGameCompleted = false,
                gameSession = null,
                currentQuestion = null,
                currentQuestionIndex = 0,
                correctAnswers = 0,
                wrongAnswers = 0,
                score = 0,
                lastAnswerCorrect = null,
                userAnswer = "",
                usedLetterIndices = emptySet(),
                currentInputLetters = emptyList(),
                inputSlotToShuffledIndex = emptyList(),
                hangmanUsedLetters = emptySet(),
                hangmanWrongCount = 0
            )
        }
    }

    fun getResult(): Pair<Int, Int> {
        val state = _uiState.value
        return Pair(state.correctAnswers, state.wrongAnswers)
    }

    fun getScore(): Int {
        val state = _uiState.value
        val total = state.correctAnswers + state.wrongAnswers
        return if (total > 0) (state.correctAnswers * 100) / total else 0
    }
}
