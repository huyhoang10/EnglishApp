package com.example.efishapp.feature.folder.presentation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun FolderScreen(
    onNavigateToVocabulary: (folderId: String, folderName: String, folderColor: Long) -> Unit,
    viewModel: FolderViewModel = hiltViewModel()
) {
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

    FolderContent(
        uiState = uiState,
        onNavigateToVocabulary = onNavigateToVocabulary,
        onShowCreateDialog = viewModel::showCreateDialog,
        onShowEditDialog = viewModel::showEditDialog,
        onHideDialog = viewModel::hideDialog,
        onCreateFolder = viewModel::createFolder,
        onUpdateFolder = viewModel::updateFolder,
        onShowDeleteConfirmation = viewModel::showDeleteConfirmation,
        onHideDeleteConfirmation = viewModel::hideDeleteConfirmation,
        onDeleteFolder = viewModel::deleteFolder,
        onToggleSelectionMode = viewModel::toggleSelectionMode,
        onToggleFolderSelection = viewModel::toggleFolderSelection,
        onSelectAll = viewModel::selectAll,
        onDeleteSelectedFolders = viewModel::deleteSelectedFolders,
        snackbarHostState = snackbarHostState
    )
}
