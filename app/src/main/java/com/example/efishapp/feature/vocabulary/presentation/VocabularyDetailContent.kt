package com.example.efishapp.feature.vocabulary.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.efishapp.feature.vocabulary.domain.model.Vocabulary
import java.util.Locale

@Composable
fun VocabularyDetailContent(
    uiState: VocabularyDetailUiState,
    folderColor: Color,
    isSpeaking: Boolean,
    isSpeakingExample: Boolean,
    onSpeakWord: (String) -> Unit,
    onSpeakExample: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onToggleLearned: () -> Unit
) {
    val vocabulary = uiState.vocabulary

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = folderColor)
                }
            }
            vocabulary != null -> {
                VocabularyDetailBody(
                    vocabulary = vocabulary,
                    folderColor = folderColor,
                    isSpeaking = isSpeaking,
                    isSpeakingExample = isSpeakingExample,
                    onSpeakWord = onSpeakWord,
                    onSpeakExample = onSpeakExample,
                    onNavigateBack = onNavigateBack,
                    onToggleLearned = onToggleLearned
                )
            }
            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.error ?: "Lỗi",
                        color = Color.Red
                    )
                }
            }
        }
    }
}

@Composable
private fun VocabularyDetailBody(
    vocabulary: Vocabulary,
    folderColor: Color,
    isSpeaking: Boolean,
    isSpeakingExample: Boolean,
    onSpeakWord: (String) -> Unit,
    onSpeakExample: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onToggleLearned: () -> Unit
) {
    val scrollState = rememberScrollState()
    val scale by animateFloatAsState(
        targetValue = if (isSpeaking) 1.1f else 1f,
        animationSpec = tween(200),
        label = "scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            folderColor,
                            folderColor.copy(alpha = 0.8f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .scale(scale)
                        .clip(CircleShape)
                        .size(120.dp)
                        .background(Color.White.copy(alpha = 0.2f))
                        .clickable { onSpeakWord(vocabulary.word) },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Phát âm",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                        if (isSpeaking) {
                            Text(
                                text = "...",
                                color = Color.White,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = vocabulary.word,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                if (vocabulary.phonetic.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = vocabulary.phonetic,
                        style = MaterialTheme.typography.titleMedium,
                        fontStyle = FontStyle.Italic,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            IconButton(
                onClick = onToggleLearned,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (vocabulary.isLearned) Color(0xFF4CAF50)
                        else Color.White.copy(alpha = 0.3f)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = if (vocabulary.isLearned) "Đã học" else "Chưa học",
                    tint = Color.White
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            DetailSection(
                title = "Nghĩa",
                icon = "📖",
                content = vocabulary.meaning,
                folderColor = folderColor
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (vocabulary.example.isNotBlank()) {
                DetailSection(
                    title = "Ví dụ",
                    icon = "💬",
                    content = vocabulary.example,
                    subContent = vocabulary.exampleMeaning,
                    folderColor = folderColor,
                    isExample = true,
                    isSpeaking = isSpeakingExample,
                    onSpeakExample = { onSpeakExample(vocabulary.example) }
                )

                Spacer(modifier = Modifier.height(20.dp))
            }

            if (vocabulary.imageUrl.isNotBlank()) {
                DetailSection(
                    title = "Hình ảnh",
                    icon = "🖼️",
                    imageUrl = vocabulary.imageUrl,
                    folderColor = folderColor
                )

                Spacer(modifier = Modifier.height(20.dp))
            }

            if (vocabulary.audioUrl.isNotBlank()) {
                DetailSection(
                    title = "Audio",
                    icon = "🔊",
                    content = vocabulary.audioUrl,
                    folderColor = folderColor
                )

                Spacer(modifier = Modifier.height(20.dp))
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF5F5F5)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Thông tin",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1A1A2E)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    InfoRow("Trạng thái", if (vocabulary.isLearned) "Đã học" else "Chưa học")
                    InfoRow("Ngày tạo", formatDate(vocabulary.createdAt))
                    InfoRow("Cập nhật", formatDate(vocabulary.updatedAt))
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
private fun DetailSection(
    title: String,
    icon: String,
    content: String? = null,
    subContent: String? = null,
    imageUrl: String? = null,
    folderColor: Color,
    isExample: Boolean = false,
    isSpeaking: Boolean = false,
    onSpeakExample: (() -> Unit)? = null
) {
    val scale by animateFloatAsState(
        targetValue = if (isSpeaking) 1.05f else 1f,
        animationSpec = tween(200),
        label = "scale"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = icon,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = folderColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isExample && onSpeakExample != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(scale)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSpeaking) folderColor.copy(alpha = 0.2f)
                            else folderColor.copy(alpha = 0.1f)
                        )
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(folderColor)
                            .clickable { onSpeakExample() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isSpeaking) Icons.Default.VolumeUp else Icons.Default.PlayArrow,
                            contentDescription = "Phát âm",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = content ?: "",
                            style = MaterialTheme.typography.bodyLarge,
                            fontStyle = FontStyle.Italic,
                            color = Color(0xFF1A1A2E)
                        )
                        if (!subContent.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = subContent,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF1A1A2E).copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            } else if (imageUrl != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF5F5F5)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Image: $imageUrl",
                        color = Color(0xFF1A1A2E).copy(alpha = 0.5f)
                    )
                }
            } else if (content != null) {
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF1A1A2E)
                )
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF1A1A2E).copy(alpha = 0.6f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF1A1A2E)
        )
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(java.util.Date(timestamp))
}
