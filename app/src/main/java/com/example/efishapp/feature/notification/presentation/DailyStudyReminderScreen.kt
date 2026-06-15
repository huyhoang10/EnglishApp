package com.example.efishapp.feature.notification.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyStudyReminderScreen(
    vm: DailyStudyReminderViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state = vm.uiState.collectAsState().value
    val context = LocalContext.current

    // Hiển thị thông báo (Toast) khi có thông tin mới từ ViewModel
    LaunchedEffect(state.info) {
        state.info?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            vm.clearInfo() // Xóa thông báo sau khi hiển thị để tránh lặp lại
        }
    }

    LaunchedEffect(Unit) { vm.load() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Study Notifications") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                "Set your daily reminder to stay on track!",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = state.title,
                onValueChange = vm::onTitleChange,
                label = { Text("Notification Title") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = state.message,
                onValueChange = vm::onMessageChange,
                label = { Text("Notification Message") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = state.hour.toString(),
                    onValueChange = { vm.onHourChange(it.toIntOrNull() ?: 0) },
                    label = { Text("Hour (0-23)") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = state.minute.toString(),
                    onValueChange = { vm.onMinuteChange(it.toIntOrNull() ?: 0) },
                    label = { Text("Minute (0-59)") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Enable Reminder", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Receive daily push notifications",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Switch(checked = state.isActive, onCheckedChange = vm::onActiveChange)
                }
            }

            Spacer(Modifier.weight(1f))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { vm.save(context) },
                    enabled = !state.loading,
                    modifier = Modifier.weight(1f)
                ) {
                    if (state.loading) CircularProgressIndicator(size = 20.dp)
                    else Text("Save Settings")
                }
                
                OutlinedButton(
                    onClick = { vm.delete(context) },
                    enabled = !state.loading,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun CircularProgressIndicator(size: androidx.compose.ui.unit.Dp) {
    androidx.compose.material3.CircularProgressIndicator(
        modifier = Modifier.size(size),
        strokeWidth = 2.dp
    )
}
