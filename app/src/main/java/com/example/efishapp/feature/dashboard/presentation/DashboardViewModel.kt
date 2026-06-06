package com.example.efishapp.feature.dashboard.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.efishapp.feature.dashboard.domain.usecase.GetMonthStatsUseCase
import com.example.efishapp.feature.dashboard.domain.usecase.GetWeeklyStatsUseCase
import com.example.efishapp.feature.dashboard.domain.usecase.SyncStreakAndActivityUseCase

import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getWeeklyStatsUseCase: GetWeeklyStatsUseCase,
    private val getMonthStatsUseCase: GetMonthStatsUseCase,
    private val syncStreakAndActivityUseCase: SyncStreakAndActivityUseCase,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    fun LoadingDashboard() {
        val userId: String = firebaseAuth.currentUser?.uid.toString()

        viewModelScope.launch {
            try {
                val (analytics, name) = syncStreakAndActivityUseCase(userId)
                _uiState.update {
                    it.copy(
                        userName = name,
                        streak = analytics.streak,
                        totalVocabLeaned = analytics.totalVocabLearned
                    )
                }
                Log.d("Dashboard", "Successfully Synchronized Profile & Analytics")
            } catch (e: Exception) {
                Log.e("Dashboard", "Error Synchronizing Profile & Analytics", e)
            }
        }

        viewModelScope.launch {
            try {
                val weeklyStats = getWeeklyStatsUseCase(userId)
                _uiState.update {
                    it.copy(weeklyLearningStats = weeklyStats)
                }
                Log.d("Dashboard", "Successfully Loaded Weekly Stats")
            } catch (e: Exception) {
                Log.e("Dashboard", "Error Loading Weekly Stats", e)
            }
        }

        viewModelScope.launch {
            try {
                val monthlyStats = getMonthStatsUseCase(userId)
                _uiState.update {
                    it.copy(monthlyLearningStat = monthlyStats)
                }
                Log.d("Dashboard", "Successfully Loaded Monthly Stats")
            } catch (e: Exception) {
                Log.e("Dashboard", "Error Loading Monthly Stats", e)
            }
        }
    }

}