package com.example.efishapp.feature.dashboard.domain

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


enum class DayOfWeek{
    Mon,
    Tue,
    Wed,
    Thu,
    Fri,
    Sat,
    Sun
}

data class UserAnalytics(
    val userId: String = "",
    val streak: Long = 0,
    val highestStreak: Long = 0,
    val totalVocabLearned: Long = 0L,
    val lastActiveDate: String = "" // Format: "YYYY-MM-DD"
) {
    val userLevel: Int
        get() = (totalVocabLearned / 100).toInt() + 1

    val isStreakActiveToday: Boolean
        get() {
            if (lastActiveDate.isEmpty()) return false

            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            val calendar = Calendar.getInstance()
            val today = formatter.format(calendar.time)

            calendar.add(Calendar.DAY_OF_YEAR, -1)
            val yesterday = formatter.format(calendar.time)

            return lastActiveDate == today || lastActiveDate == yesterday
        }
}

data class DailyVocabTracker(
    val userId: String = "",
    val date: Date = Calendar.getInstance().time,
    val dayOfWeek: DayOfWeek = DayOfWeek.Mon,
    val reviewVocabCount: Int,
    val newVocabCount: Int
) {
    val total: Int get() = reviewVocabCount + newVocabCount
}

data class MonthlyStudyTracker(
    val userId: String = "",
    val year: Int = 2026,
    val correctVocabCount: Long = 0,
    val wrongVocabCount: Long = 0,
    val month: Int = 6,
) {
    val totalWords: Long = correctVocabCount + wrongVocabCount
}

