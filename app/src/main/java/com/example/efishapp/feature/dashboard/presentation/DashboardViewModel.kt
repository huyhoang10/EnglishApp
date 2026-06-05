package com.example.efishapp.feature.dashboard.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.efishapp.feature.dashboard.data.repository.MonthlyTrackerRepositoryImpl
import com.example.efishapp.feature.dashboard.data.repository.WeeklyTrackerRepositoryImpl
import com.example.efishapp.feature.dashboard.domain.MonthTrackerReposity
import com.example.efishapp.feature.dashboard.domain.WeeklyTrackerRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@HiltViewModel
class DashboardViewModel @Inject constructor(
    val weeklyTrackerRepository: WeeklyTrackerRepository,
    val monthTrackerReposity: MonthTrackerReposity,
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
): ViewModel(){

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    fun LoadingDashboard() {
        val userId: String = firebaseAuth.currentUser?.uid.toString()
        viewModelScope.launch {
            try {
                // 2. Gọi suspend function để lấy dữ liệu từ Repository (nó sẽ đợi kết quả một cách bất đồng bộ)
                val weeklyStats = weeklyTrackerRepository.getWeeklyStats(userId)

                // 3. Sau khi có dữ liệu thành công, cập nhật vào UI State
                _uiState.update {
                    it.copy(weeklyLearningStats = weeklyStats)
                }
            } catch (e: Exception) {
                // Nên có try-catch để xử lý lỗi nếu database/network gặp sự cố
                Log.e("Dashboard", "Error fetching stats", e)
            }
            try {
                // 2. Gọi suspend function để lấy dữ liệu từ Repository (nó sẽ đợi kết quả một cách bất đồng bộ)
                val montlyStats = monthTrackerReposity.getMonthStats(userId)

                // 3. Sau khi có dữ liệu thành công, cập nhật vào UI State
                _uiState.update {
                    it.copy(monthlyLearningStat = montlyStats)
                }
            } catch (e: Exception) {
                // Nên có try-catch để xử lý lỗi nếu database/network gặp sự cố
                Log.e("Dashboard", "Error fetching stats", e)
            }
        }
    }
}