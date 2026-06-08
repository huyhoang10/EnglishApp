package com.example.efishapp.feature.dashboard.domain

interface UserAnalyticsRepository {
    suspend fun getUserAnalytics(userId: String): UserAnalytics?
    suspend fun initializeUserAnalytics(userId: String): UserAnalytics
    suspend fun updateStreakAndActivity(userId: String, streak: Long, highestStreak: Long, lastActiveDate: String)
    suspend fun updateTotalWords(userId: String, totalWords: Long)

    suspend fun getTotalWords(userId: String): Long
    suspend fun getUserName(userId: String): String
}