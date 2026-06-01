package com.example.efishapp.feature.vocabulary.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.efishapp.feature.vocabulary.domain.model.Vocabulary
import com.example.efishapp.feature.folder.presentation.theme.GlassBackground

@Composable
fun VocabularyCard(
    vocabulary: Vocabulary,
    folderColor: Color,
    onClick: () -> Unit = {},
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleLearned: () -> Unit,
    isSelectionMode: Boolean = false,
    isSelected: Boolean = false,
    onToggleSelection: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = if (isSelected) folderColor.copy(alpha = 0.1f) else Color.White
                )
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) folderColor else Color(0xFFE0E0E0),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isSelected -> folderColor.copy(alpha = 0.15f)
                                vocabulary.isLearned -> Color(0xFF4CAF50).copy(alpha = 0.15f)
                                else -> folderColor.copy(alpha = 0.15f)
                            }
                        )
                        .border(
                            width = 2.dp,
                            color = when {
                                isSelected -> folderColor
                                vocabulary.isLearned -> Color(0xFF4CAF50)
                                else -> folderColor
                            },
                            shape = CircleShape
                        )
                        .clickable {
                            if (isSelectionMode && onToggleSelection != null) {
                                onToggleSelection()
                            } else {
                                onToggleLearned()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when {
                            isSelected -> Icons.Default.Check
                            vocabulary.isLearned -> Icons.Default.Check
                            else -> Icons.Default.Edit
                        },
                        contentDescription = when {
                            isSelected -> "Đã chọn"
                            vocabulary.isLearned -> "Đã học"
                            else -> "Chưa học"
                        },
                        tint = when {
                            isSelected -> folderColor
                            vocabulary.isLearned -> Color(0xFF4CAF50)
                            else -> folderColor
                        },
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = vocabulary.word,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A2E)
                    )

                    if (vocabulary.phonetic.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = vocabulary.phonetic,
                            style = MaterialTheme.typography.bodySmall,
                            fontStyle = FontStyle.Italic,
                            color = Color(0xFF1A1A2E).copy(alpha = 0.6f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = vocabulary.meaning,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF1A1A2E).copy(alpha = 0.9f)
                    )

                    if (vocabulary.example.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "\"${vocabulary.example}\"",
                            style = MaterialTheme.typography.bodySmall,
                            fontStyle = FontStyle.Italic,
                            color = Color(0xFF1A1A2E).copy(alpha = 0.6f)
                        )
                        if (vocabulary.exampleMeaning.isNotBlank()) {
                            Text(
                                text = "= ${vocabulary.exampleMeaning}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF1A1A2E).copy(alpha = 0.5f)
                            )
                        }
                    }
                }

                if (!isSelectionMode) {
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Menu",
                                tint = Color(0xFF1A1A2E).copy(alpha = 0.5f),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Chỉnh sửa") },
                                onClick = {
                                    showMenu = false
                                    onEdit()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Edit, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Xóa") },
                                onClick = {
                                    showMenu = false
                                    onDelete()
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = Color.Red
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
