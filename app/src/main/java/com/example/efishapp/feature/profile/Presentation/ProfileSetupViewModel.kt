package com.example.efishapp.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.efishapp.feature.profile.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileSetupUiState(
    val fullName: String = "",
    val dateOfBirth: String = "",
    val gender: String = "",
    val goal: String = "",
    val level: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

class ProfileSetupViewModel(
    private val repository: UserProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileSetupUiState())
    val uiState = _uiState.asStateFlow()

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

    fun saveProfile() {
        val currentState = _uiState.value
        if (currentState.fullName.isBlank() || currentState.dateOfBirth.isBlank()) {
            _uiState.update { it.copy(error = "Vui lòng điền đầy đủ thông tin") }
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
