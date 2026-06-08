package com.example.efishapp.feature.vocabulary.domain.usecase

import com.example.efishapp.feature.vocabulary.data.remote.DictionaryApiService
import com.example.efishapp.feature.vocabulary.domain.model.Vocabulary
import javax.inject.Inject

data class DictionaryResult(
    val pronunciation: String,
    val audioUrl: String,
    val meaning: String,
    val example: String,
    val description: String,
    val relatedWords: String,
    val collocation: String
)

class FetchDictionaryInfoUseCase @Inject constructor(
    private val apiService: DictionaryApiService
) {
    suspend operator fun invoke(word: String): Result<DictionaryResult> {
        return try {
            val response = apiService.getWordInfo(word.trim().lowercase())
            val firstEntry = response.firstOrNull() ?: return Result.failure(
                Exception("No definition found for '$word'")
            )

            val phonetic = firstEntry.phonetic
                ?: firstEntry.phonetics?.firstOrNull { !it.text.isNullOrBlank() }?.text
                ?: ""

            val audioUrl = firstEntry.phonetics
                ?.firstOrNull { !it.audio.isNullOrBlank() }
                ?.audio
                ?: ""

            val meaning = firstEntry.meanings
                ?.firstOrNull()
                ?.definitions
                ?.firstOrNull()
                ?.definition
                ?: ""

            val example = firstEntry.meanings
                ?.firstOrNull()
                ?.definitions
                ?.firstOrNull()
                ?.example
                ?: ""

            val synonyms = firstEntry.meanings
                ?.flatMap { it.synonyms ?: emptyList() }
                ?.distinct()
                ?.take(5)
                ?.joinToString(", ")
                ?: ""

            val collocations = firstEntry.meanings
                ?.flatMap { m -> m.definitions?.flatMap { d -> d.synonyms ?: emptyList() } ?: emptyList() }
                ?.distinct()
                ?.take(3)
                ?.joinToString(", ")
                ?: ""

            Result.success(
                DictionaryResult(
                    pronunciation = phonetic,
                    audioUrl = audioUrl,
                    meaning = meaning,
                    example = example,
                    description = "",
                    relatedWords = synonyms,
                    collocation = collocations
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
