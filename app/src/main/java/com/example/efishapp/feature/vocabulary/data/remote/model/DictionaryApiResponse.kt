package com.example.efishapp.feature.vocabulary.data.remote.model

import com.google.gson.annotations.SerializedName

data class DictionaryApiResponse(
    val word: String = "",
    val phonetic: String? = null,
    val phonetics: List<PhoneticDto>? = null,
    val meanings: List<MeaningDto>? = null
)

data class PhoneticDto(
    val text: String? = null,
    val audio: String? = null,
    val sourceUrl: String? = null
)

data class MeaningDto(
    val partOfSpeech: String = "",
    val definitions: List<DefinitionDto>? = null,
    val synonyms: List<String>? = null,
    val antonyms: List<String>? = null
)

data class DefinitionDto(
    val definition: String = "",
    val example: String? = null,
    val synonyms: List<String>? = null,
    val antonyms: List<String>? = null
)
