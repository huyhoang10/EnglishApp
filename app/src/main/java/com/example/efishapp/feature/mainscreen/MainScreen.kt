package com.example.efishapp.feature.mainscreen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.efishapp.core.ui.component.AppBottomNavigationBar
import com.example.efishapp.core.ui.component.ScreenTab
import com.example.efishapp.core.util.OnDeviceTTSHelper
import com.example.efishapp.feature.dashboard.presentation.DashboardScreen
import com.example.efishapp.feature.dashboard.presentation.DashboardViewModel
import com.example.efishapp.feature.folder.presentation.FolderScreen
import com.example.efishapp.feature.game.domain.model.GameLevel
import com.example.efishapp.feature.game.domain.model.GameType
import com.example.efishapp.feature.game.presentation.GameScreen
import com.example.efishapp.feature.setting.presentation.SettingScreen
import com.example.efishapp.feature.setting.presentation.SettingViewModel
import com.example.efishapp.navigation.GameResultScreenRoute

@Composable
fun MainScreen(
    onNavigateToUserProfile: () -> Unit,
    onNavigateToNotification: () -> Unit,
    onNavigateToDailyStudyReminder: () -> Unit,
    onNavigateToReview: () -> Unit,
    onNavigateToGame: () -> Unit,
    onLogoutSuccess: () -> Unit,
    onNavigateToFolderDetail: (String, String) -> Unit,
    onNavigateToResult: (Int, Int, GameType, GameLevel) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTab by rememberSaveable { mutableStateOf(ScreenTab.HOME) }
    BackHandler(enabled = currentTab != ScreenTab.HOME) {
        currentTab = ScreenTab.HOME
    }
    Scaffold(
        modifier = modifier,
        bottomBar = {
            AppBottomNavigationBar(
                currentTab = currentTab,
                onTabSelected = { selectedTab -> currentTab = selectedTab }
            )
        }
    ) { paddingValues ->
        val contentModifier = Modifier.padding(paddingValues)

        when (currentTab) {
            ScreenTab.HOME -> {
                val dashboardViewModel: DashboardViewModel = hiltViewModel()
                DashboardScreen(
                    viewModel = dashboardViewModel,
                    modifier = contentModifier,
                    onNavigateToUserProfile = onNavigateToUserProfile,
                    onNavigateToNotification = onNavigateToNotification
                )
            }

            ScreenTab.SETTING -> {
                val settingViewModel: SettingViewModel = hiltViewModel()
                SettingScreen(
                    viewModel = settingViewModel,
                    modifier = contentModifier,
                    onLogoutClick = onLogoutSuccess,
                    onNavigateToDailyReminder = onNavigateToDailyStudyReminder
                )
            }

            ScreenTab.REVIEW -> { onNavigateToReview() }
            ScreenTab.MY_FOLDER -> {
                FolderScreen(
                    onNavigateToFolderDetail = onNavigateToFolderDetail
                )
            }
            ScreenTab.GAME -> { //onNavigateToGame()
                GameScreen(
                    {},
                    onNavigateToResult = onNavigateToResult
                )
            }
        }
    }
}
