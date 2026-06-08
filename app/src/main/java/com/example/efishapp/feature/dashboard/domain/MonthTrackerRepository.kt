package com.example.efishapp.feature.dashboard.domain

import com.example.efishapp.feature.flashcard.domain.model.VocabularyReview

interface MonthTrackerRepository {
    suspend fun getMonthStats(userId: String, month: Int): MonthlyStudyTracker?
    suspend fun initializeMonthlyStats(userId: String, month: Int): MonthlyStudyTracker
    suspend fun updateMonthlyAccuracy(userId: String, month: Int, correctCount: Long, wrongCount: Long)
    suspend fun updateStreak(userId: String, streak: Int)
    suspend fun getVocabularyInCurrentMonthString(userId: String): List<VocabularyReview>
}