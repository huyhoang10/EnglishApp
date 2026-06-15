package com.example.efishapp.feature.folder.presentation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.efishapp.R
import com.example.efishapp.feature.folder.domain.model.Folder
import com.example.efishapp.feature.folder.presentation.component.ImportExportDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderScreen(
    viewModel: FolderViewModel = hiltViewModel(),
    onNavigateToFolderDetail: (String, String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showFilterMenu by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }
    var showImportExportDialog by remember { mutableStateOf(false) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { viewModel.onEvent(FolderUiEvent.OnFileSelected(it)) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.folder_myFolder)) },
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
                                                FolderFilterOption.ALL -> stringResource(R.string.folder_filterAll)
                                                FolderFilterOption.STARRED -> stringResource(R.string.folder_filterPinned)
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
                                                FolderSortOption.NEWEST -> stringResource(R.string.folder_sortNewest)
                                                FolderSortOption.OLDEST -> stringResource(R.string.folder_sortOldest)
                                                FolderSortOption.ALPHABETICAL -> stringResource(R.string.folder_sortFromAtoZ)
                                                FolderSortOption.STARRED -> stringResource(R.string.folder_sortPinned)
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
                onClick = { showImportExportDialog = true }
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
                            stringResource(R.string.folder_noFolderPinned)
                        else
                            stringResource(R.string.folder_NoFolder),
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
                        onClick = { onNavigateToFolderDetail(folder.id, folder.name) },
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

    // Import / Export Dialog
    if (showImportExportDialog) {
        ImportExportDialog(
            onDismiss = { showImportExportDialog = false },
            onCreateManually = {
                showImportExportDialog = false
                viewModel.onEvent(FolderUiEvent.OpenCreateDialog)
            },
            onImportFile = {
                showImportExportDialog = false
                viewModel.onEvent(FolderUiEvent.OpenImportDialog)
                filePickerLauncher.launch(arrayOf("text/plain"))
            }
        )
    }

    // Import Preview Dialog
    if (uiState.importDialog.isOpen) {
        ImportPreviewDialog(
            state = uiState.importDialog,
            onDismiss = {
                viewModel.onEvent(FolderUiEvent.CloseImportDialog)
            },
            onConfirm = {
                viewModel.onEvent(FolderUiEvent.ConfirmImport)
            }
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
            Text(if (state.isEditMode) stringResource(R.string.folder_editFolderDialog) else stringResource(
                R.string.folder_createFolderDialog
            ))
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = state.name,
                    onValueChange = onNameChange,
                    label = { Text(stringResource(R.string.folder_nameFolder)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Text(
                    text = stringResource(R.string.folder_colorFolder),
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
                Text(if (state.isEditMode) stringResource(R.string.folder_saveChange) else stringResource(
                    R.string.folder_create
                ))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.folder_cancel)) }
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
        title = { Text(stringResource(R.string.folder_deleteFolderTile)) },
        text = {
            Text(stringResource(R.string.folder_contentDeleteDialog, folderName))
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(stringResource(R.string.folder_deleteButton))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.folder_cancelButton)) }
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

@Composable
fun ImportPreviewDialog(
    state: ImportDialogState,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            if (!state.isProcessing) onDismiss()
        },
        title = {
            if (state.importResult != null) {
                Text(if (state.importResult.success) stringResource(R.string.folder_importSuccess) else stringResource(
                    R.string.folder_errorImport
                ))
            } else {
                Text(stringResource(R.string.folder_importVocab))
            }
        },
        text = {
            when {
                state.isProcessing -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(stringResource(R.string.folder_handingImport))
                    }
                }
                state.importResult != null -> {
                    Column {
                        Text(state.importResult.message)
                        if (state.importResult.success) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                stringResource(
                                    R.string.foder_countVocabImported,
                                    state.importResult.importedCount
                                ))
                            if (state.importResult.skippedCount > 0) {
                                Text(
                                    stringResource(
                                        R.string.folder_countVocabIgnore,
                                        state.importResult.skippedCount
                                    ))
                            }
                        }
                    }
                }
                state.selectedUri != null && state.previewVocabularies.isNotEmpty() -> {
                    Column {
                        Text(
                            text = stringResource(
                                R.string.folder_nameFolderSelected,
                                state.fileName.ifEmpty { stringResource(R.string.folder_newFolder) }),
                            style = MaterialTheme.typography.titleSmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(
                                R.string.folder_countVocabFound,
                                state.previewVocabularies.size
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(8.dp))

                        val displayList = state.previewVocabularies.take(5)
                        displayList.forEach { vocab ->
                            Text(
                                text = "• ${vocab.word}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        if (state.previewVocabularies.size > 5) {
                            Text(
                                text = stringResource(
                                    R.string.folder_previewVocab,
                                    state.previewVocabularies.size - 5
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                state.selectedUri != null && state.previewVocabularies.isEmpty() -> {
                    Text(stringResource(R.string.folder_remindErrorImport))
                }
                else -> {
                    Text(stringResource(R.string.folder_reminderImportTemplate))
                }
            }
        },
        confirmButton = {
            when {
                state.importResult != null -> {
                    Button(onClick = onDismiss) {
                        Text(stringResource(R.string.folder_closeButton))
                    }
                }
                state.previewVocabularies.isNotEmpty() && !state.isProcessing -> {
                    Button(onClick = onConfirm) {
                        Text(stringResource(R.string.folder_importButton))
                    }
                }
            }
        },
        dismissButton = {
            if (state.importResult == null && !state.isProcessing) {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.folder_cancelButton2))
                }
            }
        }
    )
}
