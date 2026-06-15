package com.example.efishapp.feature.game.domain.model

import com.example.efishapp.feature.vocabulary.domain.model.Vocabulary

enum class GameType(val displayName: String) {
    SPELLING("Spelling Game"),
    FILL_BLANK("Fill in the Blank"),
    HANGMAN("Hangman")
}

enum class GameLevel(
    val displayName: String,
    val wordLengthRange: IntRange,
    val description: String
) {
    EASY("Dễ", 1..5, "Từ ngắn, 1-5 chữ cái"),
    MEDIUM("Trung bình", 6..8, "Từ trung bình, 6-8 chữ cái"),
    HARD("Khó", 9..15, "Từ dài, 9-15 chữ cái")
}

data class GameQuestion(
    val vocabulary: Vocabulary,
    val shuffledLetters: List<Char> = vocabulary.word.map { it.uppercaseChar() }.shuffled(),
    val originalWord: String = vocabulary.word.uppercase()
)

data class GameSession(
    val gameType: GameType,
    val gameLevel: GameLevel,
    val questions: List<GameQuestion>,
    val currentQuestionIndex: Int = 0,
    val correctAnswers: Int = 0,
    val wrongAnswers: Int = 0,
    val totalQuestions: Int = questions.size
) {
    val currentQuestion: GameQuestion?
        get() = questions.getOrNull(currentQuestionIndex)

    val isCompleted: Boolean
        get() = currentQuestionIndex >= questions.size

    val score: Int
        get() = if (totalQuestions > 0) (correctAnswers * 100) / totalQuestions else 0
}

data class GameResult(
    val gameType: GameType,
    val gameLevel: GameLevel,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val wrongAnswers: Int,
    val score: Int,
    val timestamp: Long = System.currentTimeMillis()
)

data class GameStats(
    val totalGamesPlayed: Int = 0,
    val totalCorrect: Int = 0,
    val totalWrong: Int = 0,
    val spellingGamesPlayed: Int = 0,
    val fillBlankGamesPlayed: Int = 0,
    val hangmanGamesPlayed: Int = 0,
    val spellingHighScore: Int = 0,
    val fillBlankHighScore: Int = 0,
    val hangmanHighScore: Int = 0
)
