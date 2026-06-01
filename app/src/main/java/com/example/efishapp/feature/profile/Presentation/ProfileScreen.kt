package com.example.efishapp.feature.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

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
            // Avatar Placeholder (Static as per request)
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
                        enabled = false // Email usually read-only for security
                    )

                    OutlinedTextField(
                        value = uiState.editedDateOfBirth,
                        onValueChange = { viewModel.onDateOfBirthChange(it) },
                        label = { Text("Date of Birth") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Gender Selection
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Gender", style = MaterialTheme.typography.labelLarge)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = uiState.editedGender == "Nam", onClick = { viewModel.onGenderChange("Nam") })
                            Text("Nam")
                            Spacer(Modifier.width(16.dp))
                            RadioButton(selected = uiState.editedGender == "Nữ", onClick = { viewModel.onGenderChange("Nữ") })
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
            title = { Text("Xác nhận xóa") },
            text = { Text("Bạn có chắc chắn muốn xóa tài khoản này không?") },
            confirmButton = {
                TextButton(onClick = { 
                    viewModel.deleteAccount()
                    showDeleteDialog = false 
                }) {
                    Text("Xóa", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Hủy")
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
