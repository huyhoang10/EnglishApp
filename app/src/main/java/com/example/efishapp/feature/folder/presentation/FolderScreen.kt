package com.example.efishapp.feature.folder.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.efishapp.feature.folder.domain.model.Folder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderScreen(
    viewModel: FolderViewModel = hiltViewModel(),
    onNavigateToFolderDetail: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showFilterMenu by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thư mục của tôi") },
                actions = {
                    Box {
                        IconButton(onClick = { showFilterMenu = true }) {
                            Icon(Icons.Default.FilterList, contentDescription = "Lọc")
                        }
                        DropdownMenu(
                            expanded = showFilterMenu,
                            onDismissRequest = { showFilterMenu = false }
                        ) {
                            FolderFilterOption.entries.forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            when (option) {
                                                FolderFilterOption.ALL -> "Tất cả"
                                                FolderFilterOption.STARRED -> "Đã ghim"
                                            }
                                        )
                                    },
                                    onClick = {
                                        viewModel.onEvent(FolderUiEvent.SetFilter(option))
                                        showFilterMenu = false
                                    },
                                    leadingIcon = {
                                        if (uiState.filterOption == option) {
                                            Icon(
                                                Icons.Default.FilterList,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }

                    Box {
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(Icons.Default.Sort, contentDescription = "Sắp xếp")
                        }
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            FolderSortOption.entries.forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            when (option) {
                                                FolderSortOption.NEWEST -> "Mới nhất"
                                                FolderSortOption.OLDEST -> "Cũ nhất"
                                                FolderSortOption.ALPHABETICAL -> "A - Z"
                                                FolderSortOption.STARRED -> "Đã ghim"
                                            }
                                        )
                                    },
                                    onClick = {
                                        viewModel.onEvent(FolderUiEvent.SetSort(option))
                                        showSortMenu = false
                                    },
                                    leadingIcon = {
                                        if (uiState.sortOption == option) {
                                            Icon(
                                                Icons.Default.Sort,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            )
        },

        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(bottom = 60.dp),
                onClick = { viewModel.onEvent(FolderUiEvent.OpenCreateDialog) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tạo thư mục mới")
            }
        },

        floatingActionButtonPosition = FabPosition.End

    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            val folders = uiState.displayedFolders

            if (folders.isEmpty() && !uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (uiState.filterOption == FolderFilterOption.STARRED)
                            "Chưa có thư mục nào được ghim"
                        else
                            "Chưa có thư mục nào",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    top = 16.dp,
                    end = 16.dp,
                    bottom = 100.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(folders, key = { it.id }) { folder ->
                    FolderItem(
                        folder = folder,
                        onClick = { onNavigateToFolderDetail(folder.id) },
                        onEdit = { viewModel.onEvent(FolderUiEvent.OpenEditDialog(folder)) },
                        onDelete = { viewModel.onEvent(FolderUiEvent.OpenDeleteDialog(folder)) },
                        onToggleStar = { viewModel.onEvent(FolderUiEvent.ToggleStar(folder)) }
                    )
                }
            }
        }
    }

    // Create / Edit Dialog
    if (uiState.createDialog.isOpen) {
        FolderCreateDialog(
            state = uiState.createDialog,
            onNameChange = { viewModel.onEvent(FolderUiEvent.OnDialogNameChange(it)) },
            onColorChange = { viewModel.onEvent(FolderUiEvent.OnDialogColorChange(it)) },
            onDismiss = { viewModel.onEvent(FolderUiEvent.CloseCreateDialog) },
            onConfirm = { viewModel.onEvent(FolderUiEvent.ConfirmCreateOrUpdate) }
        )
    }

    // Delete Confirmation Dialog
    if (uiState.deleteDialog.isOpen) {
        FolderDeleteDialog(
            folderName = uiState.deleteDialog.folderName,
            onDismiss = { viewModel.onEvent(FolderUiEvent.CloseDeleteDialog) },
            onConfirm = { viewModel.onEvent(FolderUiEvent.ConfirmDelete) }
        )
    }

    // Error Snackbar
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            viewModel.onEvent(FolderUiEvent.ClearError)
        }
    }
}

@Composable
fun FolderItem(
    folder: Folder,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleStar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = folder.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
            }

            IconButton(onClick = onToggleStar) {
                Icon(
                    imageVector = if (folder.isStarred) Icons.Filled.Star else Icons.Outlined.StarOutline,
                    contentDescription = null,
                    tint = if (folder.isStarred) MaterialTheme.colorScheme.primary else LocalContentColor.current
                )
            }

            IconButton(onClick = onEdit) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Chỉnh sửa",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Xóa",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun FolderCreateDialog(
    state: CreateDialogState,
    onNameChange: (String) -> Unit,
    onColorChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val colorOptions = listOf(
        "#4CAF50", "#2196F3", "#FF9800",
        "#E91E63", "#9C27B0", "#00BCD4",
        "#F44336", "#795548"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (state.isEditMode) "Chỉnh sửa thư mục" else "Tạo thư mục mới")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = state.name,
                    onValueChange = onNameChange,
                    label = { Text("Tên thư mục") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Text(
                    text = "Màu sắc",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    colorOptions.forEach { color ->
                        FilterChip(
                            selected = state.color == color,
                            onClick = { onColorChange(color) },
                            label = { },
                            modifier = Modifier.size(32.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor(color))
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(if (state.isEditMode) "Lưu thay đổi" else "Tạo")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
}

@Composable
fun FolderDeleteDialog(
    folderName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Xóa thư mục") },
        text = {
            Text("Bạn có chắc chắn muốn xóa thư mục \"$folderName\" không? Hành động này không thể hoàn tác.")
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Xóa")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun FolderItemPreview() {
    MaterialTheme {
        FolderItem(
            folder = Folder(
                id = "1",
                name = "English Vocabulary",
                isStarred = true
            ),
            onClick = {},
            onDelete = {},
            onEdit = {},
            onToggleStar = {}
        )
    }
}
