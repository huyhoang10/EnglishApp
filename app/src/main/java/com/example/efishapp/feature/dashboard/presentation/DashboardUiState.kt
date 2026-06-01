package com.example.efishapp.feature.dashboard.presentation

import com.example.efishapp.feature.dashboard.presentation.component.DailyLearningStats
import com.example.efishapp.feature.dashboard.presentation.component.MonthlyAccuracyStats
import com.example.efishapp.feature.flashcard.presentation.Vocabulary

val mockWeeklyVocabulary = listOf(
    DailyLearningStats(dayOfWeek = "T2", reviewCount = 15, newCount = 5),
    DailyLearningStats(dayOfWeek = "T3", reviewCount = 22, newCount = 12),
    DailyLearningStats(dayOfWeek = "T4", reviewCount = 10, newCount = 8),
    DailyLearningStats(dayOfWeek = "T5", reviewCount = 18, newCount = 15),
    DailyLearningStats(dayOfWeek = "T6", reviewCount = 28, newCount = 20),
    DailyLearningStats(dayOfWeek = "T7", reviewCount = 12, newCount = 6),
    DailyLearningStats(dayOfWeek = "CN", reviewCount = 35, newCount = 25)
)
data class DashboardUiState (
    val streak: Int = 0,
    val numVocabularyReview: Int = 0,
    val weeklyLearningStats: List<DailyLearningStats> = mockWeeklyVocabulary,
    val monthlyLearningStat: MonthlyAccuracyStats = MonthlyAccuracyStats(0,0,"5")
)

