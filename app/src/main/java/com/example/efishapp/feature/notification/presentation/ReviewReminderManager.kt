package com.example.efishapp.feature.notification.presentation

import com.example.efishapp.feature.flashcard.presentation.Vocabulary

/**
 * Manager chịu trách nhiệm kiểm tra danh sách từ vựng cần ôn tập.
 * Tách biệt hoàn toàn khỏi FlashcardViewModel để đảm bảo tính độc lập.
 */
class ReviewReminderManager {

    /**
     * Trả về danh sách từ đến hạn ôn (interval <= 0 và đã từng học).
     */
    fun getDueWords(vocabularies: List<Vocabulary>): List<Vocabulary> {
        return vocabularies.filter { it.interval <= 0 && it.repetitions > 0 }
    }
}
