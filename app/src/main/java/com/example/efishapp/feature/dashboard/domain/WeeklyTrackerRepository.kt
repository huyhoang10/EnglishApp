package com.example.efishapp.feature.dashboard.domain

interface WeeklyTrackerRepository {
    suspend fun getWeeklyStats(userId: String): List<DailyVocabTracker>?
    suspend fun initializeWeeklyStats(userId: String): List<DailyVocabTracker>
    suspend fun getTodayVocabCounts(userId: String, todayStr: String): Pair<Int, Int>
    suspend fun updateWeeklyStats(userId: String, dailyVocabTracker: DailyVocabTracker)
    suspend fun resetAndBackupWeeklyStats(userId: String, currentStats: List<DailyVocabTracker>)

    suspend fun getLastResetWeek(userId: String): Int?
}