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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.efishapp.feature.folder.domain.model.Folder
import com.example.efishapp.feature.folder.presentation.components.CreateEditFolderDialog
import com.example.efishapp.feature.folder.presentation.components.FolderCard

@Composable
fun FolderContent(
    uiState: FolderUiState,
    onNavigateToVocabulary: (folderId: String, folderName: String, folderColor: Long) -> Unit,
    onShowCreateDialog: () -> Unit,
    onShowEditDialog: (Folder) -> Unit,
    onHideDialog: () -> Unit,
    onCreateFolder: (name: String, description: String, topic: com.example.efishapp.feature.folder.domain.model.Topic, colorHex: Long) -> Unit,
    onUpdateFolder: (name: String, description: String, topic: com.example.efishapp.feature.folder.domain.model.Topic, colorHex: Long) -> Unit,
    onShowDeleteConfirmation: (Folder) -> Unit,
    onHideDeleteConfirmation: () -> Unit,
    onDeleteFolder: () -> Unit,
    onToggleSelectionMode: () -> Unit,
    onToggleFolderSelection: (String) -> Unit,
    onSelectAll: () -> Unit,
    onDeleteSelectedFolders: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
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
                        onClick = onDeleteSelectedFolders,
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
                        onClick = onShowCreateDialog,
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
                    SelectionModeHeader(
                        selectedCount = uiState.selectedFolderIds.size,
                        onCancel = onToggleSelectionMode,
                        onSelectAll = onSelectAll
                    )
                } else {
                    NormalHeader(onToggleSelectionMode = onToggleSelectionMode)
                }

                when {
                    uiState.isLoading -> {
                        LoadingView()
                    }
                    uiState.folders.isEmpty() -> {
                        EmptyFolderView()
                    }
                    else -> {
                        FolderGrid(
                            folders = uiState.folders,
                            isSelectionMode = uiState.isSelectionMode,
                            selectedFolderIds = uiState.selectedFolderIds,
                            onFolderClick = { folder ->
                                onNavigateToVocabulary(folder.id, folder.name, folder.colorHex)
                            },
                            onEdit = onShowEditDialog,
                            onDelete = onShowDeleteConfirmation,
                            onToggleSelection = onToggleFolderSelection
                        )
                    }
                }
            }
        }

        if (uiState.showCreateDialog) {
            CreateEditFolderDialog(
                folder = uiState.folderToEdit,
                onDismiss = onHideDialog,
                onConfirm = { name, description, topic, colorHex ->
                    if (uiState.folderToEdit != null) {
                        onUpdateFolder(name, description, topic, colorHex)
                    } else {
                        onCreateFolder(name, description, topic, colorHex)
                    }
                    onHideDialog()
                }
            )
        }

        if (uiState.showDeleteConfirmation && uiState.folderToDelete != null) {
            DeleteConfirmationDialog(
                folderName = uiState.folderToDelete?.name ?: "",
                onConfirm = onDeleteFolder,
                onDismiss = onHideDeleteConfirmation
            )
        }
    }
}

@Composable
private fun SelectionModeHeader(
    selectedCount: Int,
    onCancel: () -> Unit,
    onSelectAll: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp, start = 8.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onCancel) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Hủy",
                tint = Color(0xFF1A1A2E)
            )
        }

        Text(
            text = "$selectedCount đã chọn",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1A1A2E),
            modifier = Modifier.weight(1f)
        )

        TextButton(onClick = onSelectAll) {
            Text("Chọn tất cả", color = Color(0xFF4C58BA))
        }
    }
}

@Composable
private fun NormalHeader(onToggleSelectionMode: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp, start = 16.dp, end = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Thư mục của tôi",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A2E),
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = onToggleSelectionMode) {
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
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Color(0xFF4C58BA))
    }
}

@Composable
private fun FolderGrid(
    folders: List<Folder>,
    isSelectionMode: Boolean,
    selectedFolderIds: Set<String>,
    onFolderClick: (Folder) -> Unit,
    onEdit: (Folder) -> Unit,
    onDelete: (Folder) -> Unit,
    onToggleSelection: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = folders,
            key = { it.id }
        ) { folder ->
            FolderCard(
                folder = folder,
                onClick = { onFolderClick(folder) },
                onEdit = { onEdit(folder) },
                onDelete = { onDelete(folder) },
                isSelectionMode = isSelectionMode,
                isSelected = selectedFolderIds.contains(folder.id),
                onToggleSelection = { onToggleSelection(folder.id) }
            )
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(
    folderName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Xóa thư mục", color = Color(0xFF1A1A2E)) },
        text = {
            Text(
                "Bạn có chắc muốn xóa \"$folderName\"? Tất cả từ vựng trong thư mục sẽ bị xóa.",
                color = Color(0xFF1A1A2E).copy(alpha = 0.8f)
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Xóa")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy", color = Color(0xFF1A1A2E))
            }
        },
        containerColor = Color.White
    )
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
