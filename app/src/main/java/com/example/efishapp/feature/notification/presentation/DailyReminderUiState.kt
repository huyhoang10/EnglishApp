package com.example.efishapp.feature.notification.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.efishapp.feature.notification.alarm.DailyStudyAlarmScheduler
import com.example.efishapp.feature.notification.Domain.model.DailyStudyNotification
import com.example.efishapp.feature.notification.Domain.repository.DailyStudyNotificationRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DailyReminderUiState(
    val loading: Boolean = false,
    val title: String = "EnglishApp",
    val message: String = "Time to study English!",
    val hour: Int = 20,
    val minute: Int = 0,
    val isActive: Boolean = false,
    val info: String? = null
)

@HiltViewModel
class DailyStudyReminderViewModel @Inject constructor(
    private val repo: DailyStudyNotificationRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(DailyReminderUiState())
    val uiState: StateFlow<DailyReminderUiState> = _uiState

    private fun uidOrNull(): String? = auth.currentUser?.uid

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
                    info = "Chưa có cài đặt, hãy Save"
                )
            }
        }
    }

    fun onTitleChange(v: String) { _uiState.value = _uiState.value.copy(title = v) }
    fun onMessageChange(v: String) { _uiState.value = _uiState.value.copy(message = v) }
    fun onHourChange(v: Int) { _uiState.value = _uiState.value.copy(hour = v.coerceIn(0, 23)) }
    fun onMinuteChange(v: Int) { _uiState.value = _uiState.value.copy(minute = v.coerceIn(0, 59)) }
    fun onActiveChange(v: Boolean) { _uiState.value = _uiState.value.copy(isActive = v) }

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