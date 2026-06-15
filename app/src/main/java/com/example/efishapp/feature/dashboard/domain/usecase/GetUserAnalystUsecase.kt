package com.example.efishapp.feature.dashboard.domain.usecase

import com.example.efishapp.feature.dashboard.domain.UserAnalytics
import com.example.efishapp.feature.dashboard.domain.UserAnalyticsRepository
import javax.inject.Inject

class GetUserAnalystUsecase @Inject constructor(private val repository: UserAnalyticsRepository) {
    suspend operator fun invoke(userId: String): Pair<UserAnalytics, String> {
        val userAnalytics = repository.getUserAnalytics(userId)
        val userName = repository.getUserName(userId)
        return Pair(userAnalytics, userName) as Pair<UserAnalytics, String>
    }
}