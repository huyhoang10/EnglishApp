package com.example.efishapp.feature.dashboard.presentation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import com.example.efishapp.core.ui.component.AppBottomNavigationBar
import com.example.efishapp.core.ui.component.ScreenTab
import com.example.efishapp.feature.dashboard.presentation.component.DailyLearningStats
import com.example.efishapp.feature.dashboard.presentation.component.GreetingCard
import com.example.efishapp.feature.dashboard.presentation.component.MonthlyStatsScreen
import com.example.efishapp.feature.dashboard.presentation.component.ReviewCard
import com.example.efishapp.feature.dashboard.presentation.component.StreakCard
import com.example.efishapp.feature.dashboard.presentation.component.WeeklyVocabularyChart

val mockWeeklyVocabulary = listOf(
    DailyLearningStats(dayOfWeek = "T2", reviewCount = 15, newCount = 5),
    DailyLearningStats(dayOfWeek = "T3", reviewCount = 22, newCount = 12),
    DailyLearningStats(dayOfWeek = "T4", reviewCount = 10, newCount = 8),
    DailyLearningStats(dayOfWeek = "T5", reviewCount = 18, newCount = 15),
    DailyLearningStats(dayOfWeek = "T6", reviewCount = 28, newCount = 20),
    DailyLearningStats(dayOfWeek = "T7", reviewCount = 12, newCount = 6),
    DailyLearningStats(dayOfWeek = "CN", reviewCount = 35, newCount = 25)
)

@Composable
fun DashboardScreen(
    modifier: Modifier) {
    val scrollState = rememberScrollState()
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {AppBottomNavigationBar(ScreenTab.HOME,{})}
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            GreetingCard("Hoang")
            Row() {
                StreakCard(10, modifier = Modifier.weight(1f))
                ReviewCard(10, modifier = Modifier.weight(1f))
            }

            WeeklyVocabularyChart(weeklyData = mockWeeklyVocabulary)
            MonthlyStatsScreen()
        }
    }
}









