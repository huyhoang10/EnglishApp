package com.example.efishapp.feature.folder.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.efishapp.feature.folder.domain.model.Folder
import com.example.efishapp.feature.folder.domain.repository.FolderRepository
import com.example.efishapp.feature.folder.domain.usecase.CreateFolder
import com.example.efishapp.feature.folder.domain.usecase.DeleteFolder
import com.example.efishapp.feature.folder.domain.usecase.GetFolder
import com.example.efishapp.feature.folder.domain.usecase.UpdateFolder
import com.example.efishapp.feature.folder.presentation.components.CreateEditFolderDialog
import com.example.efishapp.feature.folder.presentation.components.FolderCard
import com.example.efishapp.feature.vocabulary.domain.repository.VocabularyRepository

@Composable
fun FolderScreen(
    folderRepository: FolderRepository,
    vocabularyRepository: VocabularyRepository,
    onFolderClick: (Folder) -> Unit
) {
    val viewModel = remember {
        FolderViewModel(
            getFolder = GetFolder(folderRepository),
            createFolder = CreateFolder(folderRepository),
            updateFolderUseCase = UpdateFolder(folderRepository),
            deleteFolderUseCase = DeleteFolder(folderRepository, vocabularyRepository)
        )
    }

    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSuccessMessage()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Scaffold(
            containerColor = Color.White,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButton = {
                if (uiState.isSelectionMode && uiState.selectedFolderIds.isNotEmpty()) {
                    FloatingActionButton(
                        onClick = { viewModel.deleteSelectedFolders() },
                        containerColor = Color.Red.copy(alpha = 0.8f),
                        contentColor = Color.White
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Xóa đã chọn"
                        )
                    }
                } else if (!uiState.isSelectionMode) {
                    FloatingActionButton(
                        onClick = { viewModel.showCreateDialog() },
                        containerColor = Color(0xFF4C58BA),
                        contentColor = Color.White
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Tạo thư mục"
                        )
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (uiState.isSelectionMode) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp, start = 8.dp, end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { viewModel.toggleSelectionMode() }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Hủy",
                                tint = Color(0xFF1A1A2E)
                            )
                        }

                        Text(
                            text = "${uiState.selectedFolderIds.size} đã chọn",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1A1A2E),
                            modifier = Modifier.weight(1f)
                        )

                        TextButton(onClick = { viewModel.selectAll() }) {
                            Text("Chọn tất cả", color = Color(0xFF4C58BA))
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp, start = 16.dp, end = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Thư mục của tôi",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A2E),
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(onClick = { viewModel.toggleSelectionMode() }) {
                            Icon(
                                imageVector = Icons.Default.SelectAll,
                                contentDescription = "Chọn nhiều",
                                tint = Color(0xFF1A1A2E)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Tổ chức từ vựng theo chủ đề",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF1A1A2E).copy(alpha = 0.7f),
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }

                when {
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF4C58BA))
                        }
                    }
                    uiState.folders.isEmpty() -> {
                        EmptyFolderView()
                    }
                    else -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = uiState.folders,
                                key = { it.id }
                            ) { folder ->
                                FolderCard(
                                    folder = folder,
                                    onClick = { onFolderClick(folder) },
                                    onEdit = { viewModel.showEditDialog(folder) },
                                    onDelete = { viewModel.showDeleteConfirmation(folder) },
                                    isSelectionMode = uiState.isSelectionMode,
                                    isSelected = uiState.selectedFolderIds.contains(folder.id),
                                    onToggleSelection = { viewModel.toggleFolderSelection(folder.id) }
                                )
                            }
                        }
                    }
                }
            }
        }

        if (uiState.showCreateDialog) {
            CreateEditFolderDialog(
                folder = uiState.folderToEdit,
                onDismiss = { viewModel.hideDialog() },
                onConfirm = { name, description, topic, colorHex ->
                    if (uiState.folderToEdit != null) {
                        viewModel.updateFolder(name, description, topic, colorHex)
                    } else {
                        viewModel.createFolder(name, description, topic, colorHex)
                    }
                    viewModel.hideDialog()
                }
            )
        }

        if (uiState.showDeleteConfirmation && uiState.folderToDelete != null) {
            AlertDialog(
                onDismissRequest = { viewModel.hideDeleteConfirmation() },
                title = { Text("Xóa thư mục", color = Color(0xFF1A1A2E)) },
                text = {
                    Text(
                        "Bạn có chắc muốn xóa \"${uiState.folderToDelete?.name}\"? Tất cả từ vựng trong thư mục sẽ bị xóa.",
                        color = Color(0xFF1A1A2E).copy(alpha = 0.8f)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.deleteFolder() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Xóa")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.hideDeleteConfirmation() }) {
                        Text("Hủy", color = Color(0xFF1A1A2E))
                    }
                },
                containerColor = Color.White
            )
        }
    }
}

@Composable
fun EmptyFolderView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "📁",
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Chưa có thư mục nào",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1A1A2E)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Tạo thư mục đầu tiên để bắt đầu\nhọc từ vựng theo chủ đề",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF1A1A2E).copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}
