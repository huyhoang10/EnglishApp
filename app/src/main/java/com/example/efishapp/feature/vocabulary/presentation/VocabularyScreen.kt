package com.example.efishapp.feature.vocabulary.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.efishapp.feature.vocabulary.domain.model.Vocabulary
import com.example.efishapp.feature.vocabulary.presentation.component.AddVocabularyDialog
import com.example.efishapp.feature.vocabulary.presentation.component.DeleteVocabularyDialog
import com.example.efishapp.feature.vocabulary.presentation.component.EditVocabularyDialog
import com.example.efishapp.feature.vocabulary.presentation.component.ExportVocabularyDialog
import com.example.efishapp.feature.vocabulary.presentation.component.PronunciationSection
import com.example.efishapp.feature.vocabulary.presentation.component.VocabularyItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyScreen(
    onNavigateBack: () -> Unit,
    viewModel: VocabularyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var selectedVocabulary by remember { mutableStateOf<Vocabulary?>(null) }
    var showExportDialog by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it)
                viewModel.onEvent(VocabularyUiEvent.ClearError)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(viewModel.folderName, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lai")
                    }
                },
                actions = {
                    if (uiState.vocabularies.isNotEmpty()) {
                        IconButton(onClick = { showExportDialog = true }) {
                            Icon(Icons.Default.Share, contentDescription = "Export từ vựng")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(VocabularyUiEvent.OpenAddDialog) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Thêm từ vựng")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.isLoading && uiState.vocabularies.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.vocabularies.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Chưa có từ vựng nào",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Nhấn nút + để thêm từ vựng mới",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.vocabularies, key = { it.id }) { vocabulary ->
                        VocabularyItem(
                            vocabulary = vocabulary,
                            onClick = { selectedVocabulary = vocabulary },
                            onEdit = { viewModel.onEvent(VocabularyUiEvent.OpenEditDialog(vocabulary)) },
                            onDelete = { viewModel.onEvent(VocabularyUiEvent.OpenDeleteDialog(vocabulary)) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(72.dp)) }
                }
            }
        }
    }

    if (selectedVocabulary != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedVocabulary = null },
            sheetState = sheetState
        ) {
            VocabularyDetailSheet(
                vocabulary = selectedVocabulary!!,
                onDismiss = { selectedVocabulary = null }
            )
        }
    }

    val addDialog = uiState.addDialog
    if (addDialog.isOpen) {
        AddVocabularyDialog(
            state = addDialog,
            onWordChange = { viewModel.onEvent(VocabularyUiEvent.OnWordChange(it)) },
            onMeaningChange = { viewModel.onEvent(VocabularyUiEvent.OnMeaningChange(it)) },
            onPronunciationChange = { viewModel.onEvent(VocabularyUiEvent.OnPronunciationChange(it)) },
            onDescriptionChange = { viewModel.onEvent(VocabularyUiEvent.OnDescriptionChange(it)) },
            onExampleChange = { viewModel.onEvent(VocabularyUiEvent.OnExampleChange(it)) },
            onRelatedWordChange = { viewModel.onEvent(VocabularyUiEvent.OnRelatedWordChange(it)) },
            onCollocationChange = { viewModel.onEvent(VocabularyUiEvent.OnCollocationChange(it)) },
            onNoteChange = { viewModel.onEvent(VocabularyUiEvent.OnNoteChange(it)) },
            onDismiss = { viewModel.onEvent(VocabularyUiEvent.CloseAddDialog) },
            onConfirm = { viewModel.onEvent(VocabularyUiEvent.ConfirmAdd) }
        )
    }

    val editDialog = uiState.editDialog
    if (editDialog.isOpen) {
        EditVocabularyDialog(
            state = editDialog,
            onWordChange = { viewModel.onEvent(VocabularyUiEvent.OnEditWordChange(it)) },
            onMeaningChange = { viewModel.onEvent(VocabularyUiEvent.OnEditMeaningChange(it)) },
            onPronunciationChange = { viewModel.onEvent(VocabularyUiEvent.OnEditPronunciationChange(it)) },
            onDescriptionChange = { viewModel.onEvent(VocabularyUiEvent.OnEditDescriptionChange(it)) },
            onExampleChange = { viewModel.onEvent(VocabularyUiEvent.OnEditExampleChange(it)) },
            onRelatedWordChange = { viewModel.onEvent(VocabularyUiEvent.OnEditRelatedWordChange(it)) },
            onCollocationChange = { viewModel.onEvent(VocabularyUiEvent.OnEditCollocationChange(it)) },
            onNoteChange = { viewModel.onEvent(VocabularyUiEvent.OnEditNoteChange(it)) },
            onDismiss = { viewModel.onEvent(VocabularyUiEvent.CloseEditDialog) },
            onConfirm = { viewModel.onEvent(VocabularyUiEvent.ConfirmEdit) }
        )
    }

    if (uiState.deleteDialog.isOpen) {
        DeleteVocabularyDialog(
            state = uiState.deleteDialog,
            onDismiss = { viewModel.onEvent(VocabularyUiEvent.CloseDeleteDialog) },
            onConfirm = { viewModel.onEvent(VocabularyUiEvent.ConfirmDelete) }
        )
    }

    if (showExportDialog) {
        ExportVocabularyDialog(
            folderName = viewModel.folderName,
            vocabularies = uiState.vocabularies,
            onDismiss = { showExportDialog = false }
        )
    }
}

@Composable
private fun VocabularyDetailSheet(
    vocabulary: Vocabulary,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = vocabulary.word,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        PronunciationSection(
            word = vocabulary.word,
            pronunciation = vocabulary.pronunciation
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (vocabulary.meaning.isNotBlank()) {
            DetailSection(title = "Nghĩa của từ", content = vocabulary.meaning)
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (vocabulary.description.isNotBlank()) {
            DetailSection(title = "Mô tả", content = vocabulary.description)
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (vocabulary.example.isNotBlank()) {
            DetailSection(title = "Ví dụ", content = "\"${vocabulary.example}\"", italic = true)
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (vocabulary.relatedWord.isNotBlank()) {
            DetailSection(title = "Từ liên quan", content = vocabulary.relatedWord)
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (vocabulary.collocation.isNotBlank()) {
            DetailSection(title = "Collocation", content = vocabulary.collocation)
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (vocabulary.note.isNotBlank()) {
            DetailSection(title = "Ghi chú", content = vocabulary.note)
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun DetailSection(
    title: String,
    content: String,
    italic: Boolean = false
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal
        )
    }
}
