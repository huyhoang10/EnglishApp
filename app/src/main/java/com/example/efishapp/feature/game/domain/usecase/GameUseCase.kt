package com.example.efishapp.feature.game.domain.usecase

import com.example.efishapp.feature.game.domain.model.GameLevel
import com.example.efishapp.feature.game.domain.model.GameQuestion
import com.example.efishapp.feature.game.domain.model.GameSession
import com.example.efishapp.feature.game.domain.model.GameType
import com.example.efishapp.feature.game.domain.repository.GameRepository
import javax.inject.Inject

class GenerateGameSessionUseCase @Inject constructor(
    private val gameRepository: GameRepository
) {
    suspend operator fun invoke(
        userId: String,
        folderId: String?,
        gameType: GameType,
        gameLevel: GameLevel
    ): GameSession? {
        val vocabularies = if (folderId.isNullOrEmpty()) {
            gameRepository.getAllVocabulariesForGame(userId, gameLevel)
        } else {
            gameRepository.getVocabulariesForGame(folderId, gameLevel)
        }

        if (vocabularies.size < MIN_QUESTIONS) return null

        val questions = when (gameType) {
            GameType.SPELLING -> gameRepository.generateSpellingQuestions(vocabularies)
            GameType.FILL_BLANK -> gameRepository.generateFillBlankQuestions(vocabularies)
            GameType.HANGMAN -> gameRepository.generateHangmanQuestions(vocabularies)
        }

        return GameSession(
            gameType = gameType,
            gameLevel = gameLevel,
            questions = questions.take(MAX_QUESTIONS)
        )
    }

    companion object {
        private const val MIN_QUESTIONS = 3
        private const val MAX_QUESTIONS = 10
    }
}

class CheckAnswerUseCase @Inject constructor() {
    operator fun invoke(userAnswer: String, correctAnswer: String): Boolean {
        return userAnswer.trim().equals(correctAnswer, ignoreCase = true)
    }
}
