package com.example.efishapp.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.efishapp.feature.profile.domain.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileSetupViewModel @Inject constructor(
    private val repository: UserProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileSetupUiState())
    val uiState = _uiState.asStateFlow()

    // Cập nhật thông tin vào State
    fun onFullNameChange(name: String) {
        _uiState.update { it.copy(fullName = name) }
    }

    fun onDateOfBirthChange(dob: String) {
        _uiState.update { it.copy(dateOfBirth = dob) }
    }

    fun onGenderChange(gender: String) {
        _uiState.update { it.copy(gender = gender) }
    }

    fun onGoalChange(goal: String) {
        _uiState.update { it.copy(goal = goal) }
    }

    fun onLevelChange(level: String) {
        _uiState.update { it.copy(level = level) }
    }

    // Lưu thông tin hồ sơ lần đầu tiên sau khi đăng ký
    fun saveProfile() {
        val currentState = _uiState.value
        if (currentState.fullName.isBlank() || currentState.dateOfBirth.isBlank()) {
            _uiState.update { it.copy(error = "Please fill in all the information") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = repository.updateProfile(
                fullName = currentState.fullName,
                dateOfBirth = currentState.dateOfBirth,
                gender = currentState.gender,
                goal = currentState.goal,
                level = currentState.level
            )
            
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
