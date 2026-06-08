package com.example.efishapp.feature.mainscreen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.efishapp.core.ui.component.AppBottomNavigationBar
import com.example.efishapp.core.ui.component.ScreenTab
import com.example.efishapp.core.util.OnDeviceTTSHelper
import com.example.efishapp.feature.dashboard.presentation.DashboardScreen
import com.example.efishapp.feature.dashboard.presentation.DashboardViewModel
import com.example.efishapp.feature.flashcard.presentation.FlashcardScreen
import com.example.efishapp.feature.flashcard.presentation.FlashcardViewModel
import com.example.efishapp.feature.folder.presentation.FolderScreen
import com.example.efishapp.feature.setting.presentation.SettingScreen
import com.example.efishapp.feature.setting.presentation.SettingViewModel
import com.example.efishapp.navigation.CongratulationScreenRoute

@Composable
fun MainScreen(
    onNavigateToUserProfile: () -> Unit,
    onNavigateToNotification: () -> Unit,
    onNavigateToReview: () -> Unit,
    onLogoutSuccess: () -> Unit,
    onNavigateToFolderDetail: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTab by remember { mutableStateOf(ScreenTab.HOME) }
    BackHandler(enabled = currentTab != ScreenTab.HOME) {
        currentTab = ScreenTab.HOME
    }
    Scaffold(
        modifier = modifier,
        bottomBar = {
            // Gắn thanh bar dưới đáy cố định của bạn vào
            AppBottomNavigationBar(
                currentTab = currentTab,
                onTabSelected = { selectedTab -> currentTab = selectedTab }
            )
        }
    ) { paddingValues ->
        val contentModifier = Modifier.padding(paddingValues)

        // Phân phối màn hình dựa vào tab
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
                    onLogoutClick = onLogoutSuccess
                )
            }


            ScreenTab.REVIEW -> {onNavigateToReview()}
            ScreenTab.MY_FOLDER -> {
                FolderScreen(
                    onNavigateToFolderDetail = onNavigateToFolderDetail
                )
            }
            ScreenTab.REVIEW -> { /* Gọi màn hình Review */ }
            ScreenTab.GAME -> { /* Gọi màn hình Game */ }
        }
    }
}