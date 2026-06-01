package com.example.efishapp.feature.dashboard.presentation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.efishapp.core.ui.component.AppBottomNavigationBar
import com.example.efishapp.core.ui.component.ScreenTab
import com.example.efishapp.feature.dashboard.presentation.component.DailyLearningStats
import com.example.efishapp.feature.dashboard.presentation.component.GreetingCard
import com.example.efishapp.feature.dashboard.presentation.component.MonthlyStatsScreen
import com.example.efishapp.feature.dashboard.presentation.component.ReviewCard
import com.example.efishapp.feature.dashboard.presentation.component.StreakCard
import com.example.efishapp.feature.dashboard.presentation.component.WeeklyVocabularyChart

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    modifier: Modifier) {
    val state by viewModel.uiState.collectAsState()

    val scrollState = rememberScrollState()
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {AppBottomNavigationBar(ScreenTab.HOME,{})}
    ) {

        paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            GreetingCard("Hoang")
            Row() {
                StreakCard(state, modifier = Modifier.weight(1f))
                ReviewCard(state, modifier = Modifier.weight(1f))
            }

            WeeklyVocabularyChart(state)
            MonthlyStatsScreen(state)
        }
    }
}









