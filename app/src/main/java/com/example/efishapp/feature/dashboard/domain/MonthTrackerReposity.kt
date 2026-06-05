package com.example.efishapp.feature.dashboard.domain

interface MonthTrackerReposity {
    suspend fun getMonthStats(userId: String): MonthlyStudyTracker
    suspend fun updateMonthlyStats(userId: String, monthlyStudyTracker: MonthlyStudyTracker)
    suspend fun undateStreak(userId: String, streak:Int)
}