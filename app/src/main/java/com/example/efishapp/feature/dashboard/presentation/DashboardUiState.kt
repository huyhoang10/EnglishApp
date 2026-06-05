package com.example.efishapp.feature.dashboard.presentation

import com.example.efishapp.feature.dashboard.domain.DailyVocabTracker
import com.example.efishapp.feature.dashboard.domain.DayOfWeek
import com.example.efishapp.feature.dashboard.domain.MonthlyStudyTracker

val mockWeeklyVocabulary = listOf(
    DailyVocabTracker(dayOfWeek = DayOfWeek.Mon, reviewVocabCount = 15, newVocabCount = 5),
    DailyVocabTracker(dayOfWeek = DayOfWeek.Tue, reviewVocabCount = 15, newVocabCount = 5),
    DailyVocabTracker(dayOfWeek = DayOfWeek.Wed, reviewVocabCount = 15, newVocabCount = 5),
    DailyVocabTracker(dayOfWeek = DayOfWeek.Thu, reviewVocabCount = 15, newVocabCount = 5),
    DailyVocabTracker(dayOfWeek = DayOfWeek.Fri, reviewVocabCount = 15, newVocabCount = 5),
    DailyVocabTracker(dayOfWeek = DayOfWeek.Sat, reviewVocabCount = 15, newVocabCount = 5),
    DailyVocabTracker(dayOfWeek = DayOfWeek.Sun, reviewVocabCount = 15, newVocabCount = 5)
)
data class DashboardUiState (
    val userName: String = "",
    val streak: Int = 0,
    val numVocabularyReview: Int = 0,
    val weeklyLearningStats: List<DailyVocabTracker> = mockWeeklyVocabulary,
    val monthlyLearningStat: MonthlyStudyTracker = MonthlyStudyTracker()
)

