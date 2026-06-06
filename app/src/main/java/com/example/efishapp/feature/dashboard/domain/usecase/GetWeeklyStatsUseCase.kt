package com.example.efishapp.feature.dashboard.domain.usecase

import com.example.efishapp.feature.dashboard.domain.DailyVocabTracker
import com.example.efishapp.feature.dashboard.domain.WeeklyTrackerRepository
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class GetWeeklyStatsUseCase @Inject constructor(
    private val repository: WeeklyTrackerRepository
) {
    suspend operator fun invoke(userId: String): List<DailyVocabTracker> {
        val currentStats = repository.getWeeklyStats(userId)
            ?: repository.initializeWeeklyStats(userId)

        val calendar = Calendar.getInstance(Locale.getDefault())
        val isMonday = calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY

        if (isMonday) {
            val hasDataInCurrentWeek = currentStats.any { it.newVocabCount > 0 || it.reviewVocabCount > 0 }
            if (hasDataInCurrentWeek) {
                repository.resetAndBackupWeeklyStats(userId, currentStats)
                return repository.initializeWeeklyStats(userId)
            }
        }
        return currentStats
    }
}