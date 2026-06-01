package com.example.efishapp.feature.folder.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.efishapp.feature.folder.domain.model.Folder
import com.example.efishapp.feature.folder.domain.model.Topic
import com.example.efishapp.feature.folder.presentation.theme.GlassBackground
import com.example.efishapp.feature.folder.presentation.theme.GlassBorder
import com.example.efishapp.feature.folder.presentation.theme.TopicColors

@Composable
fun CreateEditFolderDialog(
    folder: Folder? = null,
    onDismiss: () -> Unit,
    onConfirm: (name: String, description: String, topicType: Topic, colorHex: Long) -> Unit
) {
    var name by remember { mutableStateOf(folder?.name ?: "") }
    var description by remember { mutableStateOf(folder?.description ?: "") }
    var selectedTopic by remember { mutableStateOf(folder?.topicType ?: Topic.CUSTOM) }
    var selectedColor by remember { mutableLongStateOf(folder?.colorHex ?: TopicColors.first().value.toLong()) }

    val isEditing = folder != null
    val scrollState = rememberScrollState()
    val configuration = LocalConfiguration.current
    val maxHeight = configuration.screenHeightDp.dp * 0.85f

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = maxHeight)
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    color = Color.White
                )
                .border(
                    width = 1.dp,
                    color = Color(0xFFE0E0E0),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(24.dp)
        ) {
            Column (
                modifier = Modifier.verticalScroll(scrollState)
            ){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEditing) "Chỉnh sửa thư mục" else "Tạo thư mục mới",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF1A1A2E)
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

                GlassTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Tên thư mục",
                    placeholder = "Nhập tên thư mục"
                )

                Spacer(modifier = Modifier.height(16.dp))

                GlassTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Mô tả (tùy chọn)",
                    placeholder = "Nhập mô tả",
                    singleLine = false,
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(20.dp))

                TopicSelector(
                    selectedTopic = selectedTopic,
                    onTopicSelected = { topic ->
                        selectedTopic = topic
                        selectedColor = topic.colorHex
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                ColorSelector(
                    selectedColorHex = selectedColor,
                    onColorSelected = { selectedColor = it }
                )

                Spacer(modifier = Modifier.height(28.dp))

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
                            if (name.isNotBlank()) {
                                onConfirm(name, description, selectedTopic, selectedColor)
                            }
                        },
                        enabled = name.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(selectedColor)
                        )
                    ) {
                        Text(if (isEditing) "Lưu" else "Tạo", color = Color.White)
                    }
                }
            }
        }
    }
}
