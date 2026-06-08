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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.efishapp.core.designsystem.ErrorDialog
import com.example.efishapp.core.designsystem.LoadingDialog
import com.example.efishapp.feature.Auth.Presentation.components.AuthTextField
import com.example.efishapp.feature.Auth.Presentation.components.GoogleSignInButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onLoginSuccess: (Boolean) -> Unit
) {
    val context = LocalContext.current

    // Thu thập trạng thái từ ViewModel
    val uiState by viewModel.uiState.collectAsState()

    // State quản lý dữ liệu nhập vào của Form
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // State tạm thời để giữ thông tin Token khi chờ người dùng xác nhận liên kết tài khoản
    var pendingIdTokenForLink by remember { mutableStateOf<String?>(null) }

    // --- CẤU HÌNH GOOGLE SIGN-IN SDK TẠI TẦNG UI ---
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
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
            val googleEmail = account?.email ?: ""

            if (idToken != null) {
                // Đẩy cả token và email xuống để bộ não ViewModel check trùng phương thức đăng nhập
                viewModel.loginWithGoogle(idToken, googleEmail)
            } else {
                viewModel.resetUiState()
            }
        } catch (e: ApiException) {
            viewModel.resetUiState()
        }
    }

    // Xử lý các hiệu ứng chuyển màn hoặc chặn xác nhận liên kết tài khoản
    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthUiState.Success -> {
                val exists = viewModel.checkProfileExists()
                onLoginSuccess(exists)
                viewModel.resetUiState() // Đưa State về Idle sau khi hoàn thành chuyển màn
            }
            is AuthUiState.NeedAccountLinkingConfirmation -> {
                // Bắt được tín hiệu trùng tài khoản Email/Pass trước đó -> giữ token lại để mở Dialog
                pendingIdTokenForLink = (uiState as AuthUiState.NeedAccountLinkingConfirmation).idToken
            }
            else -> Unit
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
            text = "EfishApp",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Ô nhập Email (Đã xóa bỏ đoạn gán Modifier lỗi)
        AuthTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            keyboardType = KeyboardType.Email,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Ô nhập Mật khẩu (Đã xóa bỏ đoạn gán Modifier lỗi)
        AuthTextField(
            value = password,
            onValueChange = { password = it },
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

        // Nút Đăng nhập bằng Google thương hiệu
        GoogleSignInButton(
            onClick = {
                googleSignInClient.signOut().addOnCompleteListener {
                    val signInIntent = googleSignInClient.signInIntent
                    googleSignInLauncher.launch(signInIntent)
                }
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

    // XỬ LÝ TRẠNG THÁI LOADING VÀ ERROR DIALOG TỪ CORE
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

    // HỘP THOẠI HỎI Ý KIẾN LIÊN KẾT TÀI KHOẢN GOOGLE VÀO EMAIL/PASSWORD CÓ SẴN
    if (pendingIdTokenForLink != null) {
        AlertDialog(
            onDismissRequest = {
                pendingIdTokenForLink = null
                viewModel.resetUiState()
            },
            title = { Text("Liên kết tài khoản?") },
            text = {
                Text("Hệ thống phát hiện email này đã được đăng ký bằng Mật khẩu trước đó. Bạn có muốn liên kết tài khoản Google này vào tài khoản đã có để đăng nhập cho những lần sau không?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.confirmAccountLinking(pendingIdTokenForLink!!)
                        pendingIdTokenForLink = null
                    }
                ) {
                    Text("Đồng ý liên kết")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        pendingIdTokenForLink = null
                        viewModel.resetUiState() // Hủy, giữ nguyên trạng thái cũ độc lập
                    }
                ) {
                    Text("Không, hủy bỏ")
                }
            }
        )
    }
}