package com.example.efishapp.feature.dashboard.presentation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.efishapp.core.ui.component.AppBottomNavigationBar
import com.example.efishapp.core.ui.component.ScreenTab
import com.example.efishapp.feature.dashboard.domain.DailyVocabTracker
import com.example.efishapp.feature.dashboard.domain.MonthlyStudyTracker
import com.example.efishapp.feature.dashboard.presentation.component.GreetingCard
import com.example.efishapp.feature.dashboard.presentation.component.MonthlyStatsScreen
import com.example.efishapp.feature.dashboard.presentation.component.ReviewCard
import com.example.efishapp.feature.dashboard.presentation.component.StreakCard
import com.example.efishapp.feature.dashboard.presentation.component.WeeklyVocabularyChart

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    modifier: Modifier,
    onNavigateToUserProfile: () -> Unit,
    onNavigateToNotification: () -> Unit,
    flashcardViewModel: com.example.efishapp.feature.flashcard.presentation.FlashcardViewModel
) {
    val state by viewModel.uiState.collectAsState()
//    val context = LocalContext.current

//    LaunchedEffect(Unit) {
//        flashcardViewModel.checkAndNotifyReview(context)
//    }

    val scrollState = rememberScrollState()
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {AppBottomNavigationBar(ScreenTab.HOME,{})}
    ) {

        paddingValues -> DashboarContent(
            state.userName,
            state.streak,
            state.numVocabularyReview,
            state.weeklyLearningStats,
            state.monthlyLearningStat,
            onNavigateToUserProfile,
            onNavigateToNotification,
            Modifier.padding(paddingValues).verticalScroll(scrollState)
        )
    }
}



@Composable
fun DashboarContent(
    userName: String,
    streak: Int,
    numVocabularyReview: Int,
    weeklyLearningStats: List<DailyVocabTracker>,
    monthlyLearningStat: MonthlyStudyTracker,
    onNavigateToUserProfile: () -> Unit,
    onNavigateToNotification: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        GreetingCard(userName, onUserProfileClick = onNavigateToUserProfile,onNotificationClick = onNavigateToNotification)
        Row() {
            StreakCard(streak, modifier = Modifier.weight(1f))
            ReviewCard(numVocabularyReview, modifier = Modifier.weight(1f))
        }

        WeeklyVocabularyChart(weeklyLearningStats)
        MonthlyStatsScreen(monthlyLearningStat)
    }
}





