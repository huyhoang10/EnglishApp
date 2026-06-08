package com.example.efishapp.feature.vocabulary.presentation.component

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.efishapp.core.util.OnDeviceTTSHelper
import com.example.efishapp.feature.vocabulary.domain.model.Vocabulary

@Composable
fun VocabularyItem(
    vocabulary: Vocabulary,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isTtsReady by remember { mutableStateOf(false) }
    val ttsHelper = remember {
        object : OnDeviceTTSHelper(context) {
            override fun onInit(status: Int) {
                if (status == android.speech.tts.TextToSpeech.SUCCESS) {
                    isTtsReady = true
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { ttsHelper.shutdown() }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = vocabulary.word,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (vocabulary.pronunciation.isNotBlank()) {
                        Text(
                            text = vocabulary.pronunciation,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }

                IconButton(
                    onClick = {
                        if (!isTtsReady) {
                            Toast.makeText(context, "Đang khởi tạo giọng đọc...", Toast.LENGTH_SHORT).show()
                            return@IconButton
                        }
                        if (!ttsHelper.isReady) {
                            Toast.makeText(context, "Giọng đọc không khả dụng trên thiết bị này", Toast.LENGTH_SHORT).show()
                            return@IconButton
                        }
                        ttsHelper.speak(vocabulary.word)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Phát âm",
                        tint = if (isTtsReady) MaterialTheme.colorScheme.primary
                               else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Chỉnh sửa",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Xóa",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            if (vocabulary.meaning.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = vocabulary.meaning,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (vocabulary.example.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "\"${vocabulary.example}\"",
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
