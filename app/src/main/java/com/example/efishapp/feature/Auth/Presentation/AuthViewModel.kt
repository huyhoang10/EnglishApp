package com.example.efishapp.feature.Auth.Presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {
    // StateFlow quản lý trạng thái UI, Giao diện (Compose) sẽ lắng nghe biến này
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
            _uiState.value = handleResult(result)
        }
    }

    /**
     * Xử lý Đăng ký tài khoản mới
     */
    fun register(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = registerUseCase(email, password)
            _uiState.value = handleResult(result)
        }
    }

    /**
     * Xử lý Yêu cầu Khôi phục Mật khẩu
     */
    fun forgotPassword(email: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = forgotPasswordUseCase(email)
            _uiState.value = handleResult(result)
        }
    }

    /**
     * Xử lý Đăng nhập bằng Google (Nhận Token từ SDK Google ở tầng UI)
     */
    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = loginWithGoogleUseCase(idToken)
            _uiState.value = handleResult(result)
        }
    }

    fun resetUiState() {
        _uiState.value = AuthUiState.Idle
    }
    // dịch lỗi Firebase sang Tiếng Việt
    private fun handleResult(result: Result<Unit>): AuthUiState {
        return result.fold(
            onSuccess = { AuthUiState.Success },
            onFailure = { exception ->
                val friendlyMessage = when (exception) {
                    is IllegalArgumentException -> exception.message ?: "Dữ liệu không hợp lệ."
                    is FirebaseAuthInvalidUserException -> "Tài khoản email này không tồn tại."
                    is FirebaseAuthInvalidCredentialsException -> "Mật khẩu không chính xác hoặc email sai định dạng."
                    is FirebaseAuthUserCollisionException -> "Email này đã được đăng ký bằng phương thức khác."
                    else -> exception.localizedMessage ?: "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau."
                }
                AuthUiState.Error(friendlyMessage)
            }
        )
    }
}
