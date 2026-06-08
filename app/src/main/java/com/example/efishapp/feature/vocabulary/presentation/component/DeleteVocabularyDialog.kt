package com.example.efishapp.feature.vocabulary.presentation.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.example.efishapp.feature.vocabulary.presentation.DeleteVocabularyDialogState

@Composable
fun DeleteVocabularyDialog(
    state: DeleteVocabularyDialogState,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (!state.isOpen) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Xóa từ vựng") },
        text = {
            Text("Bạn có chắc chắn muốn xóa từ \"${state.vocabularyWord}\" không?")
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
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}
