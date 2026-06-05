package com.example.efishapp.feature.dashboard.domain

interface WeeklyTrackerRepository {
    suspend fun getWeeklyStats(userId: String): List<DailyVocabTracker>
    suspend fun updateWeeklyStats(userId: String, dailyVocabTracker: DailyVocabTracker)
}