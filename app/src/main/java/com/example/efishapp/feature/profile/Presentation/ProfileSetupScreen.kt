package com.example.efishapp.feature.profile.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    viewModel: ProfileSetupViewModel,
    onSetupComplete: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    // DatePicker State
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Date(millis)
                        val calendar = Calendar.getInstance()
                        calendar.time = date
                        val formattedDate = String.format(
                            Locale.getDefault(),
                            "%02d/%02d/%04d",
                            calendar.get(Calendar.DAY_OF_MONTH),
                            calendar.get(Calendar.MONTH) + 1,
                            calendar.get(Calendar.YEAR)
                        )
                        viewModel.onDateOfBirthChange(formattedDate)
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onSetupComplete()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile Setup") }
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
                "Welcome! Let us know more about you.",
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = uiState.fullName,
                onValueChange = { viewModel.onFullNameChange(it) },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = uiState.dateOfBirth,
                onValueChange = { },
                label = { Text("Date of Birth (DD/MM/YYYY)") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                    }
                },
                singleLine = true
            )

            // Gender Selection
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Gender", style = MaterialTheme.typography.labelLarge)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = uiState.gender == "Male",
                        onClick = { viewModel.onGenderChange("Male") }
                    )
                    Text("Male")
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(
                        selected = uiState.gender == "Female",
                        onClick = { viewModel.onGenderChange("Female") }
                    )
                    Text("Female")
                }
            }

            // Goal Selection
            var goalExpanded by remember { mutableStateOf(false) }
            val goals = listOf("Study", "Work", "Travel", "Communication")
            ExposedDropdownMenuBox(
                expanded = goalExpanded,
                onExpandedChange = { goalExpanded = !goalExpanded }
            ) {
                OutlinedTextField(
                    value = uiState.goal,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Study Goal") },
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
                    label = { Text("Current Level") },
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
                    Text("Save Profile")
                }
            }
        }
    }
}
