package com.example.efishapp.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.efishapp.feature.profile.domain.usecase.GetProfileUseCase
import com.example.efishapp.feature.profile.domain.usecase.UpdateProfileUseCase
import com.example.efishapp.feature.profile.domain.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val repository: UserProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    // Tải thông tin hồ sơ người dùng từ Firestore
    fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val profile = getProfileUseCase()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        profile = profile,
                        error = null,
                        editedFullName = profile.fullName,
                        editedDateOfBirth = profile.dateOfBirth,
                        editedGender = profile.gender,
                        editedGoal = profile.goal,
                        editedLevel = profile.level
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }


    // Chuyển đổi qua lại giữa chế độ xem và chế độ chỉnh sửa
    fun toggleEditMode() {
        _uiState.update { it.copy(isEditMode = !it.isEditMode) }
    }

    // Cập nhật thông tin tạm thời vào State khi người dùng nhập liệu
    fun onFullNameChange(name: String) = _uiState.update { it.copy(editedFullName = name) }

    fun onDateOfBirthChange(dob: String) = _uiState.update { it.copy(editedDateOfBirth = dob) }

    fun onGenderChange(gender: String) = _uiState.update { it.copy(editedGender = gender) }

    fun onGoalChange(goal: String) = _uiState.update { it.copy(editedGoal = goal) }

    fun onLevelChange(level: String) = _uiState.update { it.copy(editedLevel = level) }

    // Gửi yêu cầu cập nhật hồ sơ lên Firestore
    fun updateProfile() {
        val currentState = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = updateProfileUseCase(
                fullName = currentState.editedFullName,
                dateOfBirth = currentState.editedDateOfBirth,
                gender = currentState.editedGender,
                goal = currentState.editedGoal,
                level = currentState.editedLevel
            )
            result.onSuccess {
                _uiState.update { it.copy(isEditMode = false) }
                loadProfile()
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    // Xóa tài khoản: Kiểm tra bảo mật trước, sau đó xóa dữ liệu Firestore và cuối cùng xóa Auth
    fun deleteAccount() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // 1. Thực hiện xóa Auth trước.
            // Nếu phiên quá cũ, hàm này sẽ trả về lỗi RECENT_AUTH_REQUIRED mà không xóa gì cả.
            val authResult = repository.deleteFirebaseAuth()
            
            authResult.onSuccess {
                // 2. Auth xóa thành công, tiếp tục xóa dữ liệu trong Firestore
                val dbResult = repository.deleteUserProfile()
                _uiState.update { it.copy(isDeleteSuccess = true) }
            }.onFailure { e ->
                val errorMessage = if (e.message == "RECENT_AUTH_REQUIRED") {
                    "Vì lý do bảo mật, vui lòng đăng xuất và đăng nhập lại trước khi xóa tài khoản của bạn."
                } else {
                    e.message ?: "Không thể xóa tài khoản"
                }
                _uiState.update { it.copy(isLoading = false, error = errorMessage) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
