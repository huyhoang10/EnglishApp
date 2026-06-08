package com.example.efishapp.feature.Auth.Presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.efishapp.feature.Auth.Domain.UseCase.CheckLoginStatusUseCase
import com.example.efishapp.feature.Auth.Domain.UseCase.ClearSessionUseCase
import com.example.efishapp.feature.Auth.Domain.UseCase.ForgotPasswordUseCase
import com.example.efishapp.feature.Auth.Domain.UseCase.LoginUseCase
import com.example.efishapp.feature.Auth.Domain.UseCase.LoginWithGoogleUseCase
import com.example.efishapp.feature.Auth.Domain.UseCase.RegisterUseCase
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val forgotPasswordUseCase: ForgotPasswordUseCase,
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val clearSessionUseCase: ClearSessionUseCase,
    private val checkLoginStatusUseCase: CheckLoginStatusUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    suspend fun checkProfileExists(): Boolean {
        val uid = auth.currentUser?.uid ?: return false
        return try {
            val document = firestore.collection("users").document(uid).get().await()
            document.exists() && !document.getString("fullName").isNullOrBlank()
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Xử lý Đăng nhập bằng Email & Mật khẩu
     */
    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = loginUseCase(email, password)
            _uiState.value = result.fold(
                onSuccess = { AuthUiState.Success },
                onFailure = { exception -> handleFailure(exception) }
            )
        }
    }

    /**
     * Xử lý Đăng ký tài khoản mới
     */
    fun register(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = registerUseCase(email, password)
            _uiState.value = result.fold(
                onSuccess = { AuthUiState.RegisterSuccessNeedVerify },
                onFailure = { exception -> handleFailure(exception) }
            )
        }
    }

    /**
     * Xử lý Yêu cầu Khôi phục Mật khẩu
     */
    fun forgotPassword(email: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = forgotPasswordUseCase(email)
            _uiState.value = result.fold(
                onSuccess = { AuthUiState.ForgotPasswordEmailSent },
                onFailure = { exception -> handleFailure(exception) }
            )
        }
    }

    /**
     * Xử lý Đăng nhập bằng Google
     */
    fun loginWithGoogle(idToken: String, email: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            try {
                // 1. Kiểm tra các phương thức đăng nhập đã tồn tại của email này
                val signInMethods = auth.fetchSignInMethodsForEmail(email).await().signInMethods ?: emptyList()

                // 2. Nếu email này ĐÃ ĐƯỢC đăng ký bằng Mật khẩu (password) trước đó
                if (signInMethods.contains("password")) {
                    // Chuyển sang trạng thái chờ xác nhận từ người dùng chứ không đăng nhập thẳng
                    _uiState.value = AuthUiState.NeedAccountLinkingConfirmation(idToken)
                } else {
                    // Nếu chưa có hoặc chỉ có Google, tiến hành đăng nhập thẳng như cũ
                    executeGoogleLogin(idToken)
                }
            } catch (e: Exception) {
                _uiState.value = handleFailure(e)
            }
        }
    }

    private fun executeGoogleLogin(idToken: String) {
        viewModelScope.launch {
            val result = loginWithGoogleUseCase(idToken)
            _uiState.value = result.fold(
                onSuccess = { AuthUiState.Success },
                onFailure = { exception -> handleFailure(exception) }
            )
        }
    }

    fun confirmAccountLinking(idToken: String) {
        executeGoogleLogin(idToken)
    }

    /**
     * Xóa sạch phiên đăng nhập (Đăng xuất khỏi Firebase)
     */
    fun clearUserSession() {
        viewModelScope.launch {
            clearSessionUseCase()
            resetUiState()
        }
    }

    fun resetUiState() {
        _uiState.value = AuthUiState.Idle
    }

    /**
     * Xác định màn hình xuất phát dựa trên trạng thái đăng nhập
     */
    fun getStartDestination(): String {
        val isLoggedIn = checkLoginStatusUseCase()
        return if (isLoggedIn) "home" else "login"
    }

    /**
     * Hàm tập trung xử lý và chuyển đổi Exception từ Firebase thành thông báo thân thiện
     */
    private fun handleFailure(exception: Throwable): AuthUiState {
        val friendlyMessage = when (exception) {
            is IllegalArgumentException -> exception.message ?: "Dữ liệu không hợp lệ."
            is FirebaseAuthInvalidUserException -> "Tài khoản email này không tồn tại."
            is FirebaseAuthInvalidCredentialsException -> "Mật khẩu không chính xác hoặc email sai định dạng."
            is FirebaseAuthUserCollisionException -> "Email này đã được đăng ký thông qua tài khoản Google. " +
                    "Vui lòng quay lại màn hình đăng nhập và chọn 'Đăng nhập bằng Google'"
            else -> exception.localizedMessage ?: "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau."
        }
        return AuthUiState.Error(friendlyMessage)
    }
}