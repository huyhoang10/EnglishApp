package com.example.efishapp.feature.game.data.repository

import android.util.Log
import com.example.efishapp.feature.game.domain.model.GameLevel
import com.example.efishapp.feature.game.domain.model.GameQuestion
import com.example.efishapp.feature.game.domain.model.GameStats
import com.example.efishapp.feature.game.domain.repository.GameRepository
import com.example.efishapp.feature.folder.domain.FolderRepository
import com.example.efishapp.feature.vocabulary.domain.model.Vocabulary
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val folderRepository: FolderRepository
) : GameRepository {

    private val vocabularyCollection = firestore.collection("vocabulary")

    override suspend fun getVocabulariesForGame(folderId: String, level: GameLevel): List<Vocabulary> {
        return try {
            val snapshot = vocabularyCollection
                .whereEqualTo("folderId", folderId)
                .get()
                .await()
            snapshot.toObjects(Vocabulary::class.java)
                .filter { isWordInLevel(it.word, level) }
                .shuffled()
                .take(MAX_QUESTIONS_PER_GAME)
        } catch (e: Exception) {
            Log.e("GameRepo", "getVocabulariesForGame: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun getAllVocabulariesForGame(userId: String, level: GameLevel): List<Vocabulary> {
        return try {
            val folders = folderRepository.getFolders(userId)
            val allVocab = mutableListOf<Vocabulary>()
            for (folder in folders) {
                try {
                    val snapshot = vocabularyCollection
                        .whereEqualTo("folderId", folder.id)
                        .get()
                        .await()
                    allVocab.addAll(snapshot.toObjects(Vocabulary::class.java))
                } catch (e: Exception) {
                    Log.w("GameRepo", "Error fetching vocab for folder ${folder.id}", e)
                }
            }
            allVocab
                .filter { isWordInLevel(it.word, level) }
                .shuffled()
                .take(MAX_QUESTIONS_PER_GAME)
        } catch (e: Exception) {
            Log.e("GameRepo", "getAllVocabulariesForGame: ${e.message}", e)
            emptyList()
        }
    }

    private fun isWordInLevel(word: String, level: GameLevel): Boolean {
        val cleanWord = word.replace(Regex("[^a-zA-Z]"), "")
        return cleanWord.length in level.wordLengthRange
    }

    override suspend fun generateSpellingQuestions(vocabularies: List<Vocabulary>): List<GameQuestion> {
        return vocabularies.map { vocab ->
            val letters = vocab.word.uppercase().map { it }
            val shuffled = letters.shuffled()
            GameQuestion(
                vocabulary = vocab,
                shuffledLetters = shuffled,
                originalWord = vocab.word.uppercase()
            )
        }
    }

    override suspend fun generateFillBlankQuestions(vocabularies: List<Vocabulary>): List<GameQuestion> {
        return vocabularies.map { vocab ->
            val letters = vocab.word.uppercase().map { it }
            val shuffled = letters.shuffled()
            GameQuestion(
                vocabulary = vocab,
                shuffledLetters = shuffled,
                originalWord = vocab.word.uppercase()
            )
        }
    }

    override suspend fun generateHangmanQuestions(vocabularies: List<Vocabulary>): List<GameQuestion> {
        return vocabularies.map { vocab ->
            val letters = vocab.word.uppercase().map { it }.distinct()
            val shuffled = letters.shuffled()
            GameQuestion(
                vocabulary = vocab,
                shuffledLetters = shuffled,
                originalWord = vocab.word.uppercase()
            )
        }
    }

    override suspend fun saveGameStats(stats: GameStats): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun getGameStats(userId: String): GameStats {
        return GameStats()
    }

    companion object {
        private const val MAX_QUESTIONS_PER_GAME = 10
    }
}
