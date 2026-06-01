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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
fun VocabularyScreen(
    folderName: String,
    folderColor: Long,
    onNavigateBack: () -> Unit,
    onVocabularyClick: (Vocabulary) -> Unit = {},
    viewModel: VocabularyViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val folderColorValue = Color(folderColor)

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSuccessMessage()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButton = {
                if (uiState.isSelectionMode && uiState.selectedVocabularyIds.isNotEmpty()) {
                    FloatingActionButton(
                        onClick = { viewModel.deleteSelectedVocabularies() },
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
                        onClick = { viewModel.showCreateVocabularyDialog() },
                        containerColor = folderColorValue,
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
                            text = "${uiState.selectedVocabularyIds.size} đã chọn",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1A1A2E),
                            modifier = Modifier.weight(1f)
                        )

                        TextButton(onClick = { viewModel.selectAll() }) {
                            Text("Chọn tất cả", color = folderColorValue)
                        }
                    }
                } else {
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
                                text = "${uiState.vocabularies.size} từ vựng",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF1A1A2E).copy(alpha = 0.7f)
                            )
                        }

                        IconButton(onClick = { viewModel.toggleSelectionMode() }) {
                            Icon(
                                imageVector = Icons.Default.SelectAll,
                                contentDescription = "Chọn nhiều",
                                tint = Color(0xFF1A1A2E)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    VocabularySearchBar(
                        query = uiState.searchQuery,
                        onQueryChange = { viewModel.updateSearchQuery(it) },
                        folderColor = folderColorValue,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                when {
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = folderColorValue)
                        }
                    }
                    viewModel.getFilteredVocabularies().isEmpty() -> {
                        EmptyVocabularyView(
                            hasSearchQuery = uiState.searchQuery.isNotBlank(),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = viewModel.getFilteredVocabularies(),
                                key = { it.id }
                            ) { vocabulary ->
                                VocabularyCard(
                                    vocabulary = vocabulary,
                                    folderColor = folderColorValue,
                                    onClick = { onVocabularyClick(vocabulary) },
                                    onEdit = { viewModel.showEditVocabularyDialog(vocabulary) },
                                    onDelete = { viewModel.showDeleteConfirmation(vocabulary) },
                                    onToggleLearned = { viewModel.toggleVocabularyLearned(vocabulary) },
                                    isSelectionMode = uiState.isSelectionMode,
                                    isSelected = uiState.selectedVocabularyIds.contains(vocabulary.id),
                                    onToggleSelection = { viewModel.toggleVocabularySelection(vocabulary.id) }
                                )
                            }
                            if (!uiState.isSelectionMode) {
                                item {
                                    Spacer(modifier = Modifier.height(80.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        if (uiState.showVocabularyDialog) {
            CreateEditVocabularyDialog(
                vocabulary = uiState.vocabularyToEdit,
                folderColor = folderColorValue,
                onDismiss = { viewModel.hideVocabularyDialog() },
                onConfirm = { word, phonetic, meaning, example, exampleMeaning ->
                    if (uiState.vocabularyToEdit != null) {
                        viewModel.updateVocabulary(word, phonetic, meaning, example, exampleMeaning)
                    } else {
                        viewModel.createVocabulary(word, phonetic, meaning, example, exampleMeaning)
                    }
                    viewModel.hideVocabularyDialog()
                }
            )
        }

        if (uiState.showDeleteConfirmation && uiState.vocabularyToDelete != null) {
            AlertDialog(
                onDismissRequest = { viewModel.hideDeleteConfirmation() },
                title = { Text("Xóa từ vựng", color = Color(0xFF1A1A2E)) },
                text = {
                    Text(
                        "Bạn có chắc muốn xóa từ \"${uiState.vocabularyToDelete?.word}\"?",
                        color = Color(0xFF1A1A2E).copy(alpha = 0.8f)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.deleteVocabulary() },
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

        // Dialog thông báo thành công
        uiState.successMessage?.let { message ->
            AlertDialog(
                onDismissRequest = { viewModel.clearSuccessMessage() },
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
                        onClick = { viewModel.clearSuccessMessage() },
                        colors = ButtonDefaults.buttonColors(containerColor = folderColorValue)
                    ) {
                        Text("OK")
                    }
                },
                containerColor = Color.White
            )
        }

        // Dialog thông báo lỗi
        uiState.error?.let { error ->
            AlertDialog(
                onDismissRequest = { viewModel.clearError() },
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
                        onClick = { viewModel.clearError() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.clearError() }) {
                        Text("Hủy", color = Color(0xFF1A1A2E))
                    }
                },
                containerColor = Color.White
            )
        }
    }
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
