package com.example.efishapp.feature.vocabulary.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.efishapp.feature.vocabulary.domain.model.Vocabulary

@Composable
fun CreateEditVocabularyDialog(
    vocabulary: Vocabulary? = null,
    folderColor: Color,
    onDismiss: () -> Unit,
    onConfirm: (word: String, phonetic: String, meaning: String, example: String, exampleMeaning: String) -> Unit
) {
    var word by remember { mutableStateOf(vocabulary?.word ?: "") }
    var phonetic by remember { mutableStateOf(vocabulary?.phonetic ?: "") }
    var meaning by remember { mutableStateOf(vocabulary?.meaning ?: "") }
    var example by remember { mutableStateOf(vocabulary?.example ?: "") }
    var exampleMeaning by remember { mutableStateOf(vocabulary?.exampleMeaning ?: "") }

    val isEditing = vocabulary != null

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .border(
                    width = 1.dp,
                    color = Color(0xFFE0E0E0),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(24.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEditing) "Chỉnh sửa từ vựng" else "Thêm từ vựng mới",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF1A1A2E),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF1A1A2E)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                VocabularyTextField(
                    value = word,
                    onValueChange = { word = it },
                    label = "Từ *",
                    placeholder = "Nhập từ vựng",
                    folderColor = folderColor
                )

                Spacer(modifier = Modifier.height(12.dp))

                VocabularyTextField(
                    value = phonetic,
                    onValueChange = { phonetic = it },
                    label = "Phiên âm",
                    placeholder = "/prəˌnʌnsiˈeɪʃn/",
                    folderColor = folderColor
                )

                Spacer(modifier = Modifier.height(12.dp))

                VocabularyTextField(
                    value = meaning,
                    onValueChange = { meaning = it },
                    label = "Nghĩa *",
                    placeholder = "Nhập nghĩa của từ",
                    folderColor = folderColor
                )

                Spacer(modifier = Modifier.height(12.dp))

                VocabularyTextField(
                    value = example,
                    onValueChange = { example = it },
                    label = "Ví dụ",
                    placeholder = "Enter an example sentence",
                    singleLine = false,
                    maxLines = 2,
                    keyboardCapitalization = KeyboardCapitalization.Sentences,
                    folderColor = folderColor
                )

                Spacer(modifier = Modifier.height(12.dp))

                VocabularyTextField(
                    value = exampleMeaning,
                    onValueChange = { exampleMeaning = it },
                    label = "Nghĩa ví dụ",
                    placeholder = "Dịch nghĩa ví dụ",
                    singleLine = true,
                    folderColor = folderColor
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF1A1A2E)
                        )
                    ) {
                        Text("Hủy")
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = {
                            if (word.isNotBlank() && meaning.isNotBlank()) {
                                onConfirm(word, phonetic, meaning, example, exampleMeaning)
                            }
                        },
                        enabled = word.isNotBlank() && meaning.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = folderColor
                        )
                    ) {
                        Text(if (isEditing) "Lưu" else "Thêm", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun VocabularyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    folderColor: Color,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    singleLine: Boolean = true,
    maxLines: Int = 1,
    keyboardCapitalization: KeyboardCapitalization = KeyboardCapitalization.Sentences
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF1A1A2E).copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        androidx.compose.material3.OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .border(
                    width = 1.dp,
                    color = Color(0xFFE0E0E0),
                    shape = RoundedCornerShape(12.dp)
                ),
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color(0xFF1A1A2E).copy(alpha = 0.4f)
                )
            },
            singleLine = singleLine,
            maxLines = maxLines,
            keyboardOptions = KeyboardOptions(
                capitalization = keyboardCapitalization
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(0xFF1A1A2E),
                unfocusedTextColor = Color(0xFF1A1A2E),
                cursorColor = folderColor,
                focusedBorderColor = folderColor,
                unfocusedBorderColor = Color(0xFFE0E0E0)
            )
        )
    }
}
