package com.example.efishapp.feature.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit,
    onDeleteSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    var showDeleteDialog by remember { mutableStateOf(false) }

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

    LaunchedEffect(uiState.isDeleteSuccess) {
        if (uiState.isDeleteSuccess) {
            onDeleteSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.toggleEditMode() },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (uiState.isEditMode) MaterialTheme.colorScheme.secondary else Color(0xFF43766C))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
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
            // Ảnh đại diện tĩnh
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                if (uiState.isEditMode) {
                    // --- EDIT MODE ---
                    OutlinedTextField(
                        value = uiState.editedFullName,
                        onValueChange = { viewModel.onFullNameChange(it) },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = uiState.profile?.email ?: "",
                        onValueChange = { },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false // Email thường chỉ đọc vì lý do bảo mật
                    )

                    OutlinedTextField(
                        value = uiState.editedDateOfBirth,
                        onValueChange = { },
                        label = { Text("Date of Birth (DD/MM/YYYY)") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                            }
                        }
                    )

                    // Gender Selection
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Gender", style = MaterialTheme.typography.labelLarge)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = uiState.editedGender == "Male", onClick = { viewModel.onGenderChange("Male") })
                            Text("Male")
                            Spacer(Modifier.width(16.dp))
                            RadioButton(selected = uiState.editedGender == "Female", onClick = { viewModel.onGenderChange("Female") })
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
                            value = uiState.editedGoal,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Goal") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = goalExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = goalExpanded, onDismissRequest = { goalExpanded = false }) {
                            goals.forEach { goal ->
                                DropdownMenuItem(text = { Text(goal) }, onClick = { viewModel.onGoalChange(goal); goalExpanded = false })
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
                            value = uiState.editedLevel,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("English Level") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = levelExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = levelExpanded, onDismissRequest = { levelExpanded = false }) {
                            levels.forEach { level ->
                                DropdownMenuItem(text = { Text(level) }, onClick = { viewModel.onLevelChange(level); levelExpanded = false })
                            }
                        }
                    }

                    Button(
                        onClick = { viewModel.updateProfile() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Update Profile")
                    }
                } else {
                    // --- VIEW MODE ---
                    val profile = uiState.profile
                    InfoItem(label = "Full Name", value = profile?.fullName ?: "N/A")
                    InfoItem(label = "Email", value = profile?.email ?: "N/A")
                    InfoItem(label = "Date of Birth", value = profile?.dateOfBirth ?: "N/A")
                    InfoItem(label = "Gender", value = profile?.gender ?: "N/A")
                    InfoItem(label = "Goal", value = profile?.goal ?: "N/A")
                    InfoItem(label = "English Level", value = profile?.level ?: "N/A")

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { showDeleteDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Delete Account")
                    }
                }
            }

            if (uiState.error != null) {
                Text(uiState.error!!, color = MaterialTheme.colorScheme.error)
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete this account?") },
            confirmButton = {
                TextButton(onClick = { 
                    viewModel.deleteAccount()
                    showDeleteDialog = false 
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun InfoItem(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
        HorizontalDivider(modifier = Modifier.padding(top = 4.dp), thickness = 0.5.dp, color = Color.LightGray)
    }
}
