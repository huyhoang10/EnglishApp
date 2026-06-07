// com/example/efishapp/feature/setting/presentation/SettingScreen.kt
package com.example.efishapp.feature.setting.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.efishapp.feature.setting.domain.model.AppTheme

// 1. Hàm Stateful: Kết nối dữ liệu từ ViewModel thực tế
@Composable
fun SettingScreen(
    viewModel: SettingViewModel,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    SettingContent(
        uiState = uiState,
        onThemeChanged = { isDark ->
            // Nếu gạt bật (true) -> Lưu DARK, ngược lại gạt tắt (false) -> Lưu LIGHT
            val newTheme = if (isDark) AppTheme.DARK else AppTheme.LIGHT
            viewModel.onThemeSelected(newTheme)
        },
        onLogoutClick = onLogoutClick,
        modifier = modifier
    )
}

// 2. Hàm Stateless: Hiển thị giao diện và xử lý tương tác trực quan
@Composable
fun SettingContent(
    uiState: SettingUiState,
    onThemeChanged: (Boolean) -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Biến kiểm tra xem theme hiện tại có phải là DARK hay không để gạt nút Switch
    val isDarkMode = uiState.currentTheme == AppTheme.DARK

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Cài đặt hệ thống", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
        ) {
            // Thanh Menu chứa thông tin và nút gạt Switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onThemeChanged(!isDarkMode) } // Bấm vào dòng cũng tự gạt Switch
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon Chế độ tối (Hình mặt trăng)
                Icon(
                    imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.WbSunny,
                    contentDescription = if (isDarkMode) "Chế độ tối" else "Chế độ sáng",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Chế độ tối", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = if (isDarkMode) "Đang bật" else "Đang tắt",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Nút gạt Switch chuyển qua lại giữa sáng và tối
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = { isChecked -> onThemeChanged(isChecked) }
                )
            }
        }
        Button(
            onClick = onLogoutClick, // Khi bấm nút, bắn sự kiện ra ngoài luôn
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error // Màu đỏ cảnh báo
            )
        ) {
            Text(text = "Đăng xuất tài khoản")
        }
    }
}