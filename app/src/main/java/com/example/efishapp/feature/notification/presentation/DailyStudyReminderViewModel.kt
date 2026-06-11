package com.example.efishapp.feature.notification.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.efishapp.feature.notification.alarm.DailyStudyAlarmScheduler
import com.example.efishapp.feature.notification.domain.model.DailyStudyNotification
import com.example.efishapp.feature.notification.domain.repository.DailyStudyNotificationRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DailyStudyReminderViewModel @Inject constructor(
    private val repo: DailyStudyNotificationRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(DailyReminderUiState())
    val uiState: StateFlow<DailyReminderUiState> = _uiState

    private fun uidOrNull(): String? = auth.currentUser?.uid

    // Tải cài đặt nhắc nhở học tập của người dùng từ Firestore
    fun load() {
        val uid = uidOrNull() ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, info = null)

            val data = repo.get(uid)
            _uiState.value = if (data != null) {
                _uiState.value.copy(
                    loading = false,
                    title = data.title,
                    message = data.message,
                    hour = data.hour,
                    minute = data.minute,
                    isActive = data.isActive,
                    info = "Loaded"
                )
            } else {
                _uiState.value.copy(
                    loading = false,
                    info = "No settings yet, please save"
                )
            }
        }
    }

    fun onTitleChange(v: String) { _uiState.value = _uiState.value.copy(title = v) }
    fun onMessageChange(v: String) { _uiState.value = _uiState.value.copy(message = v) }
    fun onHourChange(v: Int) { _uiState.value = _uiState.value.copy(hour = v.coerceIn(0, 23)) }
    fun onMinuteChange(v: Int) { _uiState.value = _uiState.value.copy(minute = v.coerceIn(0, 59)) }
    fun onActiveChange(v: Boolean) { _uiState.value = _uiState.value.copy(isActive = v) }

    // Lưu cài đặt nhắc nhở học tập và thiết lập AlarmManager để thông báo
    fun save(context: Context) {
        val uid = uidOrNull() ?: return

        val data = DailyStudyNotification(
            title = _uiState.value.title,
            message = _uiState.value.message,
            hour = _uiState.value.hour,
            minute = _uiState.value.minute,
            isActive = _uiState.value.isActive
        )

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, info = null)

            val ok = repo.upsert(uid, data)
            if (!ok) {
                _uiState.value = _uiState.value.copy(loading = false, info = "Save FAIL")
                return@launch
            }

            if (data.isActive) {
                DailyStudyAlarmScheduler.scheduleDaily(
                    context = context,
                    hour = data.hour,
                    minute = data.minute,
                    title = data.title,
                    message = data.message
                )
            } else {
                DailyStudyAlarmScheduler.cancel(context)
            }

            _uiState.value = _uiState.value.copy(loading = false, info = "Saved")
        }
    }

    // Xóa cài đặt nhắc nhở và hủy bỏ thông báo đã lập lịch
    fun delete(context: Context) {
        val uid = uidOrNull() ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, info = null)

            val ok = repo.delete(uid)
            DailyStudyAlarmScheduler.cancel(context)

            _uiState.value = _uiState.value.copy(
                loading = false,
                isActive = false,
                info = if (ok) "Deleted" else "Delete FAIL"
            )
        }
    }
}
