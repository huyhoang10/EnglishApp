package com.example.efishapp.feature.notification.presentation

import com.example.efishapp.feature.flashcard.domain.model.Vocabulary

data class ReviewReminderUiState(
    val vocabularies: List<Vocabulary> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
