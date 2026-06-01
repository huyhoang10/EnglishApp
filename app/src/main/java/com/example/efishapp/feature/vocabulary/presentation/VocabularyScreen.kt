package com.example.efishapp.feature.vocabulary.presentation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun VocabularyScreen(
    folderName: String,
    folderColor: Long,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (vocabularyId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VocabularyViewModel = hiltViewModel()
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

    VocabularyContent(
        uiState = uiState,
        folderName = folderName,
        folderColor = folderColorValue,
        onNavigateBack = onNavigateBack,
        onNavigateToDetail = onNavigateToDetail,
        onShowCreateDialog = viewModel::showCreateVocabularyDialog,
        onShowEditDialog = viewModel::showEditVocabularyDialog,
        onHideDialog = viewModel::hideVocabularyDialog,
        onCreateVocabulary = viewModel::createVocabulary,
        onUpdateVocabulary = viewModel::updateVocabulary,
        onShowDeleteConfirmation = viewModel::showDeleteConfirmation,
        onHideDeleteConfirmation = viewModel::hideDeleteConfirmation,
        onDeleteVocabulary = viewModel::deleteVocabulary,
        onToggleLearned = viewModel::toggleVocabularyLearned,
        onToggleSelectionMode = viewModel::toggleSelectionMode,
        onToggleSelection = viewModel::toggleVocabularySelection,
        onSelectAll = viewModel::selectAll,
        onDeleteSelectedVocabularies = viewModel::deleteSelectedVocabularies,
        onUpdateSearchQuery = viewModel::updateSearchQuery,
        onClearError = viewModel::clearError,
        onClearSuccessMessage = viewModel::clearSuccessMessage,
        getFilteredVocabularies = viewModel::getFilteredVocabularies,
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}
