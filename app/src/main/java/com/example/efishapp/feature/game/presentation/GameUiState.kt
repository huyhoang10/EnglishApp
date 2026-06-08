package com.example.efishapp.feature.game.presentation

import com.example.efishapp.feature.folder.domain.model.Folder
import com.example.efishapp.feature.game.domain.model.GameLevel
import com.example.efishapp.feature.game.domain.model.GameQuestion
import com.example.efishapp.feature.game.domain.model.GameSession
import com.example.efishapp.feature.game.domain.model.GameType

data class GameUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val gameSession: GameSession? = null,
    val currentQuestion: GameQuestion? = null,
    val currentQuestionIndex: Int = 0,
    val correctAnswers: Int = 0,
    val wrongAnswers: Int = 0,
    val score: Int = 0,
    val selectedLevel: GameLevel = GameLevel.EASY,
    val selectedGameType: GameType? = null,
    val selectedFolderId: String? = null,
    val folders: List<Folder> = emptyList(),
    val isGameStarted: Boolean = false,
    val isGameCompleted: Boolean = false,
    val lastAnswerCorrect: Boolean? = null,
    val userAnswer: String = "",
    val usedLetterIndices: Set<Int> = emptySet(),
    val currentInputLetters: List<Char?> = emptyList(),
    val inputSlotToShuffledIndex: List<Int?> = emptyList(),
    val hangmanUsedLetters: Set<Char> = emptySet(),
    val hangmanWrongCount: Int = 0
)

sealed class GameEvent {
    data class SelectGameType(val gameType: GameType) : GameEvent()
    data class SelectLevel(val level: GameLevel) : GameEvent()
    data class SelectFolder(val folderId: String?) : GameEvent()
    data object StartGame : GameEvent()
    data class UpdateAnswer(val answer: String) : GameEvent()
    data class SubmitAnswer(val answer: String) : GameEvent()
    data class LetterSelected(val letter: Char, val index: Int) : GameEvent()
    data class LetterDeselected(val letter: Char, val index: Int) : GameEvent()
    data object NextQuestion : GameEvent()
    data object RetryQuestion : GameEvent()
    data object ResetGame : GameEvent()
    data object PlayTTS : GameEvent()
}
