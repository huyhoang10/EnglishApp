package com.example.efishapp.feature.folder.presentation

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import com.example.efishapp.feature.folder.presentation.theme.GradientEnd
import com.example.efishapp.feature.folder.presentation.theme.GradientStart
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(GradientStart, GradientEnd)
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { viewModel.showCreateDialog() },
                    containerColor = Color.White.copy(alpha = 0.2f),
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Tạo thư mục"
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Spacer(modifier = Modifier.height(48.dp))

                Text(
                    text = "Thư mục của tôi",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Tổ chức từ vựng theo chủ đề",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                when {
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }
                    uiState.folders.isEmpty() -> {
                        EmptyFolderView()
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
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
                                    onDelete = { viewModel.showDeleteConfirmation(folder) }
                                )
                            }
                            item {
                                Spacer(modifier = Modifier.height(80.dp))
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
                }
            )
        }

        if (uiState.showDeleteConfirmation && uiState.folderToDelete != null) {
            AlertDialog(
                onDismissRequest = { viewModel.hideDeleteConfirmation() },
                title = { Text("Xóa thư mục", color = Color.White) },
                text = {
                    Text(
                        "Bạn có chắc muốn xóa \"${uiState.folderToDelete?.name}\"? Tất cả từ vựng trong thư mục sẽ bị xóa.",
                        color = Color.White.copy(alpha = 0.8f)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.deleteFolder() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f))
                    ) {
                        Text("Xóa")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.hideDeleteConfirmation() }) {
                        Text("Hủy", color = Color.White)
                    }
                },
                containerColor = Color(0xFF2D2D44)
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
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Tạo thư mục đầu tiên để bắt đầu\nhọc từ vựng theo chủ đề",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}
