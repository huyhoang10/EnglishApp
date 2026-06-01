package com.example.efishapp.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.efishapp.feature.profile.domain.usecase.GetProfileUseCase
import com.example.efishapp.feature.profile.domain.usecase.UpdateProfileUseCase
import com.example.efishapp.feature.profile.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val repository: UserProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadProfile()
    }

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

    fun toggleEditMode() {
        _uiState.update { it.copy(isEditMode = !it.isEditMode) }
    }

    fun onFullNameChange(name: String) = _uiState.update { it.copy(editedFullName = name) }
    fun onDateOfBirthChange(dob: String) = _uiState.update { it.copy(editedDateOfBirth = dob) }
    fun onGenderChange(gender: String) = _uiState.update { it.copy(editedGender = gender) }
    fun onGoalChange(goal: String) = _uiState.update { it.copy(editedGoal = goal) }
    fun onLevelChange(level: String) = _uiState.update { it.copy(editedLevel = level) }

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

    fun deleteAccount() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // 1. Xóa dữ liệu trong Firestore
            val dbResult = repository.deleteUserProfile()
            
            dbResult.onSuccess {
                // 2. Xóa tài khoản trong Firebase Auth
                val authResult = repository.deleteFirebaseAuth()
                
                authResult.onSuccess {
                    _uiState.update { it.copy(isDeleteSuccess = true) }
                }.onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = "Xóa Auth thất bại: ${e.message}") }
                }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = "Xóa Database thất bại: ${e.message}") }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
