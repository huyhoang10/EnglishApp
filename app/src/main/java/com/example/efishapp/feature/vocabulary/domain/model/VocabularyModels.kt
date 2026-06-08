package com.example.efishapp.feature.vocabulary.domain.model

data class Vocabulary(
    val id: String = "",
    val folderId: String = "",
    val word: String = "",
    val description: String = "",
    val pronunciation: String = "",
    val meaning: String = "",
    val example: String = "",
    val relatedWord: String = "",
    val collocation: String = "",
    val audioUrl: String = "",
    val note: String = " "
)