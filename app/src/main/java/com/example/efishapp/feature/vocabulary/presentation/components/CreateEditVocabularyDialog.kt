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
import com.example.efishapp.feature.folder.presentation.theme.GlassBackground
import com.example.efishapp.feature.folder.presentation.theme.GlassBorder

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
                .background(Color(0xFF2D2D44).copy(alpha = 0.95f))
                .border(
                    width = 1.dp,
                    color = GlassBorder,
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
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                VocabularyTextField(
                    value = word,
                    onValueChange = { word = it },
                    label = "Từ *",
                    placeholder = "Nhập từ vựng"
                )

                Spacer(modifier = Modifier.height(12.dp))

                VocabularyTextField(
                    value = phonetic,
                    onValueChange = { phonetic = it },
                    label = "Phiên âm",
                    placeholder = "/prəˌnʌnsiˈeɪʃn/"
                )

                Spacer(modifier = Modifier.height(12.dp))

                VocabularyTextField(
                    value = meaning,
                    onValueChange = { meaning = it },
                    label = "Nghĩa *",
                    placeholder = "Nhập nghĩa của từ"
                )

                Spacer(modifier = Modifier.height(12.dp))

                VocabularyTextField(
                    value = example,
                    onValueChange = { example = it },
                    label = "Ví dụ",
                    placeholder = "Enter an example sentence",
                    singleLine = false,
                    maxLines = 2,
                    keyboardCapitalization = KeyboardCapitalization.Sentences
                )

                Spacer(modifier = Modifier.height(12.dp))

                VocabularyTextField(
                    value = exampleMeaning,
                    onValueChange = { exampleMeaning = it },
                    label = "Nghĩa ví dụ",
                    placeholder = "Dịch nghĩa ví dụ",
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
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
                        Text(if (isEditing) "Lưu" else "Thêm")
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
            color = Color.White.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        androidx.compose.material3.OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(GlassBackground)
                .border(
                    width = 1.dp,
                    color = GlassBorder,
                    shape = RoundedCornerShape(12.dp)
                ),
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color.White.copy(alpha = 0.4f)
                )
            },
            singleLine = singleLine,
            maxLines = maxLines,
            keyboardOptions = KeyboardOptions(
                capitalization = keyboardCapitalization
            ),
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White,
                focusedBorderColor = Color.White.copy(alpha = 0.5f),
                unfocusedBorderColor = Color.Transparent
            )
        )
    }
}
