package com.example.efishapp.feature.dashboard.domain.usecase

import com.example.efishapp.core.util.getCurrentDayOfWeek
import com.example.efishapp.feature.dashboard.domain.DailyVocabTracker
import com.example.efishapp.feature.dashboard.domain.WeeklyTrackerRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class UpdateWeeklyStatsUseCase @Inject constructor(
    private val repository: WeeklyTrackerRepository
) {
    suspend operator fun invoke(userId: String) {
        val DATE_FORMAT = "yyyy-MM-dd"
        val std = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        val todayStr = std.format(Date())

        val (newVocabCount, reviewVocabCount) = repository.getTodayVocabCounts(userId, todayStr)

        repository.updateWeeklyStats(
            userId = userId,
            DailyVocabTracker(dayOfWeek = getCurrentDayOfWeek(),
                reviewVocabCount = reviewVocabCount,
                newVocabCount=newVocabCount )
        )
    }
}