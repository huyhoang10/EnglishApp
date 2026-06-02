package com.example.efishapp.feature.notification.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun DailyStudyReminderScreen(
    vm: DailyStudyReminderViewModel,
    modifier: Modifier = Modifier
) {
    val state = vm.uiState.collectAsState().value
    val context = LocalContext.current

    LaunchedEffect(Unit) { vm.load() }

    Column(modifier = modifier.padding(16.dp)) {
        Text("Daily study reminder")

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = state.title,
            onValueChange = vm::onTitleChange,
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = state.message,
            onValueChange = vm::onMessageChange,
            label = { Text("Message") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
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

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Active")
            Switch(checked = state.isActive, onCheckedChange = vm::onActiveChange)
        }

        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { vm.save(context) }, enabled = !state.loading) { Text("Save") }
            Button(onClick = { vm.delete(context) }, enabled = !state.loading) { Text("Delete") }
        }

        Spacer(Modifier.height(12.dp))

        if (state.loading) CircularProgressIndicator()
        state.info?.let { Text(it) }
    }
}