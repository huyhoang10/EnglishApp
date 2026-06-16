package com.example.efishapp.feature.dashboard.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.efishapp.feature.dashboard.domain.usecase.GetMonthStatsUseCase
import com.example.efishapp.feature.dashboard.domain.usecase.GetUserAnalystUsecase
import com.example.efishapp.feature.dashboard.domain.usecase.GetWeeklyStatsUseCase

import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
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
    private val getUserAnalystUsecase: GetUserAnalystUsecase,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

fun LoadingDashboard() {
    val userId = firebaseAuth.currentUser?.uid.orEmpty()
    if (userId.isEmpty()) return

    viewModelScope.launch {
        // 1. Chỉ bật Loading DUY NHẤT một lần ở đây
        _uiState.update { it.copy(isLoading = true) }

        try {
            // 2. Kích hoạt cả 3 UseCase chạy song song (Async) để tiết kiệm thời gian
            val userAnalystDeferred = async { getUserAnalystUsecase(userId) }
            val weeklyStatsDeferred = async { getWeeklyStatsUseCase(userId) }
            val monthlyStatsDeferred = async { getMonthStatsUseCase(userId) }

            // 3. Đợi cả 3 thằng cùng trả về kết quả (Await)
            val (analytics, name) = userAnalystDeferred.await()
            val weeklyStats = weeklyStatsDeferred.await()
            val monthlyStats = monthlyStatsDeferred.await()

            // 4. Cập nhật toàn bộ dữ liệu VÀ tắt Loading cùng một lúc
            _uiState.update {
                it.copy(
                    userName = name,
                    streak = analytics.streak,
                    totalVocabLeaned = analytics.totalVocabLearned,
                    weeklyLearningStats = weeklyStats,
                    monthlyLearningStat = monthlyStats,
                    isLoading = false // Tắt an toàn
                )
            }
            Log.d("Dashboard", "Successfully Synchronized All Dashboard Data")

        } catch (e: Exception) {
            Log.e("Dashboard", "Error Loading Dashboard Data", e)
            // Đảm bảo nếu lỗi xảy ra thì vẫn phải tắt Loading để app không bị kẹt xoay tròn
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}
}