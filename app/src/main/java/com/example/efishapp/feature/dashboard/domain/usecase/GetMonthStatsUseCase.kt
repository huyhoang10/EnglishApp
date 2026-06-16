package com.example.efishapp.feature.dashboard.domain.usecase

import com.example.efishapp.feature.dashboard.domain.MonthTrackerRepository
import com.example.efishapp.feature.dashboard.domain.MonthlyStudyTracker
import java.util.Calendar
import javax.inject.Inject

class GetMonthStatsUseCase @Inject constructor(
    private val repository: MonthTrackerRepository
) {
    suspend operator fun invoke(userId: String): MonthlyStudyTracker {
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
        return repository.getMonthStats(userId, currentMonth)
            ?: repository.initializeMonthlyStats(userId, currentMonth)
    }
}