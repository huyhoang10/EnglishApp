package com.example.efishapp.feature.notification.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import com.example.efishapp.R
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import android.widget.Toast

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
                title = { Text(stringResource(R.string.reminder_title_screen)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.reminder_back_desc)
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
                text = stringResource(R.string.reminder_description),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = state.title,
                onValueChange = vm::onTitleChange,
                label = { Text(stringResource(R.string.reminder_label_title)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = state.message,
                onValueChange = vm::onMessageChange,
                label = { Text(stringResource(R.string.reminder_label_message)) },
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
                    label = { Text(stringResource(R.string.reminder_label_hour)) },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = state.minute.toString(),
                    onValueChange = { vm.onMinuteChange(it.toIntOrNull() ?: 0) },
                    label = { Text(stringResource(R.string.reminder_label_minute)) },
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.reminder_toggle_title),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = stringResource(R.string.reminder_toggle_desc),
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
                    else Text(stringResource(R.string.reminder_btn_save))
                }

                OutlinedButton(
                    onClick = { vm.delete(context) },
                    enabled = !state.loading,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(R.string.reminder_btn_delete))
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
