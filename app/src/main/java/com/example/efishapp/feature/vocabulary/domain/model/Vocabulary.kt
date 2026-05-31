package com.example.efishapp.feature.vocabulary.domain.model

data class Vocabulary(
    val id: String = "",
    val folderId: String = "",
    val word: String,
    val phonetic: String = "",
    val meaning: String,
    val example: String = "",
    val exampleMeaning: String = "",
    val imageUrl: String = "",
    val audioUrl: String = "",
    val isLearned: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
