package com.example.efishapp.feature.dashboard.data.repository

import com.example.efishapp.feature.dashboard.domain.DailyVocabTracker
import com.example.efishapp.feature.dashboard.domain.WeeklyTrackerRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeeklyTrackerRepositoryImpl @Inject constructor(): WeeklyTrackerRepository {
    override suspend fun getWeeklyStats(userId: String): List<DailyVocabTracker>{
        val ls: List<DailyVocabTracker> = emptyList()
        return ls
    }
    override suspend fun updateWeeklyStats(userId: String, dailyVocabTracker: DailyVocabTracker){

    }
}