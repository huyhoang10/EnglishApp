package com.example.efishapp.feature.profile.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    viewModel: ProfileSetupViewModel,
    onSetupComplete: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onSetupComplete()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thiết lập hồ sơ") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Chào mừng! Hãy cho chúng tôi biết thêm về bạn.",
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = uiState.fullName,
                onValueChange = { viewModel.onFullNameChange(it) },
                label = { Text("Họ và tên") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = uiState.dateOfBirth,
                onValueChange = { viewModel.onDateOfBirthChange(it) },
                label = { Text("Ngày sinh (DD/MM/YYYY)") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                },
                singleLine = true
            )

            // Gender Selection
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Giới tính", style = MaterialTheme.typography.labelLarge)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = uiState.gender == "Nam",
                        onClick = { viewModel.onGenderChange("Nam") }
                    )
                    Text("Nam")
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(
                        selected = uiState.gender == "Nữ",
                        onClick = { viewModel.onGenderChange("Nữ") }
                    )
                    Text("Nữ")
                }
            }

            // Goal Selection
            var goalExpanded by remember { mutableStateOf(false) }
            val goals = listOf("Học tập", "Công việc", "Du lịch", "Giao tiếp")
            ExposedDropdownMenuBox(
                expanded = goalExpanded,
                onExpandedChange = { goalExpanded = !goalExpanded }
            ) {
                OutlinedTextField(
                    value = uiState.goal,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Mục tiêu học tập") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = goalExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = goalExpanded,
                    onDismissRequest = { goalExpanded = false }
                ) {
                    goals.forEach { goal ->
                        DropdownMenuItem(
                            text = { Text(goal) },
                            onClick = {
                                viewModel.onGoalChange(goal)
                                goalExpanded = false
                            }
                        )
                    }
                }
            }

            // Level Selection
            var levelExpanded by remember { mutableStateOf(false) }
            val levels = listOf("A1", "A2", "B1", "B2", "C1", "C2")
            ExposedDropdownMenuBox(
                expanded = levelExpanded,
                onExpandedChange = { levelExpanded = !levelExpanded }
            ) {
                OutlinedTextField(
                    value = uiState.level,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Trình độ hiện tại") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = levelExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = levelExpanded,
                    onDismissRequest = { levelExpanded = false }
                ) {
                    levels.forEach { level ->
                        DropdownMenuItem(
                            text = { Text(level) },
                            onClick = {
                                viewModel.onLevelChange(level)
                                levelExpanded = false
                            }
                        )
                    }
                }
            }

            if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = { viewModel.saveProfile() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Lưu hồ sơ")
                }
            }
        }
    }
}
