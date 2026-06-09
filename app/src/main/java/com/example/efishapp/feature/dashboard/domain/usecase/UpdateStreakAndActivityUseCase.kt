package com.example.efishapp.feature.dashboard.domain.usecase

import com.example.efishapp.feature.dashboard.domain.UserAnalytics
import com.example.efishapp.feature.dashboard.domain.UserAnalyticsRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class UpdateStreakAndActivityUseCase @Inject constructor(
    private val repository: UserAnalyticsRepository
) {
    suspend operator fun invoke(userId: String) {
        // update total word
        val totalVocabulary = repository.getTotalWords(userId)
        repository.updateTotalWords(userId,totalVocabulary)

        val currentAnalytics = repository.getUserAnalytics(userId)
            ?: repository.initializeUserAnalytics(userId)

        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val todayStr = formatter.format(calendar.time)


        val updatedAnalytics = if (currentAnalytics.lastActiveDate.isNotEmpty()) {
            val lastActiveDate = formatter.parse(currentAnalytics.lastActiveDate)

            val lastActiveCal = Calendar.getInstance().apply { time = lastActiveDate }
            val yesterdayCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

            val isYesterday = lastActiveCal.get(Calendar.YEAR) == yesterdayCal.get(Calendar.YEAR) &&
                    lastActiveCal.get(Calendar.DAY_OF_YEAR) == yesterdayCal.get(Calendar.DAY_OF_YEAR)

            if (isYesterday) {
                val newStreak = currentAnalytics.streak + 1
                val newHighest = if (newStreak > currentAnalytics.highestStreak) newStreak else currentAnalytics.highestStreak
                currentAnalytics.copy(streak = newStreak, highestStreak = newHighest, lastActiveDate = todayStr)
            } else {
                currentAnalytics.copy(streak = 1, lastActiveDate = todayStr)
            }
        } else {
            currentAnalytics.copy(streak = 1, highestStreak = maxOf(1, currentAnalytics.highestStreak), lastActiveDate = todayStr)
        }

        if (updatedAnalytics != currentAnalytics) {
            repository.updateStreakAndActivity(
                userId = userId,
                streak = updatedAnalytics.streak,
                highestStreak = updatedAnalytics.highestStreak,
                lastActiveDate = updatedAnalytics.lastActiveDate
            )
        }

    }
}