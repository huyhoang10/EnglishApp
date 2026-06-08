package com.example.efishapp.feature.vocabulary.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.efishapp.feature.vocabulary.presentation.EditVocabularyDialogState

@Composable
fun EditVocabularyDialog(
    state: EditVocabularyDialogState,
    onWordChange: (String) -> Unit,
    onMeaningChange: (String) -> Unit,
    onPronunciationChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onExampleChange: (String) -> Unit,
    onRelatedWordChange: (String) -> Unit,
    onCollocationChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (!state.isOpen) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Chỉnh sửa từ vựng") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = state.word,
                    onValueChange = onWordChange,
                    label = { Text("Từ vựng *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                OutlinedTextField(
                    value = state.pronunciation,
                    onValueChange = onPronunciationChange,
                    label = { Text("Phiên âm IPA") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                OutlinedTextField(
                    value = state.meaning,
                    onValueChange = onMeaningChange,
                    label = { Text("Nghĩa") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                OutlinedTextField(
                    value = state.example,
                    onValueChange = onExampleChange,
                    label = { Text("Ví dụ") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                OutlinedTextField(
                    value = state.description,
                    onValueChange = onDescriptionChange,
                    label = { Text("Mô tả") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                OutlinedTextField(
                    value = state.relatedWord,
                    onValueChange = onRelatedWordChange,
                    label = { Text("Từ liên quan") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                OutlinedTextField(
                    value = state.collocation,
                    onValueChange = onCollocationChange,
                    label = { Text("Cụm từ / Collocation") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                OutlinedTextField(
                    value = state.note,
                    onValueChange = onNoteChange,
                    label = { Text("Ghi chú") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Lưu")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}
