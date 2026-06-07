// com.example.efishapp.feature.dashboard.presentation.DashboardScreen.kt
package com.example.efishapp.feature.dashboard.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel // Sử dụng chuẩn của hilt navigation compose
import com.example.efishapp.feature.dashboard.domain.DailyVocabTracker
import com.example.efishapp.feature.dashboard.domain.MonthlyStudyTracker
import com.example.efishapp.feature.dashboard.presentation.component.GreetingCard
import com.example.efishapp.feature.dashboard.presentation.component.MonthlyStatsScreen
import com.example.efishapp.feature.dashboard.presentation.component.ReviewCard
import com.example.efishapp.feature.dashboard.presentation.component.StreakCard
import com.example.efishapp.feature.dashboard.presentation.component.WeeklyVocabularyChart

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    modifier: Modifier = Modifier, // Thêm giá trị mặc định để tránh lỗi biên dịch
    onNavigateToUserProfile: () -> Unit,
    onNavigateToNotification: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.LoadingDashboard()
    }

    val scrollState = rememberScrollState()

    // XÓA BỎ HOÀN TOÀN SCAFFOLD VÀ BOTTOMBAR Ở ĐÂY
    DashboarContent(
        userName = state.userName,
        streak = state.streak,
        totalVocabLearned = state.totalVocabLeaned,
        weeklyLearningStats = state.weeklyLearningStats,
        monthlyLearningStat = state.monthlyLearningStat,
        onNavigateToUserProfile = onNavigateToUserProfile,
        onNavigateToNotification = onNavigateToNotification,
        // Dùng modifier được truyền từ MainScreen xuống để tránh đè lên thanh bar tổng
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    )
}

@Composable
fun DashboarContent(
    userName: String,
    streak: Long,
    totalVocabLearned: Long,
    weeklyLearningStats: List<DailyVocabTracker>,
    monthlyLearningStat: MonthlyStudyTracker,
    onNavigateToUserProfile: () -> Unit,
    onNavigateToNotification: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        GreetingCard(userName, onUserProfileClick = onNavigateToUserProfile, onNotificationClick = onNavigateToNotification)
        Row {
            StreakCard(streak, modifier = Modifier.weight(1f))
            ReviewCard(totalVocabLearned, modifier = Modifier.weight(1f))
        }

        WeeklyVocabularyChart(weeklyLearningStats)
        MonthlyStatsScreen(monthlyLearningStat)
    }
}