package com.example.efishapp.feature.dashboard.presentation

import androidx.compose.runtime.Immutable
import com.example.efishapp.feature.dashboard.domain.DailyVocabTracker
import com.example.efishapp.feature.dashboard.domain.DayOfWeek
import com.example.efishapp.feature.dashboard.domain.MonthlyStudyTracker

val mockWeeklyVocabulary = listOf(
    DailyVocabTracker(dayOfWeek = DayOfWeek.Mon, reviewVocabCount = 0, newVocabCount = 0),
    DailyVocabTracker(dayOfWeek = DayOfWeek.Tue, reviewVocabCount = 0, newVocabCount = 0),
    DailyVocabTracker(dayOfWeek = DayOfWeek.Wed, reviewVocabCount = 0, newVocabCount = 0),
    DailyVocabTracker(dayOfWeek = DayOfWeek.Thu, reviewVocabCount = 0, newVocabCount = 0),
    DailyVocabTracker(dayOfWeek = DayOfWeek.Fri, reviewVocabCount = 0, newVocabCount = 0),
    DailyVocabTracker(dayOfWeek = DayOfWeek.Sat, reviewVocabCount = 0, newVocabCount = 0),
    DailyVocabTracker(dayOfWeek = DayOfWeek.Sun, reviewVocabCount = 0, newVocabCount = 0)
)
@Immutable
data class DashboardUiState (
    val userName: String = "",
    val streak: Long = 0,
    val totalVocabLeaned: Long = 0,
    val weeklyLearningStats: List<DailyVocabTracker> = mockWeeklyVocabulary,
    val monthlyLearningStat: MonthlyStudyTracker = MonthlyStudyTracker(),
    val isLoading: Boolean = false
)

