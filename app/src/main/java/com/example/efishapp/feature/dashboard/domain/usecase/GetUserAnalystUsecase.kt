package com.example.efishapp.feature.dashboard.domain.usecase

import com.example.efishapp.feature.dashboard.domain.UserAnalytics
import com.example.efishapp.feature.dashboard.domain.UserAnalyticsRepository
import javax.inject.Inject

class GetUserAnalystUsecase @Inject constructor(
    private val repository: UserAnalyticsRepository
) {
    suspend operator fun invoke(userId: String): Pair<UserAnalytics, String> {
        var userAnalytics = repository.getUserAnalytics(userId)

        if (userAnalytics == null) {
            userAnalytics = repository.initializeUserAnalytics(userId)
        }

        val userName = repository.getUserName(userId)

        return Pair(userAnalytics, userName)
    }
}