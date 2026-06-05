package com.example.efishapp.feature.dashboard.domain

interface MonthTrackerRepository {
    suspend fun getMonthStats(userId: String, month: Int): MonthlyStudyTracker?
    suspend fun initializeMonthlyStats(userId: String, month: Int): MonthlyStudyTracker
    suspend fun incrementMonthlyAccuracy(userId: String, month: Int, correctCount: Long, wrongCount: Long)
    suspend fun updateStreak(userId: String, streak: Int)
}