package com.example.efishapp.feature.dashboard.domain.usecase

import com.example.efishapp.feature.dashboard.domain.DailyVocabTracker
import com.example.efishapp.feature.dashboard.domain.WeeklyTrackerRepository
import javax.inject.Inject

class UpdateWeeklyStatsUseCase @Inject constructor(
    private val repository: WeeklyTrackerRepository
) {
    suspend operator fun invoke(userId: String, dailyVocabTracker: DailyVocabTracker) {
        repository.updateWeeklyStats(userId, dailyVocabTracker)
    }
}