package com.example.efishapp.feature.notification.presentation

import com.example.efishapp.feature.flashcard.presentation.Vocabulary

class ReviewReminderManager {
    fun getDueWords(vocabularies: List<Vocabulary>): List<Vocabulary> {
        return vocabularies.filter { it.interval <= 0 && it.repetitions > 0 }
    }
}
