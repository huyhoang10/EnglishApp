package com.example.efishapp.feature.Auth.Presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
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
fun RegisterScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }
    var localErrorMessage by remember { mutableStateOf<String?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Theo dõi trạng thái đăng ký thành công từ Firebase
    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.RegisterSuccessNeedVerify) {
            showSuccessDialog = true
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
            text = "Đăng ký tài khoản",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Mật khẩu phải từ 8 ký tự, gồm chữ hoa, chữ thường, số và ký tự đặc biệt",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Ô nhập Email (Đã dọn dẹp lambda)
        AuthTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            keyboardType = KeyboardType.Email,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Ô nhập Mật khẩu (Đã dọn dẹp lambda)
        AuthTextField(
            value = password,
            onValueChange = { password = it },
            label = "Mật khẩu",
            isPassword = true,
            passwordVisible = isPasswordVisible,
            onPasswordToggle = { isPasswordVisible = !isPasswordVisible },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Ô nhập lại Mật khẩu để đối chiếu (Đã dọn dẹp lambda)
        AuthTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Xác nhận mật khẩu",
            isPassword = true,
            passwordVisible = isConfirmPasswordVisible,
            onPasswordToggle = { isConfirmPasswordVisible = !isConfirmPasswordVisible },
            modifier = Modifier.fillMaxWidth()
        )

        // Hiển thị lỗi kiểm tra nhanh tại local nếu mật khẩu không khớp
        if (localErrorMessage != null) {
            Text(
                text = localErrorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Nút Đăng ký
        Button(
            onClick = {
                if (password != confirmPassword) {
                    localErrorMessage = "Mật khẩu xác nhận không khớp."
                } else {
                    localErrorMessage = null
                    viewModel.register(email, password)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "Đăng Ký")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Quay lại màn hình Đăng nhập
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Đã có tài khoản? ")
            Text(
                text = "Đăng nhập",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onNavigateToLogin() }
            )
        }
    }

    // Lắng nghe trạng thái xử lý bất đồng bộ từ Core UI
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

    // Hiển thị Dialog thông báo yêu cầu xác thực Mail
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { /* Không cho phép tắt bằng cách bấm ra ngoài */ },
            title = { Text(text = "Đăng ký thành công!") },
            text = {
                Text(text = "Một email xác thực đã được gửi tới ứng dụng hộp thư của bạn. Vui lòng kiểm tra và kích hoạt tài khoản trước khi đăng nhập.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetUiState() // Reset lại state về Idle để không bị lặp loop
                        onRegisterSuccess() // Điều hướng về màn hình Login
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}