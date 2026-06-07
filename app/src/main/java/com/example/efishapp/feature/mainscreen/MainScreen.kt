package com.example.efishapp.feature.mainscreen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.efishapp.core.ui.component.AppBottomNavigationBar
import com.example.efishapp.core.ui.component.ScreenTab
import com.example.efishapp.feature.dashboard.presentation.DashboardScreen
import com.example.efishapp.feature.dashboard.presentation.DashboardViewModel
import com.example.efishapp.feature.setting.presentation.SettingScreen
import com.example.efishapp.feature.setting.presentation.SettingViewModel

@Composable
fun MainScreen(
    onNavigateToUserProfile: () -> Unit,
    onNavigateToNotification: () -> Unit,
    onLogoutSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTab by remember { mutableStateOf(ScreenTab.HOME) }

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

            // Vùng chờ cho các thành viên khác cắm màn hình Folder, Review, Game vào
            ScreenTab.MY_FOLDER -> { /* Gọi màn hình Folder */ }
            ScreenTab.REVIEW -> { /* Gọi màn hình Review */ }
            ScreenTab.GAME -> { /* Gọi màn hình Game */ }
        }
    }
}