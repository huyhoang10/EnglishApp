package com.example.efishapp.core.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Định nghĩa các tính năng (Tabs) xuất hiện trên thanh điều hướng
enum class ScreenTab(val title: String, val icon: ImageVector) {
    HOME("HOME", Icons.Filled.Home),
    MY_FOLDER("FOLDER", Icons.Filled.Folder),
    REVIEW("REVIEW", Icons.Filled.Checklist), // Icon vòng lặp/làm mới đại diện cho ôn tập
    GAME("GAME", Icons.Filled.Gamepad),
    SETTING("SETTING", Icons.Filled.Settings)
}

// Data class quản lý các thông số thiết kế (gạt bỏ gán cứng)
data class BottomNavConfig(
    val containerColor: Color = Color(0xFFF8FAFC), // Nền xanh nhạt mịn màng gần giống ảnh
    val selectedIconColor: Color = Color(0xFF1E3A8A), // Màu icon khi chọn (Xanh đậm)
    val selectedTextColor: Color = Color(0xFF1E3A8A), // Màu chữ khi chọn
    val unselectedIconColor: Color = Color(0xFF64748B), // Màu icon mặc định (Xám)
    val unselectedTextColor: Color = Color(0xFF64748B),
    val indicatorColor: Color = Color(0xFFE2E8F0), // Vùng nền tròn bao quanh icon khi active
    val elevation: Dp = 8.dp
)

@Composable
fun AppBottomNavigationBar(
    currentTab: ScreenTab,
    onTabSelected: (ScreenTab) -> Unit,
    modifier: Modifier = Modifier,
    config: BottomNavConfig = BottomNavConfig() // Nhận cấu hình tập trung
) {
    NavigationBar(
        modifier = modifier,
        containerColor = config.containerColor,
        tonalElevation = config.elevation
    ) {
        // Lặp qua tất cả các giá trị được định nghĩa trong enum ScreenTab
        ScreenTab.values().forEach { tab ->
            val isSelected = currentTab == tab

            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                label = {
                    Text(
                        text = tab.title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        letterSpacing = 0.5.sp
                    )
                },
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.title
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = config.selectedIconColor,
                    selectedTextColor = config.selectedTextColor,
                    unselectedIconColor = config.unselectedIconColor,
                    unselectedTextColor = config.unselectedTextColor,
                    indicatorColor = config.indicatorColor
                )
            )
        }
    }
}