package com.example.efishapp.feature.Auth.Presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.efishapp.core.designsystem.ErrorDialog
import com.example.efishapp.core.designsystem.LoadingDialog
import com.example.efishapp.feature.Auth.Presentation.components.AuthTextField

@Composable
fun ForgotPasswordScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateBackToLogin: () -> Unit,
    onSendEmailSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var email by remember { mutableStateOf("") }

    // Theo dõi khi Firebase gửi mail thành công
    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onSendEmailSuccess()
            viewModel.resetUiState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Khôi Phục Mật Khẩu",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Nhập email đăng ký của bạn. Chúng tôi sẽ gửi một liên kết để bạn thiết lập lại mật khẩu mới.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Ô nhập Email yêu cầu khôi phục
        AuthTextField(
            value = email,
            onValueChange = { email = it; Modifier },
            label = "Email của bạn",
            keyboardType = KeyboardType.Email,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Nút gửi yêu cầu
        Button(
            onClick = { viewModel.forgotPassword(email) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "Gửi Yêu Cầu")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Nút quay lại màn hình đăng nhập
        Text(
            text = "Quay lại Đăng nhập",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .clickable { onNavigateBackToLogin() }
                .padding(8.dp)
        )
    }

    // Lắng nghe trạng thái xử lý bất đồng bộ từ Core UI giống các màn hình trước
    when (uiState) {
        is AuthUiState.Loading -> {
            LoadingDialog()
        }
        is AuthUiState.Error -> {
            val errorMessage = (uiState as AuthUiState.Error).message
            ErrorDialog(message = errorMessage, onDismiss = { viewModel.resetUiState() })
        }
        else -> Unit
    }
}