package com.example.efishapp.feature.vocabulary.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.efishapp.feature.vocabulary.domain.model.Vocabulary
import com.example.efishapp.feature.vocabulary.presentation.components.CreateEditVocabularyDialog
import com.example.efishapp.feature.vocabulary.presentation.components.VocabularyCard

@Composable
fun VocabularyContent(
    uiState: VocabularyUiState,
    folderName: String,
    folderColor: Color,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onShowCreateDialog: () -> Unit,
    onShowEditDialog: (Vocabulary) -> Unit,
    onHideDialog: () -> Unit,
    onCreateVocabulary: (String, String, String, String, String) -> Unit,
    onUpdateVocabulary: (String, String, String, String, String) -> Unit,
    onShowDeleteConfirmation: (Vocabulary) -> Unit,
    onHideDeleteConfirmation: () -> Unit,
    onDeleteVocabulary: () -> Unit,
    onToggleLearned: (Vocabulary) -> Unit,
    onToggleSelectionMode: () -> Unit,
    onToggleSelection: (String) -> Unit,
    onSelectAll: () -> Unit,
    onDeleteSelectedVocabularies: () -> Unit,
    onUpdateSearchQuery: (String) -> Unit,
    onClearError: () -> Unit,
    onClearSuccessMessage: () -> Unit,
    getFilteredVocabularies: () -> List<Vocabulary>,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButton = {
                if (uiState.isSelectionMode && uiState.selectedVocabularyIds.isNotEmpty()) {
                    FloatingActionButton(
                        onClick = onDeleteSelectedVocabularies,
                        containerColor = Color.Red,
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
                        containerColor = folderColor,
                        contentColor = Color.White
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Thêm từ vựng"
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
                        selectedCount = uiState.selectedVocabularyIds.size,
                        onCancel = onToggleSelectionMode,
                        onSelectAll = onSelectAll
                    )
                } else {
                    NormalHeader(
                        folderName = folderName,
                        vocabularyCount = uiState.vocabularies.size,
                        folderColor = folderColor,
                        onNavigateBack = onNavigateBack,
                        onToggleSelectionMode = onToggleSelectionMode,
                        onUpdateSearchQuery = onUpdateSearchQuery,
                        searchQuery = uiState.searchQuery
                    )
                }

                when {
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = folderColor)
                        }
                    }
                    getFilteredVocabularies().isEmpty() -> {
                        EmptyVocabularyView(
                            hasSearchQuery = uiState.searchQuery.isNotBlank(),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    else -> {
                        VocabularyList(
                            vocabularies = getFilteredVocabularies(),
                            folderColor = folderColor,
                            isSelectionMode = uiState.isSelectionMode,
                            selectedVocabularyIds = uiState.selectedVocabularyIds,
                            onVocabularyClick = onNavigateToDetail,
                            onEdit = onShowEditDialog,
                            onDelete = onShowDeleteConfirmation,
                            onToggleLearned = onToggleLearned,
                            onToggleSelection = onToggleSelection
                        )
                    }
                }
            }
        }

        if (uiState.showVocabularyDialog) {
            CreateEditVocabularyDialog(
                vocabulary = uiState.vocabularyToEdit,
                folderColor = folderColor,
                onDismiss = onHideDialog,
                onConfirm = { word, phonetic, meaning, example, exampleMeaning ->
                    if (uiState.vocabularyToEdit != null) {
                        onUpdateVocabulary(word, phonetic, meaning, example, exampleMeaning)
                    } else {
                        onCreateVocabulary(word, phonetic, meaning, example, exampleMeaning)
                    }
                    onHideDialog()
                }
            )
        }

        if (uiState.showDeleteConfirmation && uiState.vocabularyToDelete != null) {
            DeleteConfirmationDialog(
                vocabularyWord = uiState.vocabularyToDelete?.word ?: "",
                onConfirm = onDeleteVocabulary,
                onDismiss = onHideDeleteConfirmation
            )
        }

        uiState.successMessage?.let { message ->
            SuccessDialog(
                message = message,
                folderColor = folderColor,
                onDismiss = onClearSuccessMessage
            )
        }

        uiState.error?.let { error ->
            ErrorDialog(
                error = error,
                onDismiss = onClearError
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
private fun NormalHeader(
    folderName: String,
    vocabularyCount: Int,
    folderColor: Color,
    onNavigateBack: () -> Unit,
    onToggleSelectionMode: () -> Unit,
    onUpdateSearchQuery: (String) -> Unit,
    searchQuery: String
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 8.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Quay lại",
                    tint = Color(0xFF1A1A2E)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = folderName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A2E)
                )
                Text(
                    text = "$vocabularyCount từ vựng",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF1A1A2E).copy(alpha = 0.7f)
                )
            }

            IconButton(onClick = onToggleSelectionMode) {
                Icon(
                    imageVector = Icons.Default.SelectAll,
                    contentDescription = "Chọn nhiều",
                    tint = Color(0xFF1A1A2E)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        VocabularySearchBar(
            query = searchQuery,
            onQueryChange = onUpdateSearchQuery,
            folderColor = folderColor,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun VocabularyList(
    vocabularies: List<Vocabulary>,
    folderColor: Color,
    isSelectionMode: Boolean,
    selectedVocabularyIds: Set<String>,
    onVocabularyClick: (String) -> Unit,
    onEdit: (Vocabulary) -> Unit,
    onDelete: (Vocabulary) -> Unit,
    onToggleLearned: (Vocabulary) -> Unit,
    onToggleSelection: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = vocabularies,
            key = { it.id }
        ) { vocabulary ->
            VocabularyCard(
                vocabulary = vocabulary,
                folderColor = folderColor,
                onClick = { onVocabularyClick(vocabulary.id) },
                onEdit = { onEdit(vocabulary) },
                onDelete = { onDelete(vocabulary) },
                onToggleLearned = { onToggleLearned(vocabulary) },
                isSelectionMode = isSelectionMode,
                isSelected = selectedVocabularyIds.contains(vocabulary.id),
                onToggleSelection = { onToggleSelection(vocabulary.id) }
            )
        }
        if (!isSelectionMode) {
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(
    vocabularyWord: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Xóa từ vựng", color = Color(0xFF1A1A2E)) },
        text = {
            Text(
                "Bạn có chắc muốn xóa từ \"$vocabularyWord\"?",
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
private fun SuccessDialog(
    message: String,
    folderColor: Color,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                "Thành công",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Text(
                message,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = folderColor)
            ) {
                Text("OK")
            }
        },
        containerColor = Color.White
    )
}

@Composable
private fun ErrorDialog(
    error: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = null,
                tint = Color.Red,
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                "Lỗi",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Text(
                error,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("OK")
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
fun VocabularySearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    folderColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(
                width = 1.dp,
                color = Color(0xFFE0E0E0),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color(0xFF1A1A2E).copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.padding(horizontal = 12.dp))

            androidx.compose.foundation.text.BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.weight(1f),
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF1A1A2E)),
                decorationBox = { innerTextField ->
                    if (query.isEmpty()) {
                        Text(
                            text = "Tìm kiếm từ vựng...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF1A1A2E).copy(alpha = 0.4f)
                        )
                    }
                    innerTextField()
                }
            )

            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = Color(0xFF1A1A2E).copy(alpha = 0.5f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyVocabularyView(
    hasSearchQuery: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (hasSearchQuery) "🔍" else "📝",
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (hasSearchQuery) "Không tìm thấy từ nào" else "Chưa có từ vựng nào",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1A1A2E)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (hasSearchQuery) "Thử từ khóa khác"
            else "Bắt đầu thêm từ vựng đầu tiên\nvào thư mục này",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF1A1A2E).copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}
