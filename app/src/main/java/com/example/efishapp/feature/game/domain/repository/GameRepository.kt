package com.example.efishapp.feature.game.domain.repository

import com.example.efishapp.feature.game.domain.model.GameLevel
import com.example.efishapp.feature.game.domain.model.GameQuestion
import com.example.efishapp.feature.game.domain.model.GameStats
import com.example.efishapp.feature.vocabulary.domain.model.Vocabulary

interface GameRepository {
    suspend fun getVocabulariesForGame(folderId: String, level: GameLevel): List<Vocabulary>
    suspend fun getAllVocabulariesForGame(userId: String, level: GameLevel): List<Vocabulary>
    suspend fun generateSpellingQuestions(vocabularies: List<Vocabulary>): List<GameQuestion>
    suspend fun generateFillBlankQuestions(vocabularies: List<Vocabulary>): List<GameQuestion>
    suspend fun generateHangmanQuestions(vocabularies: List<Vocabulary>): List<GameQuestion>
    suspend fun saveGameStats(stats: GameStats): Result<Unit>
    suspend fun getGameStats(userId: String): GameStats
}
