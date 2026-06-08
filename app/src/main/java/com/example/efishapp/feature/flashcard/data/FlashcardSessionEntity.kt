package com.example.efishapp.feature.flashcard.data

import androidx.room.Entity
import com.example.efishapp.feature.flashcard.domain.model.ActionType
import com.example.efishapp.feature.flashcard.domain.model.Vocabulary

@Entity(tableName = "flashcard_sessions", primaryKeys = ["userId", "folderId"])
data class FlashcardSessionEntity(
    val userId: String,
    val folderId: String,
    val currentIndex: Int,
    val countForget: Int,
    val countRemember: Int,
    val vocabularies: List<Vocabulary>,
    val userAnswers: Map<String, ActionType>
)