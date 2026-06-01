package com.example.efishapp.feature.Auth.Presentation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.efishapp.core.designsystem.ErrorDialog
import com.example.efishapp.core.designsystem.LoadingDialog
import com.example.efishapp.feature.Auth.Presentation.components.AuthTextField
import com.example.efishapp.feature.Auth.Presentation.components.GoogleSignInButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current

    // Thu thập trạng thái từ ViewModel
    val uiState by viewModel.uiState.collectAsState()

    // State quản lý dữ liệu nhập vào của Form
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // --- CẤU HÌNH GOOGLE SIGN-IN SDK TẠI TẦNG UI ---
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            // default_web_client_id tự động sinh ra khi bạn cắm file google-services.json vào dự án
            .requestIdToken("716504332000-4ipvopmpv2g3leaq7bloc909m8i2pprj.apps.googleusercontent.com")
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    // Cửa sổ lắng nghe kết quả trả về từ màn hình danh sách tài khoản Google
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken
            if (idToken != null) {
                // Đã lấy được Google ID Token -> Đẩy xuống bộ não ViewModel để Firebase xác thực
                viewModel.loginWithGoogle(idToken)
            } else {
                // Đề phòng trường hợp Token rỗng, đưa UI state về bình thường
                viewModel.resetUiState()
            }
        } catch (e: ApiException) {
            // Người dùng hủy chọn hoặc thiết bị lỗi kết nối
            viewModel.resetUiState()
        }
    }

    // Xử lý các hiệu ứng phụ dựa trên sự thay đổi của AuthUiState
    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onLoginSuccess()
            viewModel.resetUiState() // Đưa State về Idle sau khi hoàn thành chuyển màn
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
            text = "Chào Mừng Trở Lại",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Ô nhập Email
        AuthTextField(
            value = email,
            onValueChange = { email = it; Modifier },
            label = "Email",
            keyboardType = KeyboardType.Email,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Ô nhập Mật khẩu
        AuthTextField(
            value = password,
            onValueChange = { password = it; Modifier },
            label = "Mật khẩu",
            isPassword = true,
            passwordVisible = isPasswordVisible,
            onPasswordToggle = { isPasswordVisible = !isPasswordVisible },
            modifier = Modifier.fillMaxWidth()
        )

        // Quên mật khẩu link
        Text(
            text = "Quên mật khẩu?",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable { onNavigateToForgotPassword() },
            textAlign = TextAlign.End
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Nút Đăng nhập bằng Email/Password
        Button(
            onClick = { viewModel.loginWithEmail(email, password) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "Đăng Nhập")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Hoặc")

        Spacer(modifier = Modifier.height(16.dp))

        // Nút Đăng nhập bằng Google thương hiệu (ĐÃ ĐƯỢC KÍCH HOẠT LOGIC)
        GoogleSignInButton(
            onClick = {
                // Kích hoạt hiển thị màn hình chọn tài khoản Google của Android
                val signInIntent = googleSignInClient.signInIntent
                googleSignInLauncher.launch(signInIntent)
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Chuyển sang màn đăng ký
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Chưa có tài khoản? ")
            Text(
                text = "Đăng ký ngay",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onNavigateToRegister() }
            )
        }
    }

    // XỬ LÝ TRẠNG THÁI LOADING VÀ ERROR DIALOG
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