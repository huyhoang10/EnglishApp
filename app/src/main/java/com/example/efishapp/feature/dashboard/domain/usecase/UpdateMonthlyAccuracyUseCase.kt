package com.example.efishapp.feature.dashboard.domain.usecase

import com.example.efishapp.feature.dashboard.domain.MonthTrackerRepository
import java.util.Calendar
import javax.inject.Inject

class UpdateMonthlyAccuracyUseCase @Inject constructor(
    private val repository: MonthTrackerRepository
) {
    suspend operator fun invoke(userId: String, correctCount: Long, wrongCount: Long) {
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
        repository.incrementMonthlyAccuracy(userId, currentMonth, correctCount, wrongCount)
    }
}